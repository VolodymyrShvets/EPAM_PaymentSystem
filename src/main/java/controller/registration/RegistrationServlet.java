package controller.registration;

import controller.login.LoginBean;
import controller.login.LoginDao;
import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.util.Utility;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(RegistrationServlet.class);
    private RegistrationDao registrationDao = new RegistrationDao();
    private LoginDao loginDao = new LoginDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = Utility.encode(req.getParameter("firstName"));
        String lastName = Utility.encode(req.getParameter("lastName"));
        String userLogin = Utility.encode(req.getParameter("userLogin"));
        String userPassword = Utility.hash(Utility.encode(req.getParameter("userPassword")));

        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(userLogin);
        loginBean.setPassword(userPassword);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserLogin(userLogin);
        user.setUserPassword(userPassword);

        logger.info("Checking if user already existed in database: " + userLogin);

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
            logger.info("Check failed: " + userLogin + "  Attempt to create new.");

            User newUser = registrationDao.getNameAndID(user);
            String sessionName = newUser.getFirstName();
            Long userID = newUser.getUserID();
            HttpSession session = req.getSession();
            session.setAttribute("userName", sessionName);
            session.setAttribute("userID", userID);
            session.setAttribute("accountsList", new ArrayList<>());
            session.setAttribute("paymentsList", new ArrayList<>());

            logger.info("New User successfully created: " + userID);

            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
            dispatcher.forward(req, resp);
        } else {
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");
            dispatcher.forward(req, resp);
        }
    }
}
