
import java.io.IOException;
import static java.lang.Thread.sleep;
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
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class otp extends HttpServlet implements Runnable {

    String body = null;
    String email = null;
    String rawotp = null;
    String hashotp = null;
    String userInfo = null;

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
            this.rawotp = AttFunctions.generateOTP();
            this.hashotp = AttFunctions.hashIt(this.rawotp);
        } catch (NoSuchAlgorithmException e) {
        }
        int email_count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
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
                ps.executeUpdate();

                if (email_count == 1) {
                    ps = con.prepareStatement("INSERT INTO `otp`(`email`, `OTP`) VALUES (?,?)");
                    ps.setString(1, this.email);
                    ps.setString(2, this.hashotp);
                    ps.executeUpdate();
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
            try (Transport transport = session.getTransport("smtps")) {
                transport.connect("smtp.gmail.com", Integer.valueOf("465"), "Cerberus Support Team", "cerberu$@123");
                transport.sendMessage(msg, msg.getAllRecipients());
            }
        } catch (AddressException e) {
        } catch (MessagingException e) {
        }
        try {
            sleep(600000);
        } catch (InterruptedException e) {
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                PreparedStatement ps = con.prepareStatement("DELETE from `otp` WHERE email=");
                ps.setString(1, this.email);
                ps.executeUpdate();
                con.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
    }
}
