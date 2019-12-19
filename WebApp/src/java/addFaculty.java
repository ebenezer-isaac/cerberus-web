
import cerberus.AttFunctions;
import cerberus.Mailer;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getTimeID;
import static cerberus.AttFunctions.nameProcessor;
import cerberus.messages;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addFaculty extends HttpServlet {

    private static final long serialVersionUID = 5523616611204304202L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String name = request.getParameter("title") + nameProcessor(request.getParameter("name"));
                if (name.length() > 4) {
                    String email = request.getParameter("email");
                    String pass = "";
                    int index = email.indexOf("@");
                    if (index != -1) {
                        pass = email.substring(0, index);
                    }
                    String rawpass = pass;
                    {
                        try {
                            pass = AttFunctions.hashIt(pass);
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(addFaculty.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("INSERT INTO `faculty` (`facultyID`, `name`, `email`, `password`, `photo`) VALUES (NULL, ?,?,?,null)");
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, pass);
                        ps.executeUpdate();
                        ps = con.prepareStatement("select facultyID from `faculty` where `name` = ? and `email` = ? and `password` = ?");
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, pass);
                        ResultSet rs = ps.executeQuery();
                        String facultyID = "";
                        while (rs.next()) {
                            facultyID = rs.getString(1);
                        }
                        int dateID = getDateID(getCurrDate());
                        int timeID = getTimeID(getCurrTime());
                        ps = con.prepareStatement("INSERT INTO `facultyfingerprint` VALUES (?,1,null,?,?)");
                        ps.setString(1, facultyID);
                        ps.setInt(2, dateID);
                        ps.setInt(3, timeID);
                        ps.executeUpdate();
                        ps = con.prepareStatement("INSERT INTO `facultyfingerprint` VALUES (?,2,null,?,?)");
                        ps.setString(1, facultyID);
                        ps.setInt(2, dateID);
                        ps.setInt(3, timeID);
                        ps.executeUpdate();
                        String body = "Hello " + name + ",\n    This mail is in response to a request to add you as a faculty at MSU-CA Department.\n\n"
                                + "Email/Username : " + email + "\n"
                                + "One Time Password : " + rawpass + "\n\n"
                                + "You can now login with given username and password at at CA Department's Intranet WebSite\n"
                                + "and wield admin privileges to manage timetable, students' details and attendance amoung other things through this portal.\n\n"
                                + "Note: You will be prompted to change password on first login.\n\n"
                                + "This is an auto-generated e-mail, please do not reply.\n"
                                + "Regards\nCerberus Support Team";
                        Mailer mail = new Mailer();
                        mail.send(email, "Account Registration", body);
                        con.close();
                        messages a = new messages();
                        a.success(request, response, name + " was added to the list of faculties. A mail has been sent to their registered email address.", "homepage");
                    } catch (ClassNotFoundException | SQLException e) {
                        messages a = new messages();
                        a.dberror(request, response, e.getMessage(), "homepage");
                    }
                } else {
                    messages a = new messages();
                    a.failed(request, response, "Teacher Name cannot be smaller than 5 letters", "editAddStudent?flow=add");
                }
                break;
            case 0:
                messages a = new messages();
                a.kids(request, response);
                break;
            default:
                messages b = new messages();
                b.nouser(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
