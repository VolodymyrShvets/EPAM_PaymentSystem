package admin;

import model.Bank.User;
import model.enums.RequestType;
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

@WebServlet("/change-user-status")
public class ChangeUserStatus extends HttpServlet {
    private final SQLConfig config = new SQLConfig();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<User> users = (List<User>) session.getAttribute("users");
        long userID = Long.parseLong(req.getParameter("id"));
        boolean operation = Boolean.parseBoolean(req.getParameter("operation"));   // true: block, false: unblock
        User userB = users.stream().filter(user -> userID == user.getUserID()).findAny().orElse(null);

        if (userB != null) {
            if (operation)
                userB.block();
            else
                userB.unblock();

            try (Connection connection = DriverManager
                    .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                PreparedStatement userStatusUpdate = connection.prepareStatement("UPDATE Users SET userStatus = ? WHERE ID = ?");

                userStatusUpdate.setString(1, userB.getStatus().name());
                userStatusUpdate.setLong(2, userB.getUserID());

                int result = userStatusUpdate.executeUpdate();
                System.out.println("users updated : " + result + " : " + userB.getUserID());
                System.out.println("new status : " + userB.getStatus());

                if (!operation && result == 1) {
                    config.deleteAllRequests(String.valueOf(userID), RequestType.USER);
                }

                List<User> list = config.getAllUsers();
                session.setAttribute("users", list);
                session.setAttribute("requests", config.getAllRequests());
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
        dispatcher.forward(req, resp);
    }
}
