package controller.mainpageservlets;

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

@WebServlet("/accstatuschanging")
public class AccountBlockingServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(AccountBlockingServlet.class);
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String accountID = (String) session.getAttribute("accountID");
        BankAccount account = config.getAccount(accountID);

        account.block();
        account.printAccountState();

        logger.info("Attempt to Block Account.");

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PreparedStatement accountStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

            accountStatusUpdate.setString(1, account.getStatus().name());
            accountStatusUpdate.setLong(2, account.getAccountID());

            accountStatusUpdate.executeUpdate();

            List<BankAccount> list = config.getAllUserAccounts(String.valueOf(account.getUserID()));
            session.setAttribute("accountsList", list);
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Caught Exception: ", ex);
        }

        logger.info(String.format("Account %s successfully blocked.", accountID));

        // TODO update all PRG relationships ( sendRedirect -> forward )
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}