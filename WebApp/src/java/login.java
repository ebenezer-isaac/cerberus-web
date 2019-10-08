
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
import javax.servlet.http.HttpSession;

public class login extends HttpServlet {

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
        String temp = str;
        temp = temp.replaceAll("\\s+", "");
        temp = temp.replaceAll("[A-Za-z0-9]+", "");
        temp = temp.replaceAll("\"", "'");
        return (temp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String email = request.getParameter("email");
            String rawpass = request.getParameter("password");
            String id = "";
            if (trimSQLInjection(rawpass).equals("'''='")) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Nice Try!");
                request.setAttribute("body", "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F)));
                request.setAttribute("url", "index.html");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
            } else {
                String pass = hashIt(rawpass);
                int access = 0;
                String corrpass = "";
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select prn,password from student where email=?");
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        id = rs.getString(1);
                        corrpass = rs.getString(2);
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Database Error");
                    request.setAttribute("body", e.getMessage());
                    request.setAttribute("url", "dispSubject");
                    rd.forward(request, response);
                }
                if (corrpass.equals("")) {

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps = con.prepareStatement("select facultyID, password from faculty where email=?");
                        ps.setString(1, email);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            id = rs.getString(1);
                            corrpass = rs.getString(2);
                        }
                        access = 1;
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Database Error");
                        request.setAttribute("body", e.getMessage());
                        request.setAttribute("url", "dispSubject");
                        rd.forward(request, response);
                    }
                }
                if (corrpass.equals(pass)) {
                    String userid = "";
                    int index = email.indexOf("@");
                    if (index != -1) {
                        userid = email.substring(0, index);
                    }
                    HttpSession session = request.getSession();
                    if (corrpass.equals(userid)) {
                        RequestDispatcher rd = request.getRequestDispatcher("otp");
                        session.setAttribute("email", email);
                        rd.forward(request, response);
                    } else {
                        RequestDispatcher rd = request.getRequestDispatcher("homepage");
                        session.setAttribute("email", email);
                        session.setAttribute("access", access);
                        session.setAttribute("id", id);
                        rd.forward(request, response);
                    }
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Security Firewall");
                    request.setAttribute("body", "Invalid Username or Password. Please check your credentials and try again");
                    request.setAttribute("url", "index.html");
                    rd.forward(request, response);
                }
            }
        } catch (Exception e) {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "false");
            request.setAttribute("head", "Error");
            request.setAttribute("body", e.getMessage());
            request.setAttribute("url", "index.html");
            rd.forward(request, response);
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
        request.setAttribute("redirect", "true");
        request.setAttribute("head", "Security Firewall");
        request.setAttribute("body", "Unauthorized access detected.");
        request.setAttribute("url", "index.html");
        request.setAttribute("sec", "2");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
