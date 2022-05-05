package controller.dao;

import model.bank.BankAccount;
import model.bank.CreditCard;
import model.bank.User;
import model.bank.UserRequest;
import model.enums.AccUsrStatus;
import model.enums.RequestType;
import model.util.C3P0DataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class AccountDAO {
    final static Logger logger = LogManager.getLogger(AccountDAO.class);

    Connection connection;

    public AccountDAO() {
        connection = C3P0DataSource.getInstance().getConnection();
    }

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public BankAccount getAccount(String accountID) {
        BankAccount account = null;
        String SQL_GET_USER_ACCOUNTS_QUERY = "SELECT BC.ID, BC.userID, BC.accountStatus,\n" +
                "\tCC.cardNumber, CC.cvv2, CC.expirationDate, CC.moneyAmount\n" +
                "    FROM BankAccount AS BC LEFT JOIN CreditCard AS CC ON BC.card = CC.cardNumber\n" +
                "    WHERE BC.ID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ACCOUNTS_QUERY);
            statement.setInt(1, Integer.parseInt(accountID));

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("expirationDate")));
                account = new BankAccount(rs.getLong("ID"), new CreditCard(rs.getLong("cardNumber"), rs.getInt("cvv2"), date, rs.getDouble("moneyAmount")), AccUsrStatus.valueOf(rs.getString("accountStatus")), rs.getLong("userID"));
            }
        } catch (SQLException e) {
            logger.error("Caught Exception: ", e);
        }
        return account;
    }

    public List<BankAccount> blockAccount(String accountID) {
        BankAccount account = getAccount(accountID);
        List<BankAccount> list = null;

        account.block();

        logger.info("Attempt to Block Account.");

        try {
            PreparedStatement accountStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

            accountStatusUpdate.setString(1, account.getStatus().name());
            accountStatusUpdate.setLong(2, account.getAccountID());

            accountStatusUpdate.executeUpdate();

            list = new UserDAO(connection).getAllUserAccounts(String.valueOf(account.getUserID()));
        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
        }

        logger.info(String.format("Account %s successfully blocked.", accountID));

        return list;
    }

    public List<User> changeAccountStatus(String accountID, boolean operation) {
        List<User> list = null;
        BankAccount account = getAccount(accountID);

        if (operation)
            account.block();
        else
            account.unblock();

        try {
            PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

            userStatusUpdate.setString(1, account.getStatus().name());
            userStatusUpdate.setLong(2, account.getAccountID());

            int result = userStatusUpdate.executeUpdate();

            if (!operation && result == 1) {
                new RequestDAO().deleteAllRequests(accountID, RequestType.ACCOUNT);
            }

            logger.info(String.format("Updated BankAccount status: for ID=%s STATUS=%s", account.getAccountID(), account.getStatus()));

            list = new UserDAO(connection).getAllUsers();
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
        }

        return list;
    }

    public List<BankAccount> createNewAccount(long userID) {
        List<BankAccount> accounts = null;
        BankAccount newAccount = new BankAccount();
        newAccount.setUserID(userID);

        String INSERT_BANK_ACCOUNT_SQL = "INSERT INTO BankAccount VALUES (?, ?, ?, ?)";
        String INSERT_CARD_SQL = "INSERT INTO CreditCard VALUES (?, ?, ?, ?)";

        logger.info("Attempt to create New Account with ID=" + newAccount.getAccountID());

        try {
            PreparedStatement accountStatement = connection.prepareStatement(INSERT_BANK_ACCOUNT_SQL);
            PreparedStatement cardStatement = connection.prepareStatement(INSERT_CARD_SQL);

            cardStatement.setObject(1, newAccount.getCard().getCardNumber());
            cardStatement.setInt(2, newAccount.getCard().getCvv2());
            cardStatement.setDate(3, Date.valueOf(newAccount.getCard().getExpirationDate()));
            cardStatement.setDouble(4, newAccount.getCard().getMoneyAmount());

            cardStatement.executeUpdate();

            accountStatement.setObject(1, newAccount.getAccountID());
            accountStatement.setObject(2, newAccount.getUserID());
            accountStatement.setString(3, newAccount.getStatus().name());
            accountStatement.setObject(4, newAccount.getCard().getCardNumber());

            accountStatement.executeUpdate();

            accounts = new UserDAO(connection).getAllUserAccounts(String.valueOf(userID));
        } catch (SQLException e) {
            logger.error("Caught Exception:", e);
        }

        logger.info(String.format("New Account %s created", newAccount.getAccountID()));

        return accounts;
    }

    public List<BankAccount> fundAccount(double fundSum, String accountID) {
        List<BankAccount> list = null;
        BankAccount account = getAccount(accountID);

        account.funding(fundSum);

        try {
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, account.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, account.getCard().getCardNumber());

            moneyAmountUpdate.executeUpdate();

            list = new UserDAO(connection).getAllUserAccounts(String.valueOf(account.getUserID()));
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
        }
        logger.info(String.format("Account %s successfully funded.", accountID));

        return list;
    }
}
