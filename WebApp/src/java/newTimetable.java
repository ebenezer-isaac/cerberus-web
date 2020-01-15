
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getCurrWeekYear;
import static cerberus.AttFunctions.getWeekID;
import static cerberus.printer.error;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newTimetable extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String pwd = AttFunctions.hashIt(request.getParameter("pwd"));
            String weekYear[] = getCurrWeekYear().split(",");
            int week = Integer.parseInt(weekYear[1]);
            int year = Integer.parseInt(weekYear[0]);
            int weekid = getWeekID(week, year);
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps5 = con.prepareStatement("SELECT count(scheduleID) FROM timetable where weekID = ?");
            ps5.setInt(1, weekid);
            ResultSet rs = ps5.executeQuery();
            int currentWeekScheduleCount = 0;
            while (rs.next()) {
                currentWeekScheduleCount = rs.getInt(1);
            }
            ps5 = con.prepareStatement("SELECT count(scheduleID) FROM timetable where weekID = 0");
            rs = ps5.executeQuery();
            int masterWeekScheduleCount = 0;
            while (rs.next()) {
                masterWeekScheduleCount = rs.getInt(1);
            }
            if (currentWeekScheduleCount < masterWeekScheduleCount) {
                PreparedStatement ps9 = con.prepareStatement("SELECT scheduleID, slotID, dayID, labID FROM timetable where weekID = ?");
                ps9.setInt(1, 0);
                ResultSet rs1 = ps9.executeQuery();
                while (rs1.next()) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM timetable where slotID = ? and labID = ? and weekID = ? and dayID = ?");
                        ps.setInt(1, rs1.getInt(2));
                        ps.setInt(2, rs1.getInt(4));
                        ps.setInt(3, weekid);
                        ps.setInt(4, rs1.getInt(3));
                        ps.executeUpdate();
                        PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, ?, subjectID, batchID,? , dayID from timetable where scheduleID = ?");
                        ps3.setInt(1, rs1.getInt(4));
                        ps3.setInt(2, weekid);
                        ps3.setInt(3, rs1.getInt(1));
                        ps3.executeUpdate();
                    } catch (SQLException e) {
                    }
                }
                con.close();
            }
            con.close();
        } catch (NoSuchAlgorithmException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            error(e.getMessage());
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
