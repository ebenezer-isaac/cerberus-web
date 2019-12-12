
import cerberus.Mailer;
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.nameProcessor;
import cerberus.messages;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
                String email = request.getParameter("email");
                int classID = Integer.parseInt(request.getParameter("clas"));
                int roll = Integer.parseInt(request.getParameter("roll"));
                String photoID = request.getParameter("photo_id");
                String rawpass = prn;
                String pass = null;
                try {
                    pass = AttFunctions.hashIt(prn);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(addStudent.class.getName()).log(Level.SEVERE, null, ex);
                }
                InputStream inputStream = null;
                try {
                    URL url = new URL("http://msubcdndwn.digitaluniversity.ac/resources/public/msub/Photosign/Photo/" + prn.substring(0, 4) + "/" + photoID + "_P.JPG");
                    inputStream = url.openStream();
                } catch (IOException e) {

                }
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("INSERT INTO `student`(`PRN` ,`name`, `email`, `password`,`photo_id`, `photo`) VALUES (?,?,?,?,?,?)");
                    ps.setString(1, prn);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setString(4, pass);
                    ps.setString(5, photoID);
                    ps.setBlob(6, inputStream);
                    ps.executeUpdate();
                    ps = con.prepareStatement("INSERT INTO `rollcall`(`classID`, `rollNo`, `PRN`) VALUES (?,?,?)");
                    ps.setInt(1, classID);
                    ps.setInt(2, roll);
                    ps.setString(3, prn);
                    ps.executeUpdate();
                    ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`) VALUES (?, '1', NULL);");
                    ps.setString(1, prn);
                    ps.executeUpdate();
                    ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`) VALUES (?, '2', NULL);");
                    ps.setString(1, prn);
                    ps.executeUpdate();
                    String subjects[] = request.getParameterValues("subjects");
                    String subsbody = "Subjects Selected:\n";
                    String clas = clas = getClassName(classID);
                    for (String subject : subjects) {
                        try {
                            int batchid = Integer.parseInt(request.getParameter("batch" + subject));
                            ps = con.prepareStatement("select abbreviation from subject where subjectID = ?");
                            ps.setString(1, subject);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                subsbody += subject + " - " + rs.getString(1) + " - ";
                            }
                            ps = con.prepareStatement("select name from batch where batchID = ?");
                            ps.setInt(1, batchid);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                subsbody += rs.getString(1) + "\n";
                            }
                            ps = con.prepareStatement("INSERT INTO `studentsubject`(`PRN`, `subjectID`, `batchID`) VALUES (?,?,?)");
                            ps.setString(1, prn);
                            ps.setString(2, subject);
                            ps.setInt(3, batchid);
                            ps.executeUpdate();
                        } catch (NumberFormatException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    subsbody += "\n";
                    String body = "Hello " + name + ",\n    This mail is in response to a request to add you as a student at MSU-CA Department.\n\n"
                            + "Email/Username : " + email + "\n"
                            + "One Time Password : " + rawpass + "\n\n"
                            + "MSU Digital ID : " + photoID + "\n"
                            + "Class    : " + clas + "\n"
                            + "Roll Number    : " + roll + "\n"
                            + subsbody
                            + "You can now login with given username and password at at CA Department's Intranet WebSite\n"
                            + "and view timetable attendance through this portal.\n\n"
                            + "This is an auto-generated e-mail, please do not reply.\n"
                            + "Regards\nCerberus Support Team";

                    Mailer mail = new Mailer();
                    mail.send(email, "Account Registration", body);
                    messages a = new messages();
                    a.success(request, response, name + " was added to the list of Students. A mail has been sent to respective student.", "homepage");
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    messages a = new messages();
                    a.failed(request, response, e.getMessage(), "editAddStudent?flow=add");
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
