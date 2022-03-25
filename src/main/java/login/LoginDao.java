package login;

import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.bank.UserRequest;
import model.util.SQLConfig;
import model.util.Utility;

import java.sql.*;
import java.util.List;

public class LoginDao {
    private final SQLConfig config = new SQLConfig();

    public boolean validate(LoginBean loginBean) throws ClassNotFoundException {
        boolean status = false;

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            PreparedStatement statement = connection.prepareStatement("select userPassword from Users where userLogin = ? ");
            statement.setString(1, loginBean.getUsername());

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if(Utility.validatePassword(loginBean.getPassword(), rs.getString("userPassword"))){
                    status = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
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
