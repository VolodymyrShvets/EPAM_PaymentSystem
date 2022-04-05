package controller.mainpageservlets;

import controller.dao.AccountDAO;
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
import java.util.List;

@WebServlet("/newAccount")
public class AccountsServlet extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        long userID = (long) session.getAttribute("userID");

        AccountDAO dao = new AccountDAO(config);
        List<BankAccount> accounts = dao.createNewAccount(userID);

        session.setAttribute("accountsList", accounts);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
