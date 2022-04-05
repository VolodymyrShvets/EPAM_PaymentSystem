package controller.mainpageservlets;

import controller.dao.RequestDAO;
import model.util.SQLConfig;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/unblock-user")
public class UserUnblockingServlet extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userID = (Long) session.getAttribute("userID");

        RequestDAO dao = new RequestDAO(config);
        String result = dao.createNewRequest(userID);
        session.setAttribute("loginResult", result);

        RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
        dispatcher.forward(req, resp);
    }
}
