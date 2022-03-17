package registration;

import login.LoginBean;
import login.LoginDao;
import model.Bank.BankAccount;
import model.Bank.Payment;
import model.Bank.User;
import model.util.Util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private RegistrationDao registrationDao = new RegistrationDao();
    private LoginDao loginDao = new LoginDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = Util.encode(req.getParameter("firstName"));
        String lastName = Util.encode(req.getParameter("lastName"));
        String userLogin = Util.encode(req.getParameter("userLogin"));
        String userPassword = Util.encode(req.getParameter("userPassword"));


        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(userLogin);
        loginBean.setPassword(userPassword);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserLogin(userLogin);
        user.setUserPassword(userPassword);

        try {
            if (loginDao.validate(loginBean)) {
                user = loginDao.getUser(loginBean);
                HttpSession session = req.getSession();
                session.setAttribute("userID", user.getUserID());
                session.setAttribute("userName", user.getFirstName());
                List<BankAccount> accounts = loginDao.getUserAccounts(String.valueOf(user.getUserID()));
                List<Payment> payments = loginDao.getUserPayments(String.valueOf(user.getUserID()));
                session.setAttribute("accountsList", accounts);
                session.setAttribute("paymentsList", payments);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
                dispatcher.forward(req, resp);
            } else if (registrationDao.registerUser(user) == 1) {
                String[] values = registrationDao.getNameAndID(user);
                String sessionName = values[0];
                String userID = values[1];
                HttpSession session = req.getSession();
                session.setAttribute("userName", sessionName);
                session.setAttribute("userID", userID);
                session.setAttribute("accountsList", new ArrayList<>());
                session.setAttribute("paymentsList", new ArrayList<>());
                RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
                dispatcher.forward(req, resp);
            } else {
                RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");
                dispatcher.forward(req, resp);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
