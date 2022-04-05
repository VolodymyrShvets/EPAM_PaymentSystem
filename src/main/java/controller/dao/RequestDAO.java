package controller.dao;

import model.bank.UserRequest;
import model.enums.RequestType;
import model.util.C3P0DataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {
    final static Logger logger = LogManager.getLogger(RequestDAO.class);

    public RequestDAO() {
    }

    public String createNewRequest(long userID) {
        logger.info("Attempt to create new UserRequest.");

        UserRequest request = new UserRequest();
        request.createUserUnblockingRequest(userID);

        try(Connection connection = C3P0DataSource.getInstance().getConnection()) {
            String SQL_INSERT_NEW_REQUEST_QUERY = "INSERT INTO Request (requestType, userID) VALUES (?, ?)";
            PreparedStatement requestStatement = connection.prepareStatement(SQL_INSERT_NEW_REQUEST_QUERY);
            requestStatement.setString(1, request.getType().name());
            requestStatement.setLong(2, request.getUserID());

            requestStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
            return "Something unexpected happen.";
        }
        logger.info("Created new UserRequest for: " + userID);

        return "You successfully sent request to Admin.<br>Try another login later.";
    }

    public List<UserRequest> getAllRequests() {
        List<UserRequest> requests = new ArrayList<>();

        String SQL_GET_USER_REQUESTS_QUERY = "SELECT * FROM Request";

        try (Connection connection = C3P0DataSource.getInstance().getConnection()) {
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
        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
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

        try(Connection connection = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_QUERY.toString());
            statement.setString(1, type.name());
            statement.setString(2, ID);

            statement.executeUpdate();

        } catch (SQLException ex) {
            logger.error("Caught Exception: ", ex);
        }
    }
}
