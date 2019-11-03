package cerberus;


import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

public class Mailer implements Runnable {

    String to;
    String subject;
    String body;

    public void send(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        Thread thread = new Thread(this);
        thread.start();
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
            msg.setSubject(this.subject);
            msg.setFrom(new InternetAddress("cerberus.msubca@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to));
            msg.setText(this.body);
            try (Transport transport = session.getTransport("smtps")) {
                transport.connect("smtp.gmail.com", Integer.valueOf("465"), "Cerberus Support Team", "cerberu$@123");
                transport.sendMessage(msg, msg.getAllRecipients());
            }
        } catch (AddressException e) {
            System.out.println(e.getMessage());
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
        try {
            sleep(600000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                PreparedStatement ps = con.prepareStatement("DELETE from `otp` WHERE email=");
                ps.setString(1, this.to);
                ps.executeUpdate();
                con.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
