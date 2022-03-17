package registration;

import model.Bank.User;
import model.util.SQLConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationDao {
    private final SQLConfig config = new SQLConfig();

    public int registerUser(User user) throws ClassNotFoundException{
        String INSERT_USER_SQL = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = 0;

        Class.forName("com.mysql.cj.jdbc.Driver");

        try(Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
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
            e.printStackTrace();
        }
        return result;
    }

    public String[] getNameAndID(User user) {
        return config.getInfo(user.getUserLogin(), user.getUserPassword());
    }
}
