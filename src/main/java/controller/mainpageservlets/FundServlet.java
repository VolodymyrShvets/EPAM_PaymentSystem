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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/fund")
public class FundServlet extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double fundSum = Double.parseDouble(req.getParameter("amount"));
        HttpSession session = req.getSession();
        String accountID = (String) session.getAttribute("accountID");

        AccountDAO dao = new AccountDAO(config);
        List<BankAccount> list = dao.fundAccount(fundSum, accountID);
        session.setAttribute("accountsList", list);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
