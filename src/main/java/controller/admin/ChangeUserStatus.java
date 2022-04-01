package controller.admin;

import controller.dao.UserDAO;
import model.bank.User;
import model.util.SQLConfig;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/change-user-status")
public class ChangeUserStatus extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<User> users = (List<User>) session.getAttribute("users");
        String ID = (String) req.getParameter("id");
        long userID = Long.parseLong(ID);

        boolean operation = Boolean.parseBoolean(req.getParameter("operation"));   // true: block, false: unblock
        User userB = users.stream().filter(user -> userID == user.getUserID()).findAny().orElse(null);

        UserDAO dao = new UserDAO(config);
        List<User> list = dao.unblockUser(userB, operation);

        session.setAttribute("users", list);
        session.setAttribute("requests", config.getAllRequests());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
        dispatcher.forward(req, resp);
    }
}
