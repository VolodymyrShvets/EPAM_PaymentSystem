package sorting;

import model.bank.BankAccount;
import model.bank.Payment;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet("/sorting-servlet")
public class SortingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Payment> payments = (List<Payment>) session.getAttribute("paymentsList");
        List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accountsList");
        String paymentSortingType = req.getParameter("paymentSortMethod");
        String accountSortingType = req.getParameter("accountSortMethod");

        if (paymentSortingType != null) {
            switch (Integer.parseInt(paymentSortingType)) {
                case 1: { // oldest
                    Comparator<Payment> compareID = Comparator.comparingLong(Payment::getPaymentID).reversed();
                    Comparator<Payment> compareDate = Comparator.comparing(Payment::getPaymentDate).reversed();
                    payments.sort(compareDate.thenComparing(compareID));
                    break;
                }
                case 2: { // newest
                    Comparator<Payment> compareDate = Comparator.comparing(Payment::getPaymentDate);
                    Comparator<Payment> compareID = Comparator.comparingLong(Payment::getPaymentID);
                    payments.sort(compareDate.thenComparing(compareID));
                    /*payments.sort((o1, o2) -> {
                        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            return f.parse(String.valueOf(o1.getPaymentDate())).compareTo(f.parse(String.valueOf(o2.getPaymentDate())));
                        } catch (ParseException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    });*/
                    break;
                }
                default: {
                    payments.sort(Comparator.comparingLong(Payment::getPaymentID));
                }
            }
        }

        if (accountSortingType != null) {
            switch (Integer.parseInt(accountSortingType)) {
                case 1: { // ID
                    accounts.sort(Comparator.comparingLong(BankAccount::getAccountID));
                    break;
                }
                case 2: { // Status, then - ID
                    Comparator<BankAccount> compareStatus = (o1, o2) -> Boolean.compare(o1.isBlocked(), o2.isBlocked());
                    Comparator<BankAccount> compareID = Comparator.comparingLong(BankAccount::getAccountID);
                    accounts.sort(compareStatus.thenComparing(compareID));
                    break;
                }
                case 3: { // Balance, Ascending
                    accounts.sort(Comparator.comparingDouble(o -> o.getCard().getMoneyAmount()));
                    break;
                }
                case 4: { // Balance, Descending
                    accounts.sort((o1, o2) -> -Double.compare(o1.getCard().getMoneyAmount(), o2.getCard().getMoneyAmount()));
                    break;
                }
            }
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
        dispatcher.forward(req, resp);
    }
}
