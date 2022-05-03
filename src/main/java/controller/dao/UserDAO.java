package controller.dao;

import controller.login.LoginBean;
import controller.login.LoginDao;
import model.bank.*;
import model.enums.AccUsrStatus;
import model.enums.RequestType;
import model.enums.UserRole;
import model.util.C3P0DataSource;
import model.util.Utility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    final static Logger logger = LogManager.getLogger(UserDAO.class);
    Connection connection;

    public UserDAO() {
        connection = C3P0DataSource.getInstance().getConnection();
    }

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public List<User> unblockUser(User userB, boolean operation) {
        List<User> list = null;

        if (userB != null) {
            if (operation)
                userB.block();
            else
                userB.unblock();

            try {
                PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE Users SET userStatus = ? WHERE ID = ?");

                userStatusUpdate.setString(1, userB.getStatus().name());
                userStatusUpdate.setLong(2, userB.getUserID());

                int result = userStatusUpdate.executeUpdate();

                if (!operation && result == 1) {
                    new RequestDAO().deleteAllRequests(String.valueOf(userB.getUserID()), RequestType.USER);
                }

                logger.info(String.format("Updated User status: for ID=%s STATUS=%s", userB.getUserID(), userB.getStatus()));

                list = getAllUsers();

            } catch (SQLException ex) {
                logger.error("Caught Exception: ", ex);
            }
        }

        return list;
    }

    public List<BankAccount> getAllUserAccounts(String userID) {
        List<BankAccount> accounts = new ArrayList<>();

        String SQL_GET_USER_ACCOUNTS_QUERY = "SELECT BC.ID, BC.userID, BC.accountStatus,\n" +
                "\tCC.cardNumber, CC.cvv2, CC.expirationDate, CC.moneyAmount\n" +
                "    FROM BankAccount AS BC LEFT JOIN CreditCard AS CC ON BC.card = CC.cardNumber\n" +
                "    WHERE BC.userID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ACCOUNTS_QUERY);
            statement.setInt(1, Integer.parseInt(userID));

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(new SimpleDateFormat("yyy-MM-dd").format(rs.getDate("expirationDate")));
                BankAccount account = new BankAccount(rs.getLong("ID"), new CreditCard(rs.getLong("cardNumber"), rs.getInt("cvv2"), date, rs.getDouble("moneyAmount")), AccUsrStatus.valueOf(rs.getString("accountStatus")), rs.getLong("userID"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            logger.error("Caught Exception: ", e);
        }
        return accounts;
    }

    public User getUser(String userLogin) {
        User user = null;

        try {
            PreparedStatement statement = connection.prepareStatement("select * from Users where userLogin = ?");
            statement.setString(1, userLogin);

            ResultSet rs = statement.executeQuery();
            rs.next();

            user = new User(rs.getLong("ID"), AccUsrStatus.valueOf(rs.getString("userStatus")),
                    UserRole.valueOf(rs.getString("userRole")), rs.getString("firstName"),
                    rs.getString("lastName"));

        } catch (SQLException e) {
            logger.error("Caught Exception: ", e);
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String SQL_GET_ALL_USERS_QUERY = "SELECT ID, userStatus, userRole, firstName, lastName FROM Users";

        try {
            PreparedStatement usersStatement = connection.prepareStatement(SQL_GET_ALL_USERS_QUERY);

            ResultSet rs = usersStatement.executeQuery();
            while (rs.next()) {
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

        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
        }

        return users;
    }

    public boolean validateUserPassword(LoginBean loginBean) {
        boolean status = false;

        try {
            PreparedStatement statement = connection.prepareStatement("select userPassword from Users where userLogin = ? ");
            statement.setString(1, loginBean.getUsername());

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if (Utility.validatePassword(loginBean.getPassword(), rs.getString("userPassword"))) {
                    logger.info("Successful login validation: " + loginBean.getUsername());
                    status = true;
                }
            }
        } catch (SQLException e) {
            logger.error("Caught Exception:", e);
        }
        return status;
    }

    public int registerNewUser(User user) {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(user.getUserLogin());
        loginBean.setPassword(user.getUserPassword());

        if (validateUserPassword(loginBean))
            return 1;

        String INSERT_USER_SQL = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_USER_SQL);
            statement.setObject(1, user.getUserID());
            statement.setString(2, user.getStatus().name());
            statement.setString(3, user.getUserRole().name());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getUserLogin());
            statement.setString(7, user.getUserPassword());

            result = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Caught Exception:", e);
        }
        return result;
    }
}
