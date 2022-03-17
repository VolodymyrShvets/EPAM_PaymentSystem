import model.Bank.BankAccount;
import model.Bank.CreditCard;
import model.Bank.User;
import model.enums.UserRole;
import model.util.SQLConfig;

import java.sql.*;
import java.time.LocalDate;

public class Demo {
    public static void main(String[] args) {
       /*

       Comparator<Payment> compareID = Comparator.comparingLong(Payment::getPaymentID);
                    Comparator<Payment> compareDate = Comparator.comparing(Payment::getPaymentDate);
                    payments.sort(compareDate.thenComparing(compareID));

       long cardNumber = 4441_1144_6367_8035L;
        int cvv2 = 686;
        String expirationDate = "2029-08-01";
        double funds = 19.65;
        CreditCard card1 = new CreditCard(cardNumber, cvv2, LocalDate.parse(expirationDate), funds);
        BankAccount account1 = new BankAccount(card1);
        account1.printAccountState();
        System.out.println();

        long cardNumber2 = 5168_7554_4455_3958L;
        int cvv22 = 374;
        String expirationDate2 = "2023-05-01";
        double funds2 = 813;
        CreditCard card2 = new CreditCard(cardNumber2, cvv22, LocalDate.parse(expirationDate2), funds2);
        BankAccount account2 = new BankAccount(card2);
        account2.printAccountState();

        account2.createNewPayment(account2, 2);

        */

        //------------------------ Code for insertion new credit card and bank account for chosen user -------------------

        /*CreditCard card = new CreditCard(1551499528753564L, 952, LocalDate.of(2030, 03, 01), 0);
        BankAccount account = new BankAccount(card);
        account.setUserID(887368781);
        account.printAccountState();
        String INSERT_BANK_ACCOUNT_SQL = "INSERT INTO BankAccount VALUES (?, ?, ?, ?)";
        String INSERT_CARD_SQL = "INSERT INTO CreditCard VALUES (?, ?, ?, ?)";

         */

        SQLConfig config = new SQLConfig();
        //String INSERT_NEW_USER = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)";
        /*
        try(Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PreparedStatement userStatement = connection.prepareStatement(INSERT_NEW_USER);

            User admin = new User();
            admin.setUserID(1);
            admin.setUserRole(UserRole.ADMIN);
            admin.setFirstName("Nick");
            admin.setLastName("Fury");
            admin.setUserLogin("admin@gmail.com");
            admin.setUserPassword("_Night_Fury_");

            System.out.println(admin);

            userStatement.setLong(1, admin.getUserID());
            userStatement.setString(2, admin.getStatus().name());
            userStatement.setString(3, admin.getUserRole().name());
            userStatement.setString(4, admin.getFirstName());
            userStatement.setString(5, admin.getLastName());
            userStatement.setString(6, admin.getUserLogin());
            userStatement.setString(7, admin.getUserPassword());

            int result = userStatement.executeUpdate();
            System.out.println("inserted users :  " + result);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

         */
    }
}
