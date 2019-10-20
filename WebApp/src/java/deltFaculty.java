
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class deltFaculty extends HttpServlet implements Runnable {

    String name = "";
    String email = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String facultyID = request.getParameter("facultyID");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                    PreparedStatement ps = con.prepareStatement("Select * from `faculty` where `facultyID` = ?;");
                    ps.setString(1, facultyID);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        this.name = rs.getString(2);
                        this.email = rs.getString(3);
                    }
                    PreparedStatement stmt = con.prepareStatement("Delete from `faculty` where `facultyID` = ?;");
                    stmt.setString(1, facultyID);
                    stmt.executeUpdate();
                    con.close();
                }
                Thread thread = new Thread(this);
                thread.start();
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Faculty Deleted");
                request.setAttribute("body", "The faculty was deleted successfully");
                request.setAttribute("url", "homepage");
                rd.forward(request, response);
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Request Failed");
                request.setAttribute("body", "The teacher cannot be deleted because teacher-log/teacher-fingerprint is dependent on it");
                request.setAttribute("url", "homepage");
                rd.forward(request, response);
            }

        }
    }

    @Override
    public void run() {
        String body = "Hello " + this.name + ",\n    This mail is in response to a request to remove you as a faculty at MSU-CA Department.\n\n"
                + "Email/Username : " + this.email + "\n\n"
                + "Note: You cannot login into the portal anymore\n\n"
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
            msg.setSubject("Account Registration for Cerberus");
            msg.setFrom(new InternetAddress("cerberus.msubca@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
            msg.setText(body);
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
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
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
}
