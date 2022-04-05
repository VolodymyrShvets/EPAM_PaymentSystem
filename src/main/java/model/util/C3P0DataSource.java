package model.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class C3P0DataSource {
    final static Logger logger = LogManager.getLogger(C3P0DataSource.class);
    private static C3P0DataSource instance;
    private ComboPooledDataSource dataSource;

    private C3P0DataSource() {
        Properties properties = new Properties();
        try (InputStream iStream = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            properties.load(iStream);

            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
            dataSource.setJdbcUrl(properties.getProperty("connection.url"));
            dataSource.setUser(properties.getProperty("connection.login"));
            dataSource.setPassword(properties.getProperty("connection.password"));
        } catch (IOException | PropertyVetoException e) {
            logger.error("Unable to create new instance.", e);
        }
    }

    public static C3P0DataSource getInstance() {
        if (instance == null)
            instance = new C3P0DataSource();
        return instance;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException ex) {
            logger.error("Unable to create connection.");
        }
        return con;
    }

    /*
    CREATE TABLE Users (
	    ID INT UNSIGNED PRIMARY KEY,
        userStatus ENUM('BLOCKED', 'ACTIVE') NOT NULL,
        userRole ENUM('USER', 'ADMIN') NOT NULL DEFAULT('USER'),
        firstName VARCHAR(20) NOT NULL,
        lastName VARCHAR(20) NOT NULL,
        userLogin VARCHAR(45) NOT NULL UNIQUE,
        userPassword VARCHAR(170) NOT NULL
   );

    CREATE TABLE CreditCard (
	    cardNumber VARCHAR(16) PRIMARY KEY NOT NULL,
	    cvv2 SMALLINT UNSIGNED NOT NULL,
	    expirationDate DATE NOT NULL,
	    moneyAmount DECIMAL(9,2) DEFAULT 0
    );

    CREATE TABLE BankAccount (
	    ID INT UNSIGNED PRIMARY KEY,
	    userID INT UNSIGNED REFERENCES Users(ID),
        accountStatus ENUM('BLOCKED', 'ACTIVE') NOT NULL,
        card VARCHAR(16) REFERENCES CreditCard(cardNumber)
    );

    CREATE TABLE Payment (
	    ID INT UNSIGNED PRIMARY KEY auto_increment,
	    paymentStatus ENUM('PREPARED', 'SENT') NOT NULL,
        paymentDate DATETIME NOT NULL,
        recipientAccID INT UNSIGNED REFERENCES BankAccount(ID),
        senderAccID INT UNSIGNED REFERENCES BankAccount(ID),
        paymentSum DECIMAL(9,2) NOT NULL
        senderName VARCHAR(40) NOT NULL,
        recipientName varchar(40) NOT NULL
    );

    CREATE TABLE Request (
        ID INT UNSIGNED PRIMARY KEY auto_increment,
        requestType ENUM('USER', 'ACCOUNT') NOT NULL,
        userID INT UNSIGNED REFERENCES Users(ID),
        accountID INT UNSIGNED REFERENCES BankAccount(ID)
    );
     */
}
