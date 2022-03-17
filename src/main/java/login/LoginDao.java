package login;

import model.Bank.BankAccount;
import model.Bank.Payment;
import model.Bank.User;
import model.Bank.UserRequest;
import model.util.SQLConfig;

import java.sql.*;
import java.util.List;

public class LoginDao {
    private final SQLConfig config = new SQLConfig();

    public boolean validate(LoginBean loginBean) throws ClassNotFoundException {
        boolean status = false;

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            PreparedStatement statement = connection.prepareStatement("select * from Users where userLogin = ? and userPassword = ?");
            statement.setString(1, loginBean.getUsername());
            statement.setString(2, loginBean.getPassword());

            ResultSet rs = statement.executeQuery();
            status = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public List<BankAccount> getUserAccounts(String userID) {
        return config.getAllUserAccounts(userID);
    }

    public User getUser(LoginBean loginBean) {
        return config.getUser(loginBean.getUsername(), loginBean.getPassword());
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
