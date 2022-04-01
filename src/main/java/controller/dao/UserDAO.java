package controller.dao;

import model.bank.User;
import model.enums.RequestType;
import model.util.SQLConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserDAO {
    final static Logger logger = LogManager.getLogger(UserDAO.class);
    private SQLConfig config;

    public UserDAO(SQLConfig config) {
        this.config = config;
    }

    public List<User> unblockUser(User userB, boolean operation) {
        List<User> list = null;

        if (userB != null) {
            if (operation)
                userB.block();
            else
                userB.unblock();

            try (Connection connection = DriverManager
                    .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE Users SET userStatus = ? WHERE ID = ?");

                userStatusUpdate.setString(1, userB.getStatus().name());
                userStatusUpdate.setLong(2, userB.getUserID());

                int result = userStatusUpdate.executeUpdate();

                if (!operation && result == 1) {
                    config.deleteAllRequests(String.valueOf(userB.getUserID()), RequestType.USER);
                }

                logger.info(String.format("Updated User status: for ID=%s STATUS=%s", userB.getUserID(), userB.getStatus()));

                list = config.getAllUsers();

            } catch (SQLException | ClassNotFoundException ex) {
                logger.error("Caught Exception: ", ex);
            }
        }

        return list;
    }
}
