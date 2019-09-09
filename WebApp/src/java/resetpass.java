
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.RequestDispatcher;

public class resetpass extends HttpServlet {

    public static String hashIt(String raw) throws NoSuchAlgorithmException {
        raw = raw + "msubca";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger number = new BigInteger(1, md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public String trimSQLInjection(String str) {
        str = str.replaceAll("\\s+", "");
        str = str.replaceAll("[A-Za-z0-9]+", "");
        str = str.replaceAll("\"", "'");
        return (str);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String us = request.getParameter("username");
            String otp = request.getParameter("otp");
            String pass = request.getParameter("conpass");
            if (trimSQLInjection(us).equals("'''='") || trimSQLInjection(otp).equals("'''='") || trimSQLInjection(pass).equals("'''='")) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Nice Try!");
                request.setAttribute("body", "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F)));
                request.setAttribute("url", "index.html");
                rd.forward(request, response);
            } else {
                String corrotp = null;
                pass = hashIt(pass);
                otp = hashIt(otp);
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select otp from `otp` where username=?");
                    ps.setString(1, us);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        corrotp = rs.getString(1);
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    corrotp = "null";
                }
                if (corrotp.equals(otp)) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps = con.prepareStatement("UPDATE `credential` SET password = ? where username=?;");
                        ps.setString(1, pass);
                        ps.setString(2, us);
                        ps.executeUpdate();
                    } catch (ClassNotFoundException | SQLException e) {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Error");
                        request.setAttribute("body", e.getMessage());
                        request.setAttribute("url", "resetpassword.html");
                        rd.forward(request, response);
                    }
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "true");
                    request.setAttribute("head", "Security Message");
                    request.setAttribute("body", "Your password has been updated.<br> Please login with your new credentials");
                    request.setAttribute("url", "index.html");
                    request.setAttribute("method", "post");
                    request.setAttribute("button", "Redirect");
                    rd.forward(request, response);
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Security Firewall");
                    request.setAttribute("body", "Please cheack your username and the OTP you provided and try again.");
                    request.setAttribute("url", "resetpassword.html");
                    request.setAttribute("method", "post");
                    request.setAttribute("button", "Redirect");
                    rd.forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
        request.setAttribute("redirect", "true");
        request.setAttribute("head", "Security Firewall");
        request.setAttribute("body", "Unauthorized access to this page has been detected.");
        request.setAttribute("url", "index.html");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
