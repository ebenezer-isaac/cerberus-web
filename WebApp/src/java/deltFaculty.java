
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getAccess;
import cerberus.Mailer;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class deltFaculty extends HttpServlet {

    private static final long serialVersionUID = 1801278329077327606L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String facultyID = request.getParameter("facultyID");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("Select * from `facultytimetable` where `facultyID` = ?");
                    ps.setString(1, facultyID);
                    ResultSet rs = ps.executeQuery();
                    int flag = 0;
                    while (rs.next()) {
                        flag = 1;
                        break;
                    }
                    if (flag == 0) {
                        String name = "";
                        String email = "";
                        ps = con.prepareStatement("Select * from `faculty` where `facultyID` = ?;");
                        ps.setString(1, facultyID);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            name = rs.getString(2);
                            email = rs.getString(3);
                        }
                        PreparedStatement stmt = con.prepareStatement("Delete from `facultyfingerprint` where `facultyID` = ?;");
                        stmt.setString(1, facultyID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("Delete from `facultysubject` where `facultyID` = ?;");
                        stmt.setString(1, facultyID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("Delete from `faculty` where `facultyID` = ?;");
                        stmt.setString(1, facultyID);
                        stmt.executeUpdate();
                        con.close();
                        String body = "Hello " + name + ",\n    This mail is in response to a request to remove you as a faculty at MSU-CA Department.\n\n"
                                + "Email/Username : " + email + "\n\n"
                                + "Note: You cannot login into the portal anymore\n\n"
                                + "This is an auto-generated e-mail, please do not reply.\n"
                                + "Regards\nCerberus Mail Server";
                        Mailer mail = new Mailer();
                        mail.send(email, "Account Deletion", body);
                        dbLog(name+" was removed from being a Faculty");
                        messages a = new messages();
                        a.success(request, response, "The faculty was deleted successfully", "homepage");
                    } else {
                        messages a = new messages();
                        a.failed(request, response, "The teacher cannot be deleted because teacher-log/teacher-fingerprint is dependent on it", "homepage");
                    }
                } catch (InterruptedException | ClassNotFoundException | SQLException e) {
                    messages a = new messages();
                    a.dberror(request, response, e.getMessage(), "homepage");
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
