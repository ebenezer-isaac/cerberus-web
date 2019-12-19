
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.get_schedule_det;
import cerberus.messages;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class saveNewAttendance extends HttpServlet {

    private static final long serialVersionUID = 5124640296287249007L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    try {
                        HttpSession session = request.getSession(false);
                        int scheduleid = Integer.parseInt(request.getParameter("scheduleid"));
                        int facultyid = Integer.parseInt(session.getAttribute("user").toString());
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            PreparedStatement ps = con.prepareStatement("insert into facultytimetable values(?,?)");
                            ps.setInt(1, scheduleid);
                            ps.setInt(2, facultyid);
                            ps.executeUpdate();
                        } catch (ClassNotFoundException | SQLException x) {
                            messages b = new messages();
                            b.error(request, response, x.getMessage(), "viewTimetable");
                        }
                        out.print(tableend(null, 1));
                        messages a = new messages();
                        a.success(request, response, "Lab has been marked as conducted. Redirecting to edit page.", "rapidAttendance?scheduleid=" + scheduleid);
                    } catch (NumberFormatException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "viewTimetable");
                    }
                    break;

                case 0:
                    messages b = new messages();
                    b.kids(request, response);
                    break;
                default:
                    messages c = new messages();
                    c.nouser(request, response);
            }
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
