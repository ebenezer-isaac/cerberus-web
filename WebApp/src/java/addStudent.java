
import cerberus.Mailer;
import cerberus.AttFunctions;
import static cerberus.AttFunctions.generatePassword;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.getTimeID;
import static cerberus.AttFunctions.nameProcessor;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.messages;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addStudent extends HttpServlet {

    private static final long serialVersionUID = -8855276564876004731L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String prn = request.getParameter("prn");
                String name = nameProcessor(request.getParameter("name"));
                if (name.length() > 4) {
                    String email = request.getParameter("email");
                    int classID = Integer.parseInt(request.getParameter("clas"));
                    int roll = Integer.parseInt(request.getParameter("roll"));
                    String rawpass = null;
                    String pass = null;
                    try {
                        rawpass = generatePassword();
                        pass = AttFunctions.hashIt(rawpass);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(addStudent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("INSERT INTO `student`(`PRN` ,`name`, `email`, `password`) VALUES (?,?,?,?)");
                        ps.setString(1, prn);
                        ps.setString(2, name);
                        ps.setString(3, email);
                        ps.setString(4, pass);
                        ps.executeUpdate();
                        ps = con.prepareStatement("INSERT INTO `rollcall`(`classID`, `rollNo`, `PRN`) VALUES (?,?,?)");
                        ps.setInt(1, classID);
                        ps.setInt(2, roll);
                        ps.setString(3, prn);
                        ps.executeUpdate();
                        int dateID = getDateID(getCurrDate());
                        int timeID = getTimeID(getCurrTime());
                        ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`, `dateID`, `timeID`) VALUES (?, '1', NULL , ?, ?);");
                        ps.setString(1, prn);
                        ps.setInt(2, dateID);
                        ps.setInt(3, timeID);
                        ps.executeUpdate();
                        ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`, `dateID`, `timeID`) VALUES (?, '2', NULL , ?, ?);");
                        ps.setString(1, prn);
                        ps.setInt(2, dateID);
                        ps.setInt(3, timeID);
                        ps.executeUpdate();
                        String clas = getClassName(classID);
                        String subs[][] = semSubs(getSem(oddEve(), classID), classID);
                        for (String[] sub : subs) {
                            ps = con.prepareStatement("INSERT INTO `studentsubject`(`PRN`, `subjectID`, `batchID`) VALUES (?,?,?)");
                            ps.setString(1, prn);
                            ps.setString(2, sub[0]);
                            ps.setInt(3, 0);
                            ps.executeUpdate();
                        }
                        String body = "Hello " + name + ",\n    This mail is in response to a request to add you as a student at MSU-CA Department.\n\n"
                                + "Email/Username : " + email + "\n"
                                + "One Time Password : " + rawpass + "\n\n"
                                + "Class    : " + clas + "\n"
                                + "PRN    : " + prn + "\n\n"
                                + "Note: You can change your password by clicking 'Create a New Password' in the Login Page.\n"
                                + "You need to be connected to the BCA Intranet for the below link to work:\n"
                                + "http://172.21.170.14:8080/Cerberus/\n\n"
                                + "You can now login with given username and password at CA Department's Intranet Website\n"
                                + "and view timetable attendance through this portal. You will be asked to provide your MSU Username and select your subjects on first login.\n\n"
                                + "This is an auto-generated e-mail, please do not reply.\n"
                                + "Regards\nCerberus Support Team";

                        Mailer mail = new Mailer();
                        mail.send(email, "Account Registration", body);
                        messages a = new messages();
                        a.success(request, response, name + " has been added as a student.<br>A mail has been sent containing the login password.", "editStudDetails?class=" + classID);
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                        messages a = new messages();
                        a.failed(request, response, e.getMessage(), "editStudDetails?class=" + classID);
                    }

                } else {
                    messages a = new messages();
                    a.failed(request, response, "Student Name cannot be smaller than 5 letters", "editAddStudent?flow=add");
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
