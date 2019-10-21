
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class messages extends HttpServlet {

    String redirect, head, body, url;
    int sec = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
        request.setAttribute("redirect", redirect);
        request.setAttribute("head", head);
        request.setAttribute("body", body);
        request.setAttribute("url", url);
        request.setAttribute("sec", sec);
        rd.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void error(HttpServletRequest request, HttpServletResponse response, String body, String url)
            throws ServletException, IOException {
        this.redirect = "false";
        this.head = "Error";
        this.body = body;
        this.url = url;
        processRequest(request, response);
    }

    public void dberror(HttpServletRequest request, HttpServletResponse response, String body, String url)
            throws ServletException, IOException {
        this.redirect = "false";
        this.head = "Database Error";
        this.body = body;
        this.url = url;
        processRequest(request, response);
    }

    public void wrongpass(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "false";
        this.head = "Security Firewall";
        this.body = "Invalid Username or Password. Please check your credentials and try again";
        this.url = "index.html";
        processRequest(request, response);
    }

    public void nouser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "true";
        this.head = "Security Firewall";
        this.body = "Please login to continue";
        this.url = "index.html";
        this.sec = 2;
        processRequest(request, response);
    }

    public void unauthaccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "true";
        this.head = "Security Firewall";
        this.body = "Unauthorized access detected";
        HttpSession session = request.getSession();
        int access = Integer.parseInt(session.getAttribute("access").toString());
        if (access == 2) {
            this.url = "index.html";
        } else {
            this.url = "homepage";
        }
        this.sec = 2;
        processRequest(request, response);
    }

    public void kids(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "false";
        this.head = "Hey 'Kid'!";
        this.body = "You are not authorized to view this page";
        this.url = "homepage";
        processRequest(request, response);
    }

}
