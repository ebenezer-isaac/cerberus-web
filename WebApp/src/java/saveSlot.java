
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import java.sql.PreparedStatement;

public class saveSlot extends HttpServlet {

    private static final long serialVersionUID = 7604818262598475744L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps4 = con.prepareStatement("SELECT `slotID`,`startTime`,`endTime` from `slot` order by startTime,endTime ASC;");
                        ResultSet rs = ps4.executeQuery();
                        while (rs.next()) {
                            String exist = rs.getString(2) + " , " + rs.getString(3);
                            String stime = request.getParameter("stime" + rs.getString(1)) + ":00";
                            String etime = request.getParameter("etime" + rs.getString(1)) + ":00";
                            String table = stime + " , " + etime;
                            if (exist.equals(table)) {
                            } else {
                                PreparedStatement pps = con.prepareStatement("UPDATE `slot` SET `startTime`=?,`endTime`=? WHERE slotID=?");
                                pps.setString(1, stime);
                                pps.setString(2, etime);
                                pps.setInt(3, rs.getInt(1));
                                pps.executeUpdate();
                                dbLog(currUserName(request) + " edited slot timings for slotID : " + rs.getInt(1) + " to " + stime + " - " + etime);
                            }
                        }
                        con.close();
                        messages a = new messages();
                        a.success(request, response, "Your changes to timing have been saved", "viewTimetable");
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
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
