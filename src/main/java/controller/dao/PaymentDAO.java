package controller.dao;

import model.bank.BankAccount;
import model.bank.Payment;
import model.enums.AccUsrStatus;
import model.enums.PaymentStatus;
import model.util.SQLConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentDAO {
    final static Logger logger = LogManager.getLogger(PaymentDAO.class);
    private SQLConfig config;
    private List<Payment> payments = null;
    private List<BankAccount> accounts = null;

    public PaymentDAO(SQLConfig config) {
        this.config = config;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public String createNewPayment(long userID, String paymentType, String senderId,
                                   String recipientID, String amount, String paymentDate, String cvv2) {
        BankAccount sender = config.getAccount(senderId);
        BankAccount recipient = config.getAccount(recipientID);

        logger.info("Attempt to create New Payment.");

        if (paymentType == null) {
            logger.error("Payment Type is NULL");
            return "Payment type should be non-null.";
        }

        if (sender.getCard().getMoneyAmount() < Double.parseDouble(amount)) {
            logger.error("Not Enough Money");
            return "Not enough money on account.";
        }

        if (sender.getCard().getCvv2() != Integer.parseInt(cvv2)) {
            logger.error("Wrong CVV2");
            return "Wrong CVV2.";
        }

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");

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

            Payment payment;
            if (paymentType.equalsIgnoreCase(PaymentStatus.SENT.name()))
                payment = new Payment(PaymentStatus.SENT, recipient, recipientName, sender, senderName, Double.parseDouble(amount));
            else
                payment = new Payment(PaymentStatus.PREPARED, LocalDate.parse(paymentDate), recipient, recipientName, sender, senderName, Double.parseDouble(amount));

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
            recipient.funding(Double.parseDouble(amount));

            //----------------------
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, sender.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, sender.getCard().getCardNumber());

            moneyAmountUpdate.executeUpdate();

            //----------------------
            if (payment.getStatus().equals(PaymentStatus.SENT)) {
                moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

                moneyAmountUpdate.setDouble(1, recipient.getCard().getMoneyAmount());
                moneyAmountUpdate.setLong(2, recipient.getCard().getCardNumber());

                moneyAmountUpdate.executeUpdate();
            }
            //----------------------

            payments = config.getAllUserPayments(String.valueOf(userID));
            accounts = config.getAllUserAccounts(String.valueOf(userID));
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Caught Exception:", ex);
            return "Something wrong happens.";
        }

        logger.info("New Payment successfully created.");
        return "";
    }
}
