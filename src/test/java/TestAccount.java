import controller.dao.AccountDAO;
import model.bank.BankAccount;
import model.enums.AccUsrStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAccount {
    static C3P0Test instance;
    static AccountDAO dao;
    static BankAccount account1;
    static long account1ID;
    static BankAccount account2;
    static long account2ID;
    static BankAccount account3;
    static long account3ID;

    @BeforeAll
    static void init() {
        instance = C3P0Test.getInstance();
        dao = new AccountDAO(instance.getConnection());
        // user ID`s -> 15, 27

        account1 = new BankAccount();
        account1ID = account1.getAccountID();
        insertAccount(account1);
        account2 = new BankAccount();
        account2ID = account2.getAccountID();
        insertAccount(account2);
        account3 = new BankAccount();
        account3ID = account3.getAccountID();
        insertAccount(account3);
    }

    @Test
    @Order(1)
    public void testCreateNewAccount1() {
        AccountDAO dao = new AccountDAO(instance.getConnection());
        List<BankAccount> accounts = dao.createNewAccount(15);
        assertEquals(1, accounts.size());
        account1 = accounts.get(0);
    }

    @Test
    @Order(2)
    public void testCreateNewAccount2() {
        AccountDAO dao = new AccountDAO(instance.getConnection());
        List<BankAccount> accounts = dao.createNewAccount(15);
        assertEquals(2, accounts.size());
    }

    @Test
    @Order(3)
    public void testGetAccount1() {
        BankAccount actual = dao.getAccount(String.valueOf(account1ID));
        assertEquals(account1.toString(), actual.toString());
    }

    @Test
    @Order(4)
    public void testGetAccount2() {
        BankAccount actual = dao.getAccount(String.valueOf(account2ID));
        assertEquals(account2.toString(), actual.toString());
    }

    @Test
    @Order(4)
    public void testFundAccount1() {
        double fundingSum = 30.17;
        account1.funding(fundingSum);
        dao.fundAccount(fundingSum, String.valueOf(account1ID));
        BankAccount actual = dao.getAccount(String.valueOf(account1ID));
        assertEquals(account1.toString(), actual.toString());
    }

    @Test
    @Order(5)
    public void testFundAccount2() {
        double fundingSum = 23.09;
        account2.funding(fundingSum);
        dao.fundAccount(fundingSum, String.valueOf(account2ID));
        BankAccount actual = dao.getAccount(String.valueOf(account2ID));
        assertEquals(account2.toString(), actual.toString());
    }

    @Test
    @Order(6)
    public void testBlockAccount() {
        dao.blockAccount(String.valueOf(account3ID));
        BankAccount actual = dao.getAccount(String.valueOf(account3ID));
        assertEquals(AccUsrStatus.BLOCKED, actual.getStatus());
    }

    @Test
    @Order(7)
    public void testChangeAccountStatus1() {
        dao.changeAccountStatus(String.valueOf(account1ID), true);
        BankAccount actual = dao.getAccount(String.valueOf(account1ID));
        assertEquals(AccUsrStatus.BLOCKED, actual.getStatus());
    }

    @Test
    @Order(8)
    public void testChangeAccountStatus2() {
        dao.changeAccountStatus(String.valueOf(account1ID), false);
        BankAccount actual = dao.getAccount(String.valueOf(account1ID));
        assertEquals(AccUsrStatus.ACTIVE, actual.getStatus());
    }

    @Test
    @Order(9)
    public void testUnblockAccountRequest() {
        // should define this method later, when complete all requests tests
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
        String DELETE_QUERY = "DELETE FROM BankAccount";
        Connection connection = instance.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_QUERY);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
