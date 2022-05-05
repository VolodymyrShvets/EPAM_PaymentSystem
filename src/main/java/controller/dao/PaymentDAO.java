package controller.dao;

import model.bank.BankAccount;
import model.bank.Payment;
import model.enums.AccUsrStatus;
import model.enums.PaymentStatus;
import model.util.C3P0DataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    final static Logger logger = LogManager.getLogger(PaymentDAO.class);
    Connection connection;
    private List<Payment> payments = null;
    private List<BankAccount> accounts = null;

    public PaymentDAO() {
        connection = C3P0DataSource.getInstance().getConnection();
    }

    public PaymentDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Payment> getPayments() {
        updatePaymentStatuses(payments);
        return payments;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public String createNewPayment(long userID, String senderId,
                                   String recipientID, String amount, String paymentDate, String cvv2) {
        AccountDAO accountDAO = new AccountDAO(connection);
        BankAccount sender = accountDAO.getAccount(senderId);
        BankAccount recipient = accountDAO.getAccount(recipientID);

        logger.info("Attempt to create New Payment.");
        if (sender.getCard().getMoneyAmount() < Double.parseDouble(amount)) {
            logger.error("Not Enough Money");
            return "Not enough money on account.";
        }

        if (sender.getCard().getCvv2() != Integer.parseInt(cvv2)) {
            logger.error("Wrong CVV2");
            return "Wrong CVV2.";
        }

        try {
            String SQL_GET_SENDER_NAME_QUERY = "SELECT firstName, lastName FROM Users WHERE ID = ?";
            String SQL_GET_RECIPIENT_NAME_QUERY = "SELECT us.firstName, us.lastName, bk.accountStatus FROM Users AS us LEFT JOIN BankAccount AS bk ON bk.userID = us.ID WHERE bk.ID = ?";
            String SQL_INSERT_NEW_PAYMENT_QUERY = "INSERT INTO Payment (paymentStatus, paymentDate, recipientAccID, senderAccID, paymentSum, senderName, recipientName) \nVALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement senderNameStatement = connection.prepareStatement(SQL_GET_SENDER_NAME_QUERY);
            senderNameStatement.setString(1, String.valueOf(userID));

            ResultSet rs = senderNameStatement.executeQuery();
            rs.next();
            String senderName = rs.getString("firstName") + " " + rs.getString("lastName");

            PreparedStatement recipientNameStatement = connection.prepareStatement(SQL_GET_RECIPIENT_NAME_QUERY);
            recipientNameStatement.setObject(1, recipientID);
            rs = recipientNameStatement.executeQuery();
            rs.next();
            String recipientName = rs.getString("firstName") + " " + rs.getString("lastName");
            String recipientAccountStatus = rs.getString("accountStatus");

            if (recipientAccountStatus.equals(AccUsrStatus.BLOCKED.name())) {
                logger.error("Recipient Account is Blocked");
                return "Account you tried to reach is currently blocked.<br>Try again later.";
            }

            Payment payment = new Payment(recipient, LocalDate.parse(paymentDate), recipientName, sender, senderName, Double.parseDouble(amount));

            //if (paymentType.equalsIgnoreCase(PaymentStatus.SENT.name()))
            //    payment = new Payment(PaymentStatus.SENT, recipient, recipientName, sender, senderName, Double.parseDouble(amount));
            //else
            //    payment = new Payment(PaymentStatus.PREPARED, LocalDate.parse(paymentDate), recipient, recipientName, sender, senderName, Double.parseDouble(amount));

            PreparedStatement insertNewPaymentStatement = connection.prepareStatement(SQL_INSERT_NEW_PAYMENT_QUERY);
            insertNewPaymentStatement.setString(1, payment.getStatus().name());
            insertNewPaymentStatement.setDate(2, Date.valueOf(payment.getPaymentDate()));
            insertNewPaymentStatement.setObject(3, payment.getRecipient());
            insertNewPaymentStatement.setObject(4, payment.getSender());
            insertNewPaymentStatement.setDouble(5, payment.getPaymentSum());
            insertNewPaymentStatement.setString(6, payment.getSenderName());
            insertNewPaymentStatement.setString(7, payment.getRecipientName());

            insertNewPaymentStatement.executeUpdate();

            sender.withdrawal(Double.parseDouble(amount));

            //----------------------
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, sender.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, sender.getCard().getCardNumber());

            moneyAmountUpdate.executeUpdate();

            UserDAO userDAO = new UserDAO();
            payments = getAllUserPayments(String.valueOf(userID));
            accounts = userDAO.getAllUserAccounts(String.valueOf(userID));

            updatePaymentStatuses(payments);
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
            return "Something wrong happens.";
        }

        logger.info("New Payment successfully created.");
        return "";
    }

    public List<Payment> getAllUserPayments(String userID) {
        AccountDAO dao = new AccountDAO(connection);
        List<Payment> payments = new ArrayList<>();
        List<BankAccount> accounts = new UserDAO().getAllUserAccounts(userID);

        String SQL_GET_USER_PAYMENTS_QUERY = "SELECT * FROM Payment WHERE recipientAccID IN (";

        String newQuery = createQuery(SQL_GET_USER_PAYMENTS_QUERY, accounts);

        try {
            PreparedStatement statement = connection.prepareStatement(newQuery);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("paymentDate")));
                BankAccount recipient = dao.getAccount(rs.getString("recipientAccID"));
                BankAccount sender = dao.getAccount(rs.getString("senderAccID"));
                Payment payment = new Payment(rs.getLong("ID"), PaymentStatus.valueOf(rs.getString("paymentStatus")), date, recipient, rs.getString("recipientName"), sender, rs.getString("senderName"), rs.getDouble("paymentSum"));
                payments.add(payment);
            }
            updatePaymentStatuses(payments);
        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
        }
        return payments;
    }

    private String createQuery(String SQL_GET_USER_PAYMENTS_QUERY, List<BankAccount> accounts) {
        if (accounts.size() == 0) {
            return (SQL_GET_USER_PAYMENTS_QUERY += "1) OR senderAccID IN (1)");
        }

        for (BankAccount account : accounts) {
            SQL_GET_USER_PAYMENTS_QUERY += account.getAccountID();
            SQL_GET_USER_PAYMENTS_QUERY += ",";
        }

        if (SQL_GET_USER_PAYMENTS_QUERY.endsWith(",")) {
            SQL_GET_USER_PAYMENTS_QUERY = SQL_GET_USER_PAYMENTS_QUERY.substring(0, SQL_GET_USER_PAYMENTS_QUERY.length() - 1);
            SQL_GET_USER_PAYMENTS_QUERY += ") OR senderAccID IN (";
        }

        for (BankAccount account : accounts) {
            SQL_GET_USER_PAYMENTS_QUERY += account.getAccountID();
            SQL_GET_USER_PAYMENTS_QUERY += ",";
        }

        if (SQL_GET_USER_PAYMENTS_QUERY.endsWith(",")) {
            SQL_GET_USER_PAYMENTS_QUERY = SQL_GET_USER_PAYMENTS_QUERY.substring(0, SQL_GET_USER_PAYMENTS_QUERY.length() - 1);
            SQL_GET_USER_PAYMENTS_QUERY += ")";
        }
        return SQL_GET_USER_PAYMENTS_QUERY;
    }

    private void updatePaymentStatuses(List<Payment> payments) {
        List<Payment> paymentsForUpdate = new ArrayList<>();
        for (Payment payment : payments) {
            if (payment.getStatus().equals(PaymentStatus.PREPARED))
                if (payment.getPaymentDate().compareTo(LocalDate.now()) <= 0)
                    paymentsForUpdate.add(payment);
        }

        try {
            String updateStatement1 = "UPDATE Payment SET paymentStatus = ? WHERE recipientAccID = ? OR senderAccID = ?";
            String updateStatement2 = "UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?";

            for (Payment payment : paymentsForUpdate) {
                payment.updateStatus();
                PreparedStatement statement1 = connection.prepareStatement(updateStatement1);

                statement1.setString(1, payment.getStatus().name());
                statement1.setLong(2, payment.getRecipient());
                statement1.setLong(3, payment.getSender());

                BankAccount recipient = new AccountDAO().getAccount(String.valueOf(payment.getRecipient()));
                recipient.funding(payment.getPaymentSum());

                PreparedStatement statement2 = connection.prepareStatement(updateStatement2);
                statement2.setDouble(1, recipient.getCard().getMoneyAmount());
                statement2.setLong(2, recipient.getCard().getCardNumber());

                statement2.executeUpdate();
            }

            paymentsForUpdate.clear();
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
        }
    }
}
