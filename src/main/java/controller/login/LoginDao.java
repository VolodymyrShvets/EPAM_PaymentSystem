package controller.login;

import controller.dao.PaymentDAO;
import controller.dao.RequestDAO;
import controller.dao.UserDAO;
import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.bank.UserRequest;

import java.util.List;

public class LoginDao {
    private final UserDAO userDAO = new UserDAO();

    public boolean validate(LoginBean loginBean) throws ClassNotFoundException {
        return userDAO.validateUserPassword(loginBean);
    }

    public List<BankAccount> getUserAccounts(String userID) {
        return userDAO.getAllUserAccounts(userID);
    }

    public User getUser(LoginBean loginBean) {
        return userDAO.getUser(loginBean.getUsername());
    }

    public List<Payment> getUserPayments(String userID) {
        return new PaymentDAO().getAllUserPayments(userID);
    }

    public List<UserRequest> getAdminRequests() {
        RequestDAO requestDAO = new RequestDAO();
        return requestDAO.getAllRequests();
    }

    public List<User> getUsersForAdmin() {
        return userDAO.getAllUsers();
    }
}
