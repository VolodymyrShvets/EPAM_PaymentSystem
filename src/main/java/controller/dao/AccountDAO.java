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

    public AccountDAO() {
    }

    public BankAccount getAccount(String accountID) {
        BankAccount account = null;
        String SQL_GET_USER_ACCOUNTS_QUERY = "SELECT BC.ID, BC.userID, BC.accountStatus,\n" +
                "\tCC.cardNumber, CC.cvv2, CC.expirationDate, CC.moneyAmount\n" +
                "    FROM BankAccount AS BC LEFT JOIN CreditCard AS CC ON BC.card = CC.cardNumber\n" +
                "    WHERE BC.ID = ?";

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
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
        //account.printAccountState();

        logger.info("Attempt to Block Account.");

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement accountStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

            accountStatusUpdate.setString(1, account.getStatus().name());
            accountStatusUpdate.setLong(2, account.getAccountID());

            accountStatusUpdate.executeUpdate();

            UserDAO userDAO = new UserDAO();
            list = userDAO.getAllUserAccounts(String.valueOf(account.getUserID()));
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

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

            userStatusUpdate.setString(1, account.getStatus().name());
            userStatusUpdate.setLong(2, account.getAccountID());

            int result = userStatusUpdate.executeUpdate();

            if (!operation && result == 1) {
                new RequestDAO().deleteAllRequests(accountID, RequestType.ACCOUNT);
            }

            logger.info(String.format("Updated BankAccount status: for ID=%s STATUS=%s", account.getAccountID(), account.getStatus()));

            list = new UserDAO().getAllUsers();
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
        }

        return list;
    }

    public void unblockAccountRequest(long userID, String accountID) {
        UserRequest request = new UserRequest();
        request.createAccountUnblockingRequest(Long.parseLong(accountID), userID);

        logger.info("Attempt to create new AccountRequest.");

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
            String SQL_INSERT_NEW_REQUEST_QUERY = "INSERT INTO Request (requestType, userID, accountID) VALUES (?, ?, ?)";
            PreparedStatement requestStatement = connection.prepareStatement(SQL_INSERT_NEW_REQUEST_QUERY);
            requestStatement.setString(1, request.getType().name());
            requestStatement.setLong(2, request.getUserID());
            requestStatement.setLong(3, request.getAccountID());

            requestStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
        }
        logger.info("Created new AccountRequest for UserID=" + userID + " and AccountID=" + accountID);
    }

    public List<BankAccount> createNewAccount(long userID) {
        List<BankAccount> accounts = null;
        BankAccount newAccount = new BankAccount();
        newAccount.setUserID(userID);

        String INSERT_BANK_ACCOUNT_SQL = "INSERT INTO BankAccount VALUES (?, ?, ?, ?)";
        String INSERT_CARD_SQL = "INSERT INTO CreditCard VALUES (?, ?, ?, ?)";

        logger.info("Attempt to create New Account with ID=" + newAccount.getAccountID());

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
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

            accounts = new UserDAO().getAllUserAccounts(String.valueOf(userID));
        } catch (SQLException e) {
            logger.error("Caught Exception:", e);
        }

        logger.info(String.format("New Account %s created", newAccount.getAccountID()));

        return accounts;
    }

    public List<BankAccount> fundAccount(double fundSum, String accountID) {
        List<BankAccount> list = null;
        BankAccount account = getAccount(accountID);

        //account.printAccountState();
        account.funding(fundSum);

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, account.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, account.getCard().getCardNumber());

            moneyAmountUpdate.executeUpdate();

            list = new UserDAO().getAllUserAccounts(String.valueOf(account.getUserID()));
        } catch (SQLException ex) {
            logger.error("Caught Exception:", ex);
        }
        logger.info(String.format("Account %s successfully funded.", accountID));

        return list;
    }
}
