package controller.dao;

import model.bank.UserRequest;
import model.util.SQLConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequestDAO {
    final static Logger logger = LogManager.getLogger(RequestDAO.class);
    private SQLConfig config;

    public RequestDAO(SQLConfig config) {
        this.config = config;
    }

    public String createNewRequest(long userID) {
        logger.info("Attempt to create new UserRequest.");

        UserRequest request = new UserRequest();
        request.createUserUnblockingRequest(userID);

        try(Connection connection = DriverManager.getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String SQL_INSERT_NEW_REQUEST_QUERY = "INSERT INTO Request (requestType, userID) VALUES (?, ?)";
            PreparedStatement requestStatement = connection.prepareStatement(SQL_INSERT_NEW_REQUEST_QUERY);
            requestStatement.setString(1, request.getType().name());
            requestStatement.setLong(2, request.getUserID());

            requestStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Caught Exception: ", ex);
            return "Something unexpected happen.";
        }
        logger.info("Created new UserRequest for: " + userID);

        return "You successfully sent request to Admin.<br>Try another login later.";
    }
}
