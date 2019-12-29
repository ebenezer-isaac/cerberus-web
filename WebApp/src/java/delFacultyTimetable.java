
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class delFacultyTimetable extends HttpServlet {

    private static final long serialVersionUID = -4628778484796197771L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String scheduleid = request.getParameter("scheduleid");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("Delete from `attendance` where `scheduleid` = ?;");
                    ps.setString(1, scheduleid);
                    ps.executeUpdate();
                    ps = con.prepareStatement("Delete from `facultytimetable` where `scheduleid` = ?;");
                    ps.setString(1, scheduleid);
                    ps.executeUpdate();
                    con.close();
                    dbLog(currUserName(request) + " deleted the Lab Session with scheduleID " + scheduleid);
                    messages a = new messages();
                    a.success(request, response, "The Lab Session has been marked as not conducted and its attendance has been deleted permanently.", "viewTimetable");
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
