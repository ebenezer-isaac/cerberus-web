package cerberus;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class messages extends HttpServlet {

    String redirect, head, body, url;
    String fullpage = "true";
    int sec = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
        request.setAttribute("redirect", redirect);
        request.setAttribute("head", head);
        request.setAttribute("body", body);
        request.setAttribute("fullpage", fullpage);
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

    public void nouser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "true";
        this.head = "Security Firewall";
        this.body = "Please login to continue";
        this.url = "index.jsp";
        this.fullpage = "false";
        this.sec = 2;
        processRequest(request, response);
    }

    public void success(HttpServletRequest request, HttpServletResponse response, String body, String url)
            throws ServletException, IOException {
        try {
            try (PrintWriter out = response.getWriter()) {
                this.redirect = "true";
                this.head = "Request Successfull";
                this.body = body;
                this.url = url;
                this.sec = 2;
                processRequest(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void failed(HttpServletRequest request, HttpServletResponse response, String body, String url)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            this.redirect = "true";
            this.head = "Request Unsuccessfull";
            this.body = body;
            this.url = url;
            this.sec = 2;
            processRequest(request, response);
        }
    }

    public void unauthaccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.redirect = "true";
        HttpSession session = request.getSession();
        int access = Integer.parseInt(session.getAttribute("access").toString());
        if (access == 2) {
            this.head = "Security Firewall";
            this.body = "Unauthorized access detected";
            this.url = "index.jsp";
            this.fullpage = "false";
        } else if (access == 1 || access == 0) {
            this.head = "Login Successfull";
            this.body = "We are populating your profile";
            this.url = "homepage";
        }
        this.sec = 2;
        processRequest(request, response);
    }

    public void firewall(HttpServletRequest request, HttpServletResponse response, String body, String url)
            throws ServletException, IOException {
        this.redirect = "false";
        this.head = "Security Firewall";
        this.body = body;
        this.url = url;
        this.fullpage = "false";
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
