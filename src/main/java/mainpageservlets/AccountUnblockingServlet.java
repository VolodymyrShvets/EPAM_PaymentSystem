package mainpageservlets;

import model.bank.UserRequest;
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

@WebServlet("/unblock")
public class AccountUnblockingServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(AccountUnblockingServlet.class);
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userID = (Long) session.getAttribute("userID");
        String accountID = req.getParameter("accountID");

        UserRequest request = new UserRequest();
        request.createAccountUnblockingRequest(Long.parseLong(accountID), userID);

        logger.info("Attempt to create new AccountRequest.");

        try(Connection connection = DriverManager.getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String SQL_INSERT_NEW_REQUEST_QUERY = "INSERT INTO Request (requestType, userID, accountID) VALUES (?, ?, ?)";
            PreparedStatement requestStatement = connection.prepareStatement(SQL_INSERT_NEW_REQUEST_QUERY);
            requestStatement.setString(1, request.getType().name());
            requestStatement.setLong(2, request.getUserID());
            requestStatement.setLong(3, request.getAccountID());

            requestStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Caught Exception: ", ex);
        }
        logger.info("Created new AccountRequest for UserID=" + userID + " and AccountID=" + accountID);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
