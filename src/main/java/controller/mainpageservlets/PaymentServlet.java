package controller.mainpageservlets;

import controller.dao.PaymentDAO;
import model.bank.BankAccount;
import model.bank.Payment;
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

@WebServlet("/newPayment")
public class PaymentServlet extends HttpServlet {
    private final SQLConfig config = SQLConfig.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        long userID = (long) session.getAttribute("userID");
        String paymentType = req.getParameter("paymentType");
        String senderId = req.getParameter("senderID");
        String recipientID = req.getParameter("recipientID");
        String amount = req.getParameter("amount");
        String paymentDate = req.getParameter("paymentDate");
        String cvv2 = req.getParameter("cvv2");

        PaymentDAO dao = new PaymentDAO(config);
        String result = dao.createNewPayment(userID, paymentType, senderId, recipientID, amount, paymentDate, cvv2);

        if (result.length() == 0) {
            List<Payment> payments = dao.getPayments();
            List<BankAccount> accounts = dao.getAccounts();

            session.setAttribute("paymentsList", payments);
            session.setAttribute("accountsList", accounts);
        } else {
            session.setAttribute("paymentError", result);
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
