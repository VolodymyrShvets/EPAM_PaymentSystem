package login;

import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.bank.UserRequest;
import model.util.SQLConfig;

import java.util.List;

public class LoginDao {
    private final SQLConfig config = SQLConfig.getInstance();

    public boolean validate(LoginBean loginBean) throws ClassNotFoundException {
        return config.validateUserPassword(loginBean);
    }

    public List<BankAccount> getUserAccounts(String userID) {
        return config.getAllUserAccounts(userID);
    }

    public User getUser(LoginBean loginBean) {
        return config.getUser(loginBean.getUsername());
    }

    public List<Payment> getUserPayments(String userID) {
        return config.getAllUserPayments(userID);
    }

    public List<UserRequest> getAdminRequests() {
        return config.getAllRequests();
    }

    public List<User> getUsersForAdmin() {
        return config.getAllUsers();
    }
}
