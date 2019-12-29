import static cerberus.AttFunctions.errorLogger;
import cerberus.Mailer;
import cerberus.AttFunctions;
import static cerberus.AttFunctions.checkInternetConnection;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getTimeID;
import cerberus.messages;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class otp extends HttpServlet {

    private static final long serialVersionUID = 5409486300699166138L;

    String body = null;
    String email = null;
    String rawotp = null;
    String hashotp = null;
    String userInfo = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (checkInternetConnection()) {
            this.userInfo = request.getHeader("User-Agent");
            this.email = request.getParameter("emailadd");
            int spam = 0;
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
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                PreparedStatement ps = con.prepareStatement("SELECT email from `student` WHERE email=? union SELECT email from `faculty` WHERE email=?");
                ps.setString(1, this.email);
                ps.setString(2, this.email);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    this.email = rs.getString(1);
                    email_count++;
                }
                ps = con.prepareStatement("SELECT (select datedata.date from datedata where otp.dateID = datedata.dateID) as dates,(select timedata.time from timedata where otp.timeID = timedata.timeID) as times from `otp` WHERE email=? order by dates,times desc");
                ps.setString(1, this.email);
                rs = ps.executeQuery();

                java.util.Date now = new java.util.Date();
                if (rs.next()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String dateInString = rs.getString(1) + " " + rs.getString(2);
                        Date datetime = sdf.parse(dateInString);
                        long diff = ((now.getTime() - datetime.getTime()) / 1000) / 60;
                        if (diff < 10) {
                            spam = 1;
                        }
                    } catch (SQLException | ParseException e) {
                        errorLogger(e.getMessage());
                    }
                } else {
                    ps = con.prepareStatement("DELETE from `otp` where email=?;");
                    ps.setString(1, this.email);
                    ps.executeUpdate();
                    if (email_count == 1) {
                        int timeID = getTimeID(getCurrTime());
                        int dateID = getDateID(getCurrDate());
                        ps = con.prepareStatement("INSERT INTO `otp`values (null, ?,?,?,?)");
                        ps.setString(1, this.email);
                        ps.setString(2, this.hashotp);
                        ps.setInt(3, dateID);
                        ps.setInt(4, timeID);
                        ps.executeUpdate();
                    }
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                errorLogger(e.getMessage());
                messages b = new messages();
                b.error(request, response, e.getMessage(), "resetpassword.html");
            }
            if (spam == 0) {
                switch (email_count) {
                    case 1: {
                        try {
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
                            Mailer mail = new Mailer();
                            mail.send(this.email, "Password for Cerberus", this.body);
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("redirect", "false");
                            request.setAttribute("head", "OTP Status");
                            request.setAttribute("body", "An One Time High Security Password has been sent to your registered e-mail account.<br>"
                                    + "The OTP is valid for only 10 minutes.<br>");
                            request.setAttribute("url", "resetpassword.html");
                            request.setAttribute("fullpage", "false");
                            request.setAttribute("sec", "3");
                            rd.forward(request, response);
                            break;
                        } catch (InterruptedException ex) {
                            errorLogger(ex.getMessage());
                        }
                    }
                    case 0: {
                        messages c = new messages();
                        c.firewall(request, response, "The email is not registered with this portal.<br>Please check your email and try again.", "index.jsp");
                        break;
                    }
                    default: {
                        messages c = new messages();
                        c.firewall(request, response, "Multiple accounts have been registered with the same email.<br>Please contact Administrator", "index.jsp");
                        break;
                    }
                }
            } else {
                messages c = new messages();
                c.firewall(request, response, "An OTP with a validity of 10 minutes has already been sent to your email.<br>Please check your email.", "index.jsp");
            }
        } else {
            messages c = new messages();
            c.error(request, response, "An active Internet Connection was not found on the server.<br>Please try again later.", "index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            messages c = new messages();
            c.unauthaccess(request, response);
        } catch (Exception e) {
            errorLogger(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
