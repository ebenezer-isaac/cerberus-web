package cerberus;

import static cerberus.AttFunctions.errorLogger;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

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
            errorLogger(e.getMessage());

        } catch (MessagingException | UnsupportedEncodingException e) {
            errorLogger(e.getMessage());
        }
        try {
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", 993, "cerberus.msubca@gmail.com", "cerberu$@123");
            Folder folderInbox = null;
            Folder[] f = store.getDefaultFolder().list();
            for (Folder fd : f) {
                Folder t[] = fd.list();
                for (Folder f1 : t) {
                    folderInbox = store.getFolder(fd.getName() + "/" + f1.getName());
                    folderInbox.open(Folder.READ_WRITE);
                    Message[] arrayMessages = folderInbox.getMessages();
                    for (Message message : arrayMessages) {
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            }
            boolean expunge = true;
            folderInbox.close(expunge);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        System.out.println("And God said It is done");
    }
}
