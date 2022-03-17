package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "SessionLocaleFilter", urlPatterns = {"/*"})
public class SessionLocaleFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (req.getParameter("sessionLocale") != null) {
            req.getSession().setAttribute("lang", req.getParameter("sessionLocale"));
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}
/*
<li><a href="<%=request.getContextPath()%>/self-main">ENG</a></li>
    <li><a href="<%=request.getContextPath()%>/self-main?lang=ua">UA</a></li>
 */
