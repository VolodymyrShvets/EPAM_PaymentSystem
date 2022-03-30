package mainpageservlets;

import model.bank.BankAccount;
import model.util.SQLConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet("/newAccount")
public class AccountsServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(AccountsServlet.class);
    private final SQLConfig config = new SQLConfig();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        long userID = (long) session.getAttribute("userID");
        List<BankAccount> accounts = createNewAccount(String.valueOf(userID));
        session.setAttribute("accountsList", accounts);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }

    private List<BankAccount> createNewAccount(String userID) {
        List<BankAccount> accounts = null;
        BankAccount newAccount = new BankAccount();
        newAccount.setUserID(Long.parseLong(userID));

        String INSERT_BANK_ACCOUNT_SQL = "INSERT INTO BankAccount VALUES (?, ?, ?, ?)";
        String INSERT_CARD_SQL = "INSERT INTO CreditCard VALUES (?, ?, ?, ?)";

        logger.info("Attempt to create New Account with ID=" + newAccount.getAccountID());

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PreparedStatement accountStatement = connection.prepareStatement(INSERT_BANK_ACCOUNT_SQL);
            PreparedStatement cardStatement = connection.prepareStatement(INSERT_CARD_SQL);

            cardStatement.setObject(1, newAccount.getCard().getCardNumber());
            cardStatement.setInt(2, newAccount.getCard().getCvv2());
            cardStatement.setDate(3, Date.valueOf(newAccount.getCard().getExpirationDate()));
            cardStatement.setDouble(4, newAccount.getCard().getMoneyAmount());

            cardStatement.executeUpdate();

            accountStatement.setObject(1, newAccount.getAccountID());
            accountStatement.setObject(2, newAccount.getUserID());
            accountStatement.setString(3, newAccount.getStatus().name());
            accountStatement.setObject(4, newAccount.getCard().getCardNumber());

            accountStatement.executeUpdate();

            accounts = config.getAllUserAccounts(userID);
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Caught Exception:", e);
        }

        logger.info(String.format("New Account %s created", newAccount.getAccountID()));

        return accounts;
    }
}
