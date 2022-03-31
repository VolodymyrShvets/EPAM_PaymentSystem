package controller.forwardservlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/contact-admin")
public class ForwardServlet4 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String destination = "/WEB-INF/views/user/accountunblock.jsp";
        RequestDispatcher dispatcher = req.getRequestDispatcher(destination);
        dispatcher.forward(req, resp);
    }
}
