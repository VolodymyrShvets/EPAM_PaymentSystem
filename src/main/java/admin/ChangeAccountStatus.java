package admin;

import model.bank.BankAccount;
import model.bank.User;
import model.enums.RequestType;
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

@WebServlet("/change-account-status")
public class ChangeAccountStatus extends HttpServlet {
    final static Logger logger = LogManager.getLogger(ChangeAccountStatus.class);
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String accountID = req.getParameter("id");
        boolean operation = Boolean.parseBoolean(req.getParameter("operation"));   // true: block, false: unblock
        BankAccount account = config.getAccount(accountID);

        if (account != null) {
            if (operation)
                account.block();
            else
                account.unblock();

            try (Connection connection = DriverManager
                    .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE BankAccount SET accountStatus = ? WHERE ID = ?");

                userStatusUpdate.setString(1, account.getStatus().name());
                userStatusUpdate.setLong(2, account.getAccountID());

                int result = userStatusUpdate.executeUpdate();

                if (!operation && result == 1) {
                    config.deleteAllRequests(accountID, RequestType.ACCOUNT);
                }

                logger.info(String.format("Updated BankAccount status: for ID=%s STATUS=%s", account.getAccountID(), account.getStatus()));

                List<User> list = config.getAllUsers();
                session.setAttribute("users", list);
                session.setAttribute("requests", config.getAllRequests());
            } catch (SQLException | ClassNotFoundException ex) {
                logger.error("Caught Exception:", ex);
            }
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
        dispatcher.forward(req, resp);
    }


    /*

    When blocking account - just block.

    When unblocking - search in requests, if account(we want to block) is present in any requests,
                unblock account and delete all requests that contain selected account.

     */
}
