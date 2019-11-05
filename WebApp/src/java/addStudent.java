
import cerberus.Mailer;
import cerberus.AttFunctions;
import cerberus.messages;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addStudent extends HttpServlet {

    String prn = "";
    String name = "";
    String email = "";
    String rawpass = "";
    String body = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            this.prn = request.getParameter("prn");
            this.name = request.getParameter("name");
            this.email = request.getParameter("email");
            int seleclass = Integer.parseInt(request.getParameter("clas"));
            int roll = Integer.parseInt(request.getParameter("roll"));
            String photoID = request.getParameter("photo_id");
            this.rawpass = this.prn;
            String pass = AttFunctions.hashIt(this.prn);
            InputStream inputStream = null;
            try {
                URL url = new URL("http://msubcdndwn.digitaluniversity.ac/resources/public/msub/Photosign/Photo/" + this.prn.substring(0, 4) + "/" + photoID + "_P.JPG");
                inputStream = url.openStream();
            } catch (IOException e) {

            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("INSERT INTO `student`(`PRN` ,`name`, `email`, `password`,`photo_id`, `photo`) VALUES (?,?,?,?,?,?)");
                ps.setString(1, this.prn);
                ps.setString(2, this.name);
                ps.setString(3, this.email);
                ps.setString(4, pass);
                ps.setString(5, photoID);
                ps.setBlob(6, inputStream);
                ps.executeUpdate();

                ps = con.prepareStatement("INSERT INTO `rollcall`(`classID`, `rollNo`, `PRN`) VALUES (?,?,?)");
                ps.setInt(1, seleclass);
                ps.setInt(2, roll);
                ps.setString(3, this.prn);
                ps.executeUpdate();

                ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`) VALUES (?, '1', NULL);");
                ps.setString(1, this.prn);
                ps.executeUpdate();
                ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`) VALUES (?, '2', NULL);");
                ps.setString(1, prn);
                ps.executeUpdate();

                String subjects[] = request.getParameterValues("subjects");
                System.out.println("hwkki world " + subjects.length);
                String subsbody = "Subjects Selected:\n";
                String clas = "";
                for (int i = 0; i < subjects.length; i++) {
                    System.out.println(subjects[i]);
                    try {
                        int batchid = Integer.parseInt(request.getParameter("batch" + subjects[i]));
                        ps = con.prepareStatement("select class from class where classID = ?");
                        ps.setInt(1, seleclass);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            clas = rs.getString(1);
                        }
                        ps = con.prepareStatement("select abbreviation from subject where subjectID = ?");
                        ps.setString(1, subjects[i]);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            subsbody += subjects[i] + " - " + rs.getString(1) + " - ";
                        }
                        ps = con.prepareStatement("select name from batch where batchID = ?");
                        ps.setInt(1, batchid);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            subsbody += rs.getString(1) + "\n";
                        }
                        ps = con.prepareStatement("INSERT INTO `studentsubject`(`PRN`, `subjectID`, `batchID`) VALUES (?,?,?)");
                        ps.setString(1, this.prn);
                        ps.setString(2, subjects[i]);
                        ps.setInt(3, batchid);
                        ps.executeUpdate();
                    } catch (NumberFormatException | SQLException e) {
                        e.printStackTrace();
                    }

                }
                subsbody += "\n";
                this.body = "Hello " + this.name + ",\n    This mail is in response to a request to add you as a student at MSU-CA Department.\n\n"
                        + "Email/Username : " + this.email + "\n"
                        + "One Time Password : " + this.rawpass + "\n\n"
                        + "MSU Digital ID : " + photoID + "\n"
                        + "Class    : " + clas + "\n"
                        + "Roll Number    : " + roll + "\n"
                        + subsbody
                        + "You can now login with given username and password at at CA Department's Intranet WebSite\n"
                        + "and view timetable attendance through this portal.\n\n"
                        + "This is an auto-generated e-mail, please do not reply.\n"
                        + "Regards\nCerberus Support Team";

                Mailer mail = new Mailer();
                mail.send(this.email, "Account Registration", this.body);
                messages a = new messages();
                a.success(request, response, this.name + " was added to the list of Students. A mail has been sent to respective student.", "homepage");

                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
                messages a = new messages();
                a.failed(request, response, e.getMessage(), "editAddStudent?flow=add");
            }
        } catch (Exception e) {
            messages a = new messages();
            a.error(request, response, e.getMessage(), "homepage");
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
