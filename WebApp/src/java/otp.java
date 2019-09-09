
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;

public class otp extends HttpServlet implements Runnable {

    String to = null;
    String body = null;
    String username = null;
    String hashotp = null;

    public String trimSQLInjection(String str) {
        str = str.replaceAll("\\s+", "");
        str = str.replaceAll("[A-Za-z0-9]+", "");
        str = str.replaceAll("\"", "'");
        return (str);
    }

    public static String generateOTP() throws NoSuchAlgorithmException {
        String otpchars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * otpchars.length());
            salt.append(otpchars.charAt(index));
        }
        return salt.toString();
    }

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        java.util.Date dateobj = new java.util.Date();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String ipValidater = "0";
        try {
            ipValidater = (ipAddress.substring(0, ipAddress.indexOf(":")));
        } catch (Exception e) {
        }
        String userInfo = request.getHeader("User-Agent");
        String os = userInfo.substring(userInfo.indexOf("(") + 1, userInfo.indexOf(")"));
        this.username = request.getParameter("username");
        if (trimSQLInjection(this.username).equals("'''='")) {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "true");
            request.setAttribute("head", "Nice Try!");
            request.setAttribute("body", "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F)));
            request.setAttribute("url", "index.html");
            rd.forward(request, response);
        } else {
            String otp = null;
            try {
                otp = generateOTP();
                this.hashotp = hashIt(otp);
            } catch (NoSuchAlgorithmException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("url", "index.html");
                rd.forward(request, response);
            }
            String toadd = "";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("SELECT email from `student` WHERE PRN=?");
                ps.setString(1, this.username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    toadd = rs.getString(1);
                }
                if (toadd == null) {
                    try {
                        ps = con.prepareStatement("SELECT email from `faculty` WHERE facultyID=?");
                        ps.setString(1, this.username);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            toadd = rs.getString(1);
                        }
                        con.close();
                    } catch (SQLException ee) {
                    }
                }
                this.to = toadd;
                con.close();
            } catch (ClassNotFoundException | SQLException ee) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "An e-mail was not found for the provided username. Please check your username and try again");
                request.setAttribute("url", "index.html");
                rd.forward(request, response);
            }
            if (toadd != null) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("DELETE * from `otp` where username=?;");
                    ps.setString(1, this.username);
                    try {
                        ps.executeUpdate();
                    } catch (SQLException e) {
                    }
                    ps = con.prepareStatement("INSERT INTO `otp` VALUES (?,?);");
                    ps.setString(1, this.username);
                    ps.setString(2, this.hashotp);
                    ps.executeUpdate();
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Error");
                    request.setAttribute("body", e.getMessage());
                    request.setAttribute("url", "index.html");
                    rd.forward(request, response);
                }
                this.body = "Hello Subscriber,\n    This mail is in response to your request for password reset.\n"
                        + "\n"
                        + "Your OTP for Password Reset is\n" + otp + "\n"
                        + "This password will become invalid after 10 minutes from the time of request.\n\n"
                        + "Details of the Recieved Request are as follows :\n"
                        + "Date and Time : " + (df.format(dateobj)).trim() + "\n";
                if (!ipValidater.equals("0")) {
                    this.body += "IP Address : " + ipAddress + "\n";
                }
                this.body += "Operating System : " + os + "\n\n"
                        + "This is an auto-generated e-mail, please do not reply.\n"
                        + "Regards\nCerberus Support Team";
                Thread thread = new Thread(this);
                thread.start();
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "OTP Status");
                request.setAttribute("body", "An One Time High Security Password has been sent to your registered e-mail account.<br>"
                        + "The OTP is valid for only 10 minutes.<br>");
                request.setAttribute("url", "resetpassword.html");
                rd.forward(request, response);
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "An e-mail was not found for the provided username. Please check your username and try again");
                request.setAttribute("url", "index.html");
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
        request.setAttribute("url", "index.html");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put("mail.smtp.user", "cerberus.msubca@gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cerberus.msubca@gmail.com", "cerberu$@123");
            }
        });
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setSubject("Password Reset for Cerberus");
            msg.setFrom(new InternetAddress("cerberus.msubca@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to));
            msg.setText(this.body);
            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", Integer.valueOf("465"), "Cerberus Support Team", "cerberu$@123");
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (AddressException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        try {
            sleep(600000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("DELETE from `otp` WHERE username=? and otp=?;");
            ps.setString(1, this.username);
            ps.setString(2, this.hashotp);
            ps.executeUpdate();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
