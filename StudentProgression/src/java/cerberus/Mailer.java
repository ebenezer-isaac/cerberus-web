package cerberus;

import java.io.UnsupportedEncodingException;
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

    public void send(String to, String subject, String body) throws InterruptedException {
        this.to = to;
        this.subject = subject;
        this.body = body;
        Thread thread = new Thread(this);
        thread.start();
        Thread.sleep(500);
    }

    @Override
    public void run() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Properties props = new Properties();
        props.put("mail.smtps.user", "cerberus.msubca@gmail.com");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", "465");
        props.put("mail.smtps.starttls.enable", "true");
        props.put("mail.smtps.debug", "true");
        props.put("mail.smtps.auth", "true");

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
            msg.setFrom(new InternetAddress("cerberus.msubca@gmail.com", "Cerberus Team"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to));
            msg.setText(this.body);
            try (Transport transport = session.getTransport("smtps")) {
                transport.connect("smtp.gmail.com", Integer.valueOf("465"), "Cerberus Team", "cerberu$@123");
                transport.sendMessage(msg, msg.getAllRecipients());
            }
        } catch (AddressException e) {
            e.printStackTrace();

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }
}
