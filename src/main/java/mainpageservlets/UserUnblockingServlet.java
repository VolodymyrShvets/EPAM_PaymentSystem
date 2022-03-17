package mainpageservlets;

import model.Bank.UserRequest;
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

@WebServlet("/unblock-user")
public class UserUnblockingServlet extends HttpServlet {
    private final SQLConfig config = new SQLConfig();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userID = (Long) session.getAttribute("userID");
        System.out.println(userID);

        UserRequest request = new UserRequest();
        request.createUserUnblockingRequest(userID);

        try(Connection connection = DriverManager.getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String SQL_INSERT_NEW_REQUEST_QUERY = "INSERT INTO Request (requestType, userID) VALUES (?, ?)";
            PreparedStatement requestStatement = connection.prepareStatement(SQL_INSERT_NEW_REQUEST_QUERY);
            requestStatement.setString(1, request.getType().name());
            requestStatement.setLong(2, request.getUserID());

            int res = requestStatement.executeUpdate();
            System.out.println("inserted requests : " + res);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        session.setAttribute("loginResult", "You successfully sent request to Admin.<br>Try another login later.");
        RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
        dispatcher.forward(req, resp);
    }
}
