
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getAccess;
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

public class deltSubject extends HttpServlet {

    private static final long serialVersionUID = -7656776598974905541L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String subjectID = request.getParameter("subject");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("Select * from `timetable` where `subjectID` = ?");
                    ps.setString(1, subjectID);
                    ResultSet rs = ps.executeQuery();
                    int flag = 0;
                    while (rs.next()) {
                        flag = 1;
                        break;
                    }
                    if (flag == 0) {
                        PreparedStatement stmt = con.prepareStatement("Delete from `subject` where `subjectID` = ?;");
                        stmt.setString(1, subjectID);
                        stmt.executeUpdate();
                        dbLog(currUserName(request) + " deleted a subject with subjectID : " + subjectID);
                        messages a = new messages();
                        a.success(request, response, "The subject was deleted successfully<br>SubjectID : " + subjectID, "viewSubject");
                    } else {
                        messages a = new messages();
                        a.failed(request, response, "The subject cannot be deleted because timetable sessions are dependent on it", "homepage");
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
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
