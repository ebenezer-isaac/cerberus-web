
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;

public class resetpass extends HttpServlet {

    private static final long serialVersionUID = 8478417136338882914L;

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
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");
        String pass = request.getParameter("conpass");
        if (trimSQLInjection(otp).equals("'''='") || trimSQLInjection(pass).equals("'''='")) {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "true");
            request.setAttribute("head", "Nice Try!");
            request.setAttribute("body", "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F)));
            request.setAttribute("url", "index.jsp");
            request.setAttribute("sec", "2");
            rd.forward(request, response);
        } else {
            int otp_count = 0;
            String corrotp = null;
            try {
                pass = hashIt(pass);
                otp = otp.toUpperCase();
                otp = hashIt(otp);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(resetpass.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Class.forName("com.mysql.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123")) {
                    PreparedStatement ps = con.prepareStatement("select otp from `otp` where email=?");
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        otp_count++;
                        corrotp = rs.getString(1);
                    }
                    con.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("url", "resetpassword.html");
                rd.forward(request, response);
            }
            if (otp_count == 1) {
                if (corrotp.equals(otp)) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("UPDATE `student` SET password = ? where email=?");
                        ps.setString(1, pass);
                        ps.setString(2, email);
                        ps.executeUpdate();
                        ps = con.prepareStatement("UPDATE `faculty` SET password = ? where email=?");
                        ps.setString(1, pass);
                        ps.setString(2, email);
                        ps.executeUpdate();
                        ps = con.prepareStatement("DELETE from otp where email=?");
                        ps.setString(1, email);
                        ps.executeUpdate();
                        con.close();
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Security Message");
                        request.setAttribute("body", "Your password has been updated.<br> Please login with your new credentials");
                        request.setAttribute("url", "index.jsp");
                        request.setAttribute("sec", "2");
                        request.setAttribute("fullpage", "false");
                        rd.forward(request, response);
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Error");
                        request.setAttribute("body", e.getMessage());
                        request.setAttribute("url", "resetpassword.html");
                        request.setAttribute("fullpage", "false");
                        rd.forward(request, response);
                    }
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Security Firewall");
                    request.setAttribute("body", "Please cheack your Email and the OTP you provided and try again.");
                    request.setAttribute("url", "resetpassword.html");
                    request.setAttribute("button", "Redirect");
                    request.setAttribute("fullpage", "false");
                    rd.forward(request, response);
                }
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "An OTP was not found for the provided Email address.");
                request.setAttribute("url", "index.jsp");
                request.setAttribute("button", "Redirect");
                request.setAttribute("fullpage", "false");
                rd.forward(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
        request.setAttribute("redirect", "true");
        request.setAttribute("head", "Security Firewall");
        request.setAttribute("body", "Unauthorized access to this page has been detected.");
        request.setAttribute("url", "index.jsp");
        request.setAttribute("sec", "2");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
