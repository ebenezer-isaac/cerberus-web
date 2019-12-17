
import cerberus.Mailer;
import cerberus.AttFunctions;
import static cerberus.AttFunctions.checkInternetConnection;
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
import java.text.SimpleDateFormat;
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
                ps = con.prepareStatement("SELECT (select datedata.date from datedata where otp.dateID = datedata.dateID) as dates,(select timedata.time from timedata where otp.timeID = timedata.timeID) as times from `otp` WHERE email=? order by dates,times desc");
                ps.setString(1, this.email);
                rs = ps.executeQuery();

                java.util.Date now = new java.util.Date();
                if (rs.next()) {
                    String date[] = rs.getString(1).split("-");
                    String time[] = rs.getString(2).split(":");
                    java.util.Date then = new java.util.Date(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]) + 10, Integer.parseInt(time[2]));
                    if (then.compareTo(now) < 0) {
                        spam = 1;
                    }
                } else {
                    System.out.println("No data found");
                    ps = con.prepareStatement("DELETE from `otp` where email=?;");
                    ps.setString(1, this.email);
                    ps.executeUpdate();
                    if (email_count == 1) {
                        String now_time = now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
                        String now_date = now.getYear() + "-" + now.getMonth() + "-" + now.getDate();
                        // int timeID = getTimeID(now_time);
                        // int dateID = getDateID(now_date);
                        ps = con.prepareStatement("INSERT INTO `otp`(`email`, `OTP`, `dateID`, `timeID`) VALUES (?,?,?,?)");
                        ps.setString(1, this.email);
                        ps.setString(2, this.hashotp);
                        //ps.setString(3, timeID);
                        //ps.setString(4, dateID);
                        ps.executeUpdate();
                    }
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("fullpage", "false");
                request.setAttribute("url", "resetpassword.html");
                rd.forward(request, response);
            }
            if (spam == 0) {
                switch (email_count) {
                    case 1: {
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
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "OTP Status");
                        request.setAttribute("body", "An One Time High Security Password has been sent to your registered e-mail account.<br>"
                                + "The OTP is valid for only 10 minutes.<br>");
                        request.setAttribute("url", "resetpassword.html");
                        request.setAttribute("fullpage", "false");
                        request.setAttribute("sec", "3");
                        rd.forward(request, response);
                        break;
                    }
                    case 0: {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Security Firewall");
                        request.setAttribute("body", "An e-mail was not found for the provided username. Please check your username and try again");
                        request.setAttribute("url", "index.jsp");
                        request.setAttribute("fullpage", "false");
                        rd.forward(request, response);
                        break;
                    }
                    default: {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Security Firewall");
                        request.setAttribute("body", "Multiple accounts have been registered with the same email. Please contact Administrator");
                        request.setAttribute("url", "index.jsp");
                        request.setAttribute("fullpage", "false");
                        rd.forward(request, response);
                        break;
                    }
                }
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "An OTP with a validity of 10 minutes has already been sent to your email. Please check your email.");
                request.setAttribute("url", "index.jsp");
                request.setAttribute("fullpage", "false");
                rd.forward(request, response);
            }
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "false");
            request.setAttribute("head", "Request Failed");
            request.setAttribute("body", "An active Internet Connection was not found on the server. Please try again later.");
            request.setAttribute("url", "index.jsp");
            request.setAttribute("fullpage", "false");
            rd.forward(request, response);
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
