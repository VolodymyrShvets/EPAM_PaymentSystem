package login;

import model.Bank.BankAccount;
import model.Bank.Payment;
import model.Bank.User;
import model.enums.AccUsrStatus;
import model.enums.UserRole;
import model.util.Util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final LoginDao loginDao = new LoginDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = Util.encode(req.getParameter("username"));
        String password = Util.encode(req.getParameter("password"));

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
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/userunblock.jsp");
                    dispatcher.forward(req, resp);
                }
                if (!userRole.equals(UserRole.ADMIN)) {
                    session.setAttribute("userName", user.getFirstName());
                    List<BankAccount> accounts = loginDao.getUserAccounts(String.valueOf(user.getUserID()));
                    List<Payment> payments = loginDao.getUserPayments(String.valueOf(user.getUserID()));
                    session.setAttribute("accountsList", accounts);
                    session.setAttribute("paymentsList", payments);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/main.jsp");
                    dispatcher.forward(req, resp);
                } else {
                    session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
                    session.setAttribute("requests", loginDao.getAdminRequests());
                    session.setAttribute("users", loginDao.getUsersForAdmin());
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp");
                    dispatcher.forward(req, resp);
                }
            } else {
                HttpSession session = req.getSession();
                session.setAttribute("loginResult", "Wrong password or username");
                RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
                dispatcher.forward(req, resp);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}