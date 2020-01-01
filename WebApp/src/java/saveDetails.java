
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class saveDetails extends HttpServlet {

    private static final long serialVersionUID = -1107134811018436314L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                messages a = new messages();
                a.failed(request, response, "This page is only for Students", "homepage");
                break;
            case 0:
                String photoID = request.getParameter("msuid");
                HttpSession session = request.getSession(false);
                String prn = (String) session.getAttribute("user");
                InputStream inputStream = null;
                try {
                    URL url = new URL("http://msubcdndwn.digitaluniversity.ac/resources/public/msub/Photosign/Photo/20" + photoID.substring(1, 3) + "/" + photoID + "_P.JPG");
                    inputStream = url.openStream();
                } catch (IOException e) {
                    errorLogger(e.getMessage());
                }
                String subjects[] = request.getParameterValues("subjects");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps;
                    for (String subject : subjects) {
                        int batchid = Integer.parseInt(request.getParameter("batch" + subject));
                        ps = con.prepareStatement("UPDATE `studentsubject` set `batchID` = ? where prn= ? and subjectID = ?");
                        ps.setInt(1, batchid);
                        ps.setString(2, prn);
                        ps.setString(3, subject);
                        ps.executeUpdate();
                    }
                    if (inputStream != null) {
                        ps = con.prepareStatement("INSERT INTO `studentphoto`(`PRN`, `photo`) VALUES (?,?)");
                        ps.setString(1, prn);
                        ps.setBlob(2, inputStream);
                        ps.executeUpdate();
                    }
                    con.close();
                } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                    errorLogger(e.getMessage());
                    messages b = new messages();
                    b.error(request, response, e.getMessage(), "homepage");
                }

                dbLog("Student with prn : " + prn + " selected his/her subjects");
                messages d = new messages();
                d.success(request, response, "Your Details have been saved successfully", "homepage");
                break;
            default:
                messages c = new messages();
                c.nouser(request, response);
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
