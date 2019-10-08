
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
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class otp extends HttpServlet implements Runnable {

    String body = null;
    String email = null;
    String rawotp = null;
    String hashotp = null;
    String userInfo = null;

    public static String generateOTP() throws NoSuchAlgorithmException {
        String otpchars = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) {
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
        this.userInfo = request.getHeader("User-Agent");
        this.email = request.getParameter("emailadd");
        if (this.email == null) {
            HttpSession session = request.getSession(true);
            this.email = (String) session.getAttribute("email");
        }
        try {
            this.rawotp = generateOTP();
            this.hashotp = hashIt(this.rawotp);
        } catch (NoSuchAlgorithmException e) {
        }
        int email_count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT email from `student` WHERE email=? union SELECT email from `faculty` WHERE email=?");
            ps.setString(1, this.email);
            ps.setString(2, this.email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                this.email = rs.getString(1);
                email_count++;
            }
            ps = con.prepareStatement("DELETE from `otp` where email=?;");
            ps.setString(1, this.email);
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
            }
            if (email_count == 1) {
                ps = con.prepareStatement("INSERT INTO `otp`(`email`, `OTP`) VALUES (?,?)");
                ps.setString(1, this.email);
                ps.setString(2, this.hashotp);
                ps.executeUpdate();
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "false");
            request.setAttribute("head", "Error");
            request.setAttribute("body", e.getMessage());
            request.setAttribute("url", "resetpassword.html");
            rd.forward(request, response);
        }
        switch (email_count) {
            case 1: {
                Thread thread = new Thread(this);
                thread.start();
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "OTP Status");
                request.setAttribute("body", "An One Time High Security Password has been sent to your registered e-mail account.<br>"
                        + "The OTP is valid for only 10 minutes.<br>");
                request.setAttribute("url", "resetpassword.html");
                request.setAttribute("sec", "3");
                rd.forward(request, response);
                break;
            }
            case 0: {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "An e-mail was not found for the provided username. Please check your username and try again");
                request.setAttribute("url", "index.html");
                rd.forward(request, response);
                break;
            }
            default: {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Multiple accounts have been registered with the same email. Please contact Administrator");
                request.setAttribute("url", "index.html");
                rd.forward(request, response);
                break;
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
        request.setAttribute("sec", "2");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void run() {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        java.util.Date dateobj = new java.util.Date();
        String os = userInfo.substring(userInfo.indexOf("(") + 1, userInfo.indexOf(")"));
        this.body = "Hello Subscriber,\n    This mail is in response to your request for password reset.\n"
                + "\n"
                + "Your OTP for Password Reset is\n" + this.rawotp + "\n"
                + "This password will become invalid after 10 minutes from the time of request.\n\n"
                + "Details of the Recieved Request are as follows :\n"
                + "Date and Time : " + (df.format(dateobj)).trim() + "\n";
        this.body += "Operating System : " + os + "\n\n"
                + "This is an auto-generated e-mail, please do not reply.\n"
                + "Regards\nCerberus Support Team";
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
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
            msg.setText(this.body);
            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", Integer.valueOf("465"), "Cerberus Support Team", "cerberu$@123");
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("DELETE from `otp` WHERE email=");
            ps.setString(1, this.email);
            ps.executeUpdate();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
