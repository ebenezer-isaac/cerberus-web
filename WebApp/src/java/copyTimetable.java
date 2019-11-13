
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getWeekID;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class copyTimetable extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                HttpSession session = request.getSession();
                int week = (int) session.getAttribute("week");
                week++;
                int weekid = getWeekID(week);
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    int flag = 0;
                    PreparedStatement ps = con.prepareStatement("DELETE FROM timetable where weekID = ?");
                    ps.setInt(1, weekid);
                    ps.executeUpdate();
                    PreparedStatement ps10 = con.prepareStatement("SELECT weekID FROM `week` ORDER BY `week`.`weekID` DESC");
                    ResultSet rs = ps10.executeQuery();
                    while (rs.next() && flag == 0) {
                        PreparedStatement ps9 = con.prepareStatement("SELECT * FROM timetable where weekID = ?");
                        ps9.setInt(1, rs.getInt(1));
                        ResultSet rs1 = ps9.executeQuery();
                        while (rs1.next()) {
                            PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID,? , dayID from timetable where weekID = ?;");
                            ps3.setInt(1, weekid);
                            ps3.setInt(2, rs.getInt(1));
                            ps3.executeUpdate();
                            try {
                                ps3 = con.prepareStatement("DELETE t1 FROM timetable t1 "
                                        + "INNER JOIN timetable t2 "
                                        + "WHERE "
                                        + "t1.scheduleID < t2.scheduleID AND "
                                        + "t1.slotID = t2.slotID AND "
                                        + "t1.labID = t2.labID AND "
                                        + "t1.weekID = t2.weekID AND "
                                        + "t1.dayID = t2.dayID;");
                                ps3.executeUpdate();
                            } catch (Exception e) {
                            }
                            flag = 1;
                            break;
                        }
                    }
                    con.close();
                    messages a = new messages();
                    a.success(request, response, "TimeTable has copied into next week", "viewTimetable?week=" + week);
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
