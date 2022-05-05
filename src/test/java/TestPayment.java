import controller.dao.AccountDAO;
import controller.dao.PaymentDAO;
import controller.dao.UserDAO;
import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.enums.PaymentStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPayment {
    static C3P0Test instance;
    static PaymentDAO dao;
    static BankAccount account1;
    static long account1ID;
    static BankAccount account2;
    static long account2ID;
    static User user1;
    static User user2;

    @BeforeAll
    static void init() {
        instance = C3P0Test.getInstance();
        dao = new PaymentDAO(instance.getConnection());

        UserDAO userDAO = new UserDAO(instance.getConnection());
        user1 = userDAO.getUser("TakeshiKovacs");
        user2 = userDAO.getUser("HenryCavill");
        account1 = new BankAccount();
        account1.setUserID(user1.getUserID());
        account1ID = account1.getAccountID();
        insertAccount(account1);
        account2 = new BankAccount();
        account2.setUserID(user2.getUserID());
        account2ID = account2.getAccountID();
        insertAccount(account2);
    }

    @Test
    public void testCreateNewPayment1() {
        double fundingSum = 45.17;
        double paymentSum = 32.53;

        AccountDAO accountDAO = new AccountDAO(instance.getConnection());
        accountDAO.fundAccount(fundingSum, String.valueOf(account1ID));

        BankAccount actual1 = accountDAO.getAccount(String.valueOf(account1ID));
        actual1.funding(fundingSum);
        actual1.withdrawal(paymentSum);
        BankAccount actual2 = accountDAO.getAccount(String.valueOf(account2ID));
        actual2.funding(paymentSum);

        String result = dao.createNewPayment(user1.getUserID(), String.valueOf(account1ID), String.valueOf(account2ID),
                String.valueOf(paymentSum), String.valueOf(LocalDate.now()), String.valueOf(account1.getCard().getCvv2()));

        Payment expected = new Payment(PaymentStatus.SENT, LocalDate.now(),
                actual2, "Henry Cavill", actual1, "Takeshi Kovacs", paymentSum);
        Payment payment = getPayment(String.valueOf(account2ID));
        expected.setPaymentID(payment.getPaymentID());

        assertEquals("", result);
        assertEquals(expected.toString(), payment.toString());
    }

    @Test
    public void testCreateNewPayment2() {
        double fundingSum = 45.17;
        double paymentSum = 190.45;

        AccountDAO accountDAO = new AccountDAO(instance.getConnection());
        accountDAO.fundAccount(fundingSum, String.valueOf(account1ID));

        String result = dao.createNewPayment(user1.getUserID(), String.valueOf(account1ID), String.valueOf(account2ID),
                String.valueOf(paymentSum), String.valueOf(LocalDate.now()), String.valueOf(account1.getCard().getCvv2()));

        assertEquals("Not enough money on account.", result);
    }

    @Test
    public void testCreateNewPayment3() {
        double fundingSum = 45.17;
        double paymentSum = 12.90;

        AccountDAO accountDAO = new AccountDAO(instance.getConnection());
        accountDAO.fundAccount(fundingSum, String.valueOf(account1ID));

        String result = dao.createNewPayment(user1.getUserID(), String.valueOf(account1ID), String.valueOf(account2ID),
                String.valueOf(paymentSum), String.valueOf(LocalDate.now()), String.valueOf(312));

        assertEquals("Wrong CVV2.", result);
    }

    @Test
    public void testCreateNewPayment4() {
        double fundingSum = 45.17;
        double paymentSum = 12.90;

        AccountDAO accountDAO = new AccountDAO(instance.getConnection());
        accountDAO.fundAccount(fundingSum, String.valueOf(account1ID));
        accountDAO.blockAccount(String.valueOf(account2ID));

        String result = dao.createNewPayment(user1.getUserID(), String.valueOf(account1ID), String.valueOf(account2ID),
                String.valueOf(paymentSum), String.valueOf(LocalDate.now()), String.valueOf(account1.getCard().getCvv2()));

        assertEquals("Account you tried to reach is currently blocked.<br>Try again later.", result);
    }

    @Test
    public void testGetAllUserPayments() {
        List<Payment> payments = dao.getAllUserPayments(String.valueOf(user1.getUserID()));
        System.out.println("payments:");
        for (Payment p :
                payments) {
            System.out.println(p);
        }
    }

    public Payment getPayment(String recipientID) {
        Connection connection = instance.getConnection();
        AccountDAO dao = new AccountDAO(connection);
        Payment payment = null;

        String SQL_GET_USER_PAYMENT_QUERY = "SELECT * FROM Payment WHERE recipientAccID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_PAYMENT_QUERY);
            statement.setString(1, recipientID);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("paymentDate")));
                BankAccount recipient = dao.getAccount(rs.getString("recipientAccID"));
                BankAccount sender = dao.getAccount(rs.getString("senderAccID"));
                payment = new Payment(rs.getLong("ID"), PaymentStatus.SENT,//valueOf(rs.getString("paymentStatus")),
                        date, recipient, rs.getString("recipientName"), sender, rs.getString("senderName"), rs.getDouble("paymentSum"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return payment;
    }

    public static void insertAccount(BankAccount account) {
        String INSERT_BANK_ACCOUNT_SQL = "INSERT INTO BankAccount VALUES (?, ?, ?, ?)";
        String INSERT_CARD_SQL = "INSERT INTO CreditCard VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement accountStatement = instance.getConnection().prepareStatement(INSERT_BANK_ACCOUNT_SQL);
            PreparedStatement cardStatement = instance.getConnection().prepareStatement(INSERT_CARD_SQL);

            cardStatement.setObject(1, account.getCard().getCardNumber());
            cardStatement.setInt(2, account.getCard().getCvv2());
            cardStatement.setDate(3, Date.valueOf(account.getCard().getExpirationDate()));
            cardStatement.setDouble(4, account.getCard().getMoneyAmount());

            cardStatement.executeUpdate();

            accountStatement.setObject(1, account.getAccountID());
            accountStatement.setObject(2, account.getUserID());
            accountStatement.setString(3, account.getStatus().name());
            accountStatement.setObject(4, account.getCard().getCardNumber());

            accountStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void clear() {
        String DELETE_QUERY1 = "DELETE FROM Payment";
        String DELETE_QUERY2 = "DELETE FROM BankAccount";
        Connection connection = instance.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_QUERY1);
            statement.executeUpdate();
            statement = connection.prepareStatement(DELETE_QUERY2);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
