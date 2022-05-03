import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class C3P0Test {
    private static C3P0Test instance;
    private ComboPooledDataSource dataSource;

    private C3P0Test() {
        Properties properties = new Properties();
        try (InputStream iStream = getClass().getClassLoader().getResourceAsStream("testdb.properties")) {
            properties.load(iStream);

            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
            dataSource.setJdbcUrl(properties.getProperty("connection.url"));
            dataSource.setUser(properties.getProperty("connection.login"));
            dataSource.setPassword(properties.getProperty("connection.password"));
        } catch (IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static C3P0Test getInstance() {
        if (instance == null)
            instance = new C3P0Test();
        return instance;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }
}
