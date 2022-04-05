package controller.mainpageservlets;

import controller.dao.AccountDAO;
import model.bank.BankAccount;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/fund")
public class FundServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double fundSum = Double.parseDouble(req.getParameter("amount"));
        HttpSession session = req.getSession();
        String accountID = (String) session.getAttribute("accountID");

        AccountDAO dao = new AccountDAO();
        List<BankAccount> list = dao.fundAccount(fundSum, accountID);
        session.setAttribute("accountsList", list);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
