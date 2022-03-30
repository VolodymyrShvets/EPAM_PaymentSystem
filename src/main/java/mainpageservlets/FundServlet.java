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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/fund")
public class FundServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(FundServlet.class);
    private final SQLConfig config = new SQLConfig();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double fundSum = Double.parseDouble(req.getParameter("amount"));
        HttpSession session = req.getSession();
        String accountID = (String) session.getAttribute("accountID");
        BankAccount account = config.getAccount(accountID);

        account.printAccountState();
        account.funding(fundSum);

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, account.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, account.getCard().getCardNumber());

            moneyAmountUpdate.executeUpdate();

            List<BankAccount> list = config.getAllUserAccounts(String.valueOf(account.getUserID()));
            session.setAttribute("accountsList", list);
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Caught Exception:", ex);
        }
        logger.info(String.format("Account %s successfully funded.", accountID));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
