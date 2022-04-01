package controller.login;

import model.bank.BankAccount;
import model.bank.Payment;
import model.bank.User;
import model.enums.AccUsrStatus;
import model.enums.UserRole;
import model.util.Utility;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    final static Logger logger = LogManager.getLogger(LoginServlet.class);
    private final LoginDao loginDao = new LoginDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = Utility.encode(req.getParameter("username"));
        String password = Utility.encode(req.getParameter("password"));

        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(userName);
        loginBean.setPassword(password);

        try {
            if (loginDao.validate(loginBean)) {
                User user = loginDao.getUser(loginBean);
                UserRole userRole = user.getUserRole();
                HttpSession session = req.getSession();
                session.setAttribute("userID", user.getUserID());
                if (user.getStatus() == AccUsrStatus.BLOCKED) {
                    logger.info("Attempt to login into blocked account: " + user.getUserID());
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/userunblock.jsp");
                    dispatcher.forward(req, resp);
                }
                if (!userRole.equals(UserRole.ADMIN)) {
                    logger.info("Login into account: " + user.getUserID());
                    session.setAttribute("userName", user.getFirstName());
                    List<BankAccount> accounts = loginDao.getUserAccounts(String.valueOf(user.getUserID()));
                    List<Payment> payments = loginDao.getUserPayments(String.valueOf(user.getUserID()));
                    session.setAttribute("accountsList", accounts);
                    session.setAttribute("paymentsList", payments);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
                    dispatcher.forward(req, resp);
                } else {
                    logger.info("Login into ADMIN account.");
                    session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
                    session.setAttribute("requests", loginDao.getAdminRequests());
                    session.setAttribute("users", loginDao.getUsersForAdmin());
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
                    dispatcher.forward(req, resp);
                }
            } else {
                logger.info(String.format("Invalid login or password: {%s}/{%s}", loginBean.getUsername(), loginBean.getPassword()));
                HttpSession session = req.getSession();
                session.setAttribute("loginResult", "Wrong password or username");
                RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
                dispatcher.forward(req, resp);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Caught Exception:", e);
        }
    }
}
