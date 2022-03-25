package model.util;

import model.bank.*;
import model.enums.AccUsrStatus;
import model.enums.PaymentStatus;
import model.enums.RequestType;
import model.enums.UserRole;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SQLConfig {
    private static String url;
    private static String password;
    private static String login;

    public SQLConfig() {
        Properties properties = new Properties();
        try (InputStream iStream = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            properties.load(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = properties.getProperty("connection.url");
        login = properties.getProperty("connection.login");
        password = properties.getProperty("connection.password");
    }

    public String getUrl() {
        return url;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public BankAccount getAccount(String accountID) {
        BankAccount account = null;
        String SQL_GET_USER_ACCOUNTS_QUERY = "SELECT BC.ID, BC.userID, BC.accountStatus,\n" +
                "\tCC.cardNumber, CC.cvv2, CC.expirationDate, CC.moneyAmount\n" +
                "    FROM BankAccount AS BC LEFT JOIN CreditCard AS CC ON BC.card = CC.cardNumber\n" +
                "    WHERE BC.ID = ?";

        try (Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ACCOUNTS_QUERY);
            statement.setInt(1, Integer.parseInt(accountID));

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("expirationDate")));
                account = new BankAccount(rs.getLong("ID"), new CreditCard(rs.getLong("cardNumber"), rs.getInt("cvv2"), date, rs.getDouble("moneyAmount")), AccUsrStatus.valueOf(rs.getString("accountStatus")), rs.getLong("userID"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return account;
    }

    public List<BankAccount> getAllUserAccounts(String userID) {
        List<BankAccount> accounts = new ArrayList<>();

        String SQL_GET_USER_ACCOUNTS_QUERY = "SELECT BC.ID, BC.userID, BC.accountStatus,\n" +
                "\tCC.cardNumber, CC.cvv2, CC.expirationDate, CC.moneyAmount\n" +
                "    FROM BankAccount AS BC LEFT JOIN CreditCard AS CC ON BC.card = CC.cardNumber\n" +
                "    WHERE BC.userID = ?";

        try (Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ACCOUNTS_QUERY);
            statement.setInt(1, Integer.parseInt(userID));

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("expirationDate")));
                BankAccount account = new BankAccount(rs.getLong("ID"), new CreditCard(rs.getLong("cardNumber"), rs.getInt("cvv2"), date, rs.getDouble("moneyAmount")), AccUsrStatus.valueOf(rs.getString("accountStatus")), rs.getLong("userID"));
                accounts.add(account);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public List<Payment> getAllUserPayments(String userID) {
        List<Payment> payments = new ArrayList<>();
        List<BankAccount> accounts = getAllUserAccounts(userID);

        String SQL_GET_USER_PAYMENTS_QUERY = "SELECT * FROM Payment WHERE recipientAccID IN (";

        String newQuery = createQuery(SQL_GET_USER_PAYMENTS_QUERY, accounts);

        try (Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement statement = connection.prepareStatement(newQuery);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("paymentDate")));
                BankAccount recipient = getAccount(rs.getString("recipientAccID"));
                BankAccount sender = getAccount(rs.getString("senderAccID"));
                Payment payment = new Payment(rs.getLong("ID"), PaymentStatus.valueOf(rs.getString("paymentStatus")), date, recipient, rs.getString("recipientName"), sender, rs.getString("senderName"), rs.getDouble("paymentSum"));
                payments.add(payment);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return payments;
    }

    public List<UserRequest> getAllRequests() {
        List<UserRequest> requests = new ArrayList<>();

        String SQL_GET_USER_REQUESTS_QUERY = "SELECT * FROM Request";

        try (Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_REQUESTS_QUERY);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                UserRequest request = new UserRequest();
                if (rs.getString("requestType").equals(RequestType.USER.name())) {
                    request.createUserUnblockingRequest(Long.parseLong(rs.getString("userID")));
                    request.setRequestID(Long.parseLong(rs.getString("ID")));
                } else {
                    request.createAccountUnblockingRequest(Long.parseLong(rs.getString("accountID")), Long.parseLong(rs.getString("userID")));
                    request.setRequestID(Long.parseLong(rs.getString("ID")));
                }
                requests.add(request);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return requests;
    }

    public void deleteAllRequests(String ID, RequestType type) {
        StringBuilder DELETE_QUERY = new StringBuilder("DELETE FROM Request WHERE requestType = ? and ");

        if (type == RequestType.USER)
            DELETE_QUERY.append("userID");
        else
            DELETE_QUERY.append("accountID");

        DELETE_QUERY.append(" = ?");

        try(Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement statement = connection.prepareStatement(DELETE_QUERY.toString());
            statement.setString(1, type.name());
            statement.setString(2, ID);

            System.out.println(DELETE_QUERY);
            System.out.println(statement);

            int rs = statement.executeUpdate();
            System.out.println("requests deleted : " + rs);

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
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

    public User getUser(String userLogin) {
        User user = null;

        try (Connection connection = DriverManager
                .getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement statement = connection.prepareStatement("select * from Users where userLogin = ?");
            statement.setString(1, userLogin);

            ResultSet rs = statement.executeQuery();
            rs.next();

            user = new User(rs.getLong("ID"), AccUsrStatus.valueOf(rs.getString("userStatus")),
                    UserRole.valueOf(rs.getString("userRole")), rs.getString("firstName"),
                    rs.getString("lastName"));

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String SQL_GET_ALL_USERS_QUERY = "SELECT ID, userStatus, userRole, firstName, lastName FROM Users";

        try(Connection connection = DriverManager.getConnection(url, login, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            PreparedStatement usersStatement = connection.prepareStatement(SQL_GET_ALL_USERS_QUERY);

            ResultSet rs = usersStatement.executeQuery();
            while (rs.next()){
                if (rs.getString("userRole").equals(UserRole.ADMIN.name()))
                    continue;

                User user = new User();
                user.setUserID(rs.getLong("ID"));
                user.setStatus(AccUsrStatus.valueOf(rs.getString("userStatus")));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setAccounts(getAllUserAccounts(String.valueOf(user.getUserID())));

                users.add(user);
            }

        }catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return users;
    }

    /*
    CREATE TABLE Users (
	    ID INT UNSIGNED PRIMARY KEY,
        userStatus ENUM('BLOCKED', 'ACTIVE') NOT NULL,
        userRole ENUM('USER', 'ADMIN') NOT NULL DEFAULT('USER'),
        firstName VARCHAR(20) NOT NULL,
        lastName VARCHAR(20) NOT NULL,
        userLogin VARCHAR(45) NOT NULL UNIQUE,
        userPassword VARCHAR(170) NOT NULL
   );

    CREATE TABLE CreditCard (
	    cardNumber VARCHAR(16) PRIMARY KEY NOT NULL,
	    cvv2 SMALLINT UNSIGNED NOT NULL,
	    expirationDate DATE NOT NULL,
	    moneyAmount DECIMAL(9,2) DEFAULT 0
    );

    CREATE TABLE BankAccount (
	    ID INT UNSIGNED PRIMARY KEY,
	    userID INT UNSIGNED REFERENCES Users(ID),
        accountStatus ENUM('BLOCKED', 'ACTIVE') NOT NULL,
        card VARCHAR(16) REFERENCES CreditCard(cardNumber)
    );

    CREATE TABLE Payment (
	    ID INT UNSIGNED PRIMARY KEY auto_increment,
	    paymentStatus ENUM('PREPARED', 'SENT') NOT NULL,
        paymentDate DATETIME NOT NULL,
        recipientAccID INT UNSIGNED REFERENCES BankAccount(ID),
        senderAccID INT UNSIGNED REFERENCES BankAccount(ID),
        paymentSum DECIMAL(9,2) NOT NULL
        senderName VARCHAR(40) NOT NULL,
        recipientName varchar(40) NOT NULL
    );

    CREATE TABLE Request (
        ID INT UNSIGNED PRIMARY KEY auto_increment,
        requestType ENUM('USER', 'ACCOUNT') NOT NULL,
        userID INT UNSIGNED REFERENCES Users(ID),
        accountID INT UNSIGNED REFERENCES BankAccount(ID)
    );
     */
}
