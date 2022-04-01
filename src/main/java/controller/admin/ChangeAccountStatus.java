package controller.admin;

import controller.dao.AccountDAO;
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

@WebServlet("/change-account-status")
public class ChangeAccountStatus extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String accountID = req.getParameter("id");
        boolean operation = Boolean.parseBoolean(req.getParameter("operation"));   // true: block, false: unblock

        AccountDAO dao = new AccountDAO(config);
        List<User> list =  dao.changeAccountStatus(accountID, operation);

        session.setAttribute("users", list);
        session.setAttribute("requests", config.getAllRequests());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
        dispatcher.forward(req, resp);
    }


    /*

    When blocking account - just block.

    When unblocking - search in requests, if account(we want to block) is present in any requests,
                unblock account and delete all requests that contain selected account.

     */
}
