package controller.registration;

import model.bank.User;
import model.util.SQLConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationDao {
    private final SQLConfig config = SQLConfig.getInstance();

    public int registerUser(User user) throws ClassNotFoundException{
        return config.registerNewUser(user);
    }

    public User getNameAndID(User user) {
        return config.getUser(user.getUserLogin());
    }
}
