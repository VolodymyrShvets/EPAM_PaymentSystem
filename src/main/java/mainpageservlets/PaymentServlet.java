package mainpageservlets;

import model.Bank.BankAccount;
import model.Bank.Payment;
import model.enums.AccUsrStatus;
import model.enums.PaymentStatus;
import model.util.SQLConfig;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet("/newPayment")
public class PaymentServlet extends HttpServlet {
    private final SQLConfig config = new SQLConfig();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        long userID = (long) session.getAttribute("userID");
        String senderId = req.getParameter("senderID");
        String recipientID = req.getParameter("recipientID");
        String amount = req.getParameter("amount");
        String cvv2 = req.getParameter("cvv2");

        List<Payment> payments;

        BankAccount sender = config.getAccount(senderId);
        BankAccount recipient = config.getAccount(recipientID);

        if (sender.getCard().getMoneyAmount() < Double.parseDouble(amount)) {
            session.setAttribute("paymentError", "Not enough money on account.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/paymentpage.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if (sender.getCard().getCvv2() != Integer.parseInt(cvv2)) {
            session.setAttribute("paymentError", "Wrong CVV2.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/paymentpage.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        try (Connection connection = DriverManager
                .getConnection(config.getUrl(), config.getLogin(), config.getPassword())) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String SQL_GET_SENDER_NAME_QUERY = "SELECT firstName, lastName FROM Users WHERE ID = ?";
            String SQL_GET_RECIPIENT_NAME_QUERY = "SELECT us.firstName, us.lastName, bk.accountStatus FROM Users AS us LEFT JOIN BankAccount AS bk ON bk.userID = us.ID WHERE bk.ID = ?";
            String SQL_INSERT_NEW_PAYMENT_QUERY = "INSERT INTO Payment (paymentStatus, paymentDate, recipientAccID, senderAccID, paymentSum, senderName, recipientName) \nVALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement senderNameStatement = connection.prepareStatement(SQL_GET_SENDER_NAME_QUERY);
            senderNameStatement.setString(1, String.valueOf(userID));

            ResultSet rs = senderNameStatement.executeQuery();
            rs.next();
            String senderName = rs.getString("firstName") + " " + rs.getString("lastName");

            PreparedStatement recipientNameStatement = connection.prepareStatement(SQL_GET_RECIPIENT_NAME_QUERY);
            recipientNameStatement.setObject(1, recipientID);
            rs = recipientNameStatement.executeQuery();
            rs.next();
            String recipientName = rs.getString("firstName") + " " + rs.getString("lastName");
            String recipientAccountStatus = rs.getString("accountStatus");

            if (recipientAccountStatus.equals(AccUsrStatus.BLOCKED.name())) {
                session.setAttribute("paymentError", "Account you tried to reach is currently blocked.<br>Try again later.");
                RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/paymentpage.jsp");
                dispatcher.forward(req, resp);
                return;
            }

            Payment payment = new Payment(PaymentStatus.SENT, recipient, recipientName, sender, senderName, Double.parseDouble(amount));
            PreparedStatement insertNewPaymentStatement = connection.prepareStatement(SQL_INSERT_NEW_PAYMENT_QUERY);
            insertNewPaymentStatement.setString(1, payment.getStatus().name());
            insertNewPaymentStatement.setDate(2, Date.valueOf(payment.getPaymentDate()));
            insertNewPaymentStatement.setObject(3, payment.getRecipient());
            insertNewPaymentStatement.setObject(4, payment.getSender());
            insertNewPaymentStatement.setDouble(5, payment.getPaymentSum());
            insertNewPaymentStatement.setString(6, payment.getSenderName());
            insertNewPaymentStatement.setString(7, payment.getRecipientName());

            int result = insertNewPaymentStatement.executeUpdate();
            System.out.println("inserted payments :  " + result);

            sender.withdrawal(Double.parseDouble(amount));
            recipient.funding(Double.parseDouble(amount));

            //----------------------
            PreparedStatement moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, sender.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, sender.getCard().getCardNumber());

            result = moneyAmountUpdate.executeUpdate();
            System.out.println("cards updated :  " + result);

            //----------------------
            moneyAmountUpdate = connection.prepareStatement("UPDATE CreditCard SET moneyAmount = ? WHERE cardNumber = ?");

            moneyAmountUpdate.setDouble(1, recipient.getCard().getMoneyAmount());
            moneyAmountUpdate.setLong(2, recipient.getCard().getCardNumber());

            result = moneyAmountUpdate.executeUpdate();
            System.out.println("cards updated :  " + result);

            //----------------------

            payments = config.getAllUserPayments(String.valueOf(userID));
            session.setAttribute("paymentsList", payments);
            List<BankAccount> accounts = config.getAllUserAccounts(String.valueOf(userID));
            session.setAttribute("accountsList", accounts);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
