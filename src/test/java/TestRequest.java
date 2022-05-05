import controller.dao.AccountDAO;
import controller.dao.RequestDAO;
import controller.dao.UserDAO;
import model.bank.BankAccount;
import model.bank.User;
import model.bank.UserRequest;
import model.enums.RequestType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRequest {
    static C3P0Test instance;
    static RequestDAO dao;

    @BeforeAll
    static void init() {
        instance = C3P0Test.getInstance();
        dao = new RequestDAO(instance.getConnection());
    }

    @Test
    @Order(1)
    public void testNewUserUnblockingRequest() {
        UserDAO userDAO = new UserDAO(instance.getConnection());
        User user = userDAO.getUser("HenryCavill");
        userDAO.unblockUser(user, true);

        UserRequest expected = new UserRequest();
        expected.createUserUnblockingRequest(user.getUserID());

        dao.newUserUnblockingRequest(user.getUserID());
        List<UserRequest> requests = dao.getAllRequests().stream().filter(r -> r.getType().equals(RequestType.USER)).collect(Collectors.toList());

        assertEquals(4, requests.size());
        expected.setRequestID(requests.get(0).getRequestID());
        assertEquals(expected.toString(), requests.get(0).toString());
    }

    @Test
    @Order(2)
    public void testNewAccountUnblockingRequest() {
        UserDAO userDAO = new UserDAO(instance.getConnection());
        User user = userDAO.getUser("HenryCavill");

        AccountDAO accountDAO = new AccountDAO(instance.getConnection());
        List<BankAccount> accounts = accountDAO.createNewAccount(user.getUserID());

        UserRequest expected = new UserRequest();
        expected.createAccountUnblockingRequest(accounts.get(0).getAccountID(), user.getUserID());

        dao.newAccountUnblockingRequest(user.getUserID(), String.valueOf(accounts.get(0).getAccountID()));
        List<UserRequest> requests = dao.getAllRequests().stream().filter(r -> r.getType().equals(RequestType.ACCOUNT)).collect(Collectors.toList());

        assertEquals(1, requests.size());
        expected.setRequestID(requests.get(0).getRequestID());
        assertEquals(expected.toString(), requests.get(0).toString());
    }

    @Test
    @Order(3)
    public void testGetAllRequests() {
        UserDAO userDAO = new UserDAO(instance.getConnection());
        User user = userDAO.getUser("HenryCavill");
        userDAO.unblockUser(user, true);

        UserRequest expected = new UserRequest();
        expected.createUserUnblockingRequest(user.getUserID());

        dao.newUserUnblockingRequest(user.getUserID());
        dao.newUserUnblockingRequest(user.getUserID());
        dao.newUserUnblockingRequest(user.getUserID());

        List<UserRequest> requests = dao.getAllRequests();
        assertEquals(3, requests.size());
    }

    @Test
    @Order(4)
    public void testDeleteAllRequests() {
        UserDAO userDAO = new UserDAO(instance.getConnection());
        User user = userDAO.getUser("TakeshiKovacs");
        userDAO.unblockUser(user, true);

        UserRequest expected = new UserRequest();
        expected.createUserUnblockingRequest(user.getUserID());

        dao.newUserUnblockingRequest(user.getUserID());

        dao.deleteAllRequests(String.valueOf(user.getUserID()), RequestType.USER);

        List<UserRequest> requests = dao.getAllRequests();
        assertEquals(5, requests.size());
    }

    @AfterAll
    static void clear() {
        String DELETE_QUERY = "DELETE FROM Request";
        Connection connection = instance.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_QUERY);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
