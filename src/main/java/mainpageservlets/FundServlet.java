package mainpageservlets;

import model.Bank.BankAccount;
import model.util.SQLConfig;

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

            int result = moneyAmountUpdate.executeUpdate();
            System.out.println("cards updated :  " + result);

            List<BankAccount> list = config.getAllUserAccounts(String.valueOf(account.getUserID()));
            session.setAttribute("accountsList", list);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
