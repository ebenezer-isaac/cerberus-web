
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getWeekID;
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

public class copyTimetable extends HttpServlet {

    private static final long serialVersionUID = 1307628222749536806L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String asdf = request.getParameter("modweekyear");
                String splitt[] = asdf.split("-W");
                System.out.println("mod week year : "+asdf);
                int modyear = Integer.parseInt(splitt[0]);
                int modweek = Integer.parseInt(splitt[1]);
                int year = Integer.parseInt(request.getParameter("year"));
                int week = Integer.parseInt(request.getParameter("week"));
                int labid = Integer.parseInt(request.getParameter("lab"));
                int modlabid = Integer.parseInt(request.getParameter("modlab"));
                int modweekid = getWeekID(modweek, modyear);
                int weekid = getWeekID(week, year);
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps9 = con.prepareStatement("SELECT scheduleID, slotID, dayID FROM timetable where weekID = ? and labID = ?");
                    ps9.setInt(1, modweekid);
                    ps9.setInt(2, modlabid);
                    ResultSet rs1 = ps9.executeQuery();
                    if (rs1.next()) {
                        rs1.first();
                        rs1.previous();
                        while (rs1.next()) {
                            System.out.println(rs1.getInt(1) + "," + rs1.getInt(2) + "," + rs1.getInt(3));
                            try {
                                PreparedStatement ps = con.prepareStatement("DELETE FROM timetable where slotID = ? and labID = ? and weekID = ? and dayID = ?");
                                ps.setInt(1, rs1.getInt(2));
                                ps.setInt(2, labid);
                                ps.setInt(3, weekid);
                                ps.setInt(4, rs1.getInt(3));
                                ps.executeUpdate();
                                PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, ?, subjectID, batchID,? , dayID from timetable where scheduleID = ?");
                                ps3.setInt(1, labid);
                                ps3.setInt(2, weekid);
                                ps3.setInt(3, rs1.getInt(1));
                                ps3.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        con.close();
                        if (weekid == 0) {
                            messages a = new messages();
                            a.success(request, response, "TimeTable has been copied into the Master Timetable", "viewMasterTimetable");
                        } else {
                            messages a = new messages();
                            a.success(request, response, "TimeTable has been copied into the selected week", "viewTimetable?week=" + week + "&year=" + year);
                        }

                    } else {
                        con.close();
                        messages a = new messages();
                        a.failed(request, response, "Timetable for Selected Week is already Empty", "viewTimetable?week=" + week + "&year=" + year);
                    }
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
