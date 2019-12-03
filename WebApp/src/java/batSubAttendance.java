
import static cerberus.printer.error;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class batSubAttendance extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int batchID = Integer.parseInt(request.getParameter("batchID"));
            String subjectID = request.getParameter("subjectID");
            String sql = "SELECT rollcall.rollNo as Roll,student.name as Name,";
            String dates[][];
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("SELECT (STR_TO_DATE(concat(YEAR(CURDATE()),' ',timetable.weekID,' ',timetable.dayID),'%X %V %w')) as date,"
                        + " timetable.scheduleID as ScheduleID"
                        + ",(select faculty.name from faculty where faculty.facultyID=facultytimetable.facultyID) as teacher "
                        + "from facultytimetable\n"
                        + "INNER JOIN timetable\n"
                        + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                        + "INNER JOIN slot\n"
                        + "on slot.slotID=timetable.slotID\n"
                        + "where timetable.subjectID =? and timetable.batchID=? order by date,slot.startTime;");
                ps.setString(1, subjectID);
                ps.setInt(2, batchID);
                ResultSet rs = ps.executeQuery();
                int no_of_dates = 0;
                while (rs.next()) {
                    no_of_dates++;
                }
                System.out.println("NO of dates :" + no_of_dates);
                dates = new String[no_of_dates][3];
                rs.first();
                rs.previous();
                int index = 0;
                while (rs.next()) {
                    dates[index][0] = rs.getString(1);
                    dates[index][1] = rs.getString(2);
                    dates[index][2] = rs.getString(3);
                    index++;
                }
                ps = con.prepareStatement("select rollcall.rollNo, studentsubject.PRN, student.name from studentsubject INNER JOIN rollcall on studentsubject.PRN = rollcall.PRN INNER JOIN student on student.PRN = studentsubject.PRN where studentsubject.subjectID = ? and studentsubject.batchID = ?");
                ps.setString(1, subjectID);
                ps.setInt(2, batchID);
                rs = ps.executeQuery();
                String studs[][];
                int no_of_studs = 0;
                while (rs.next()) {
                    no_of_studs++;
                }
                studs = new String[no_of_studs][3];
                rs.first();
                rs.previous();
                index = 0;
                while (rs.next()) {
                    studs[index][0] = rs.getString(1);
                    studs[index][1] = rs.getString(2);
                    studs[index][2] = rs.getString(3);
                    index++;
                }
                System.out.println("NO of studs :" + no_of_studs);
                out.print(tablestart(subjectID + " " + batchID + " Details", "hover", "studDetails", 1) + "");
                String header = "<tr align = center>";
                header += "<th>Roll</th>";
                header += "<th>Name</th>";
                for (int i = 0; i < no_of_dates; i++) {
                    header += "<th>" + dates[i][0] + "</th>";
                }
                header += "</tr>";
                out.print(tablehead(header));
                for (int i = 0; i < no_of_studs; i++) {
                    out.print("<tr><td>" + studs[i][0] + "</td><td>" + studs[i][2] + "</td>");
                    for (int j = 0; j < no_of_dates; j++) {
                        out.print("<td>");
                        ps = con.prepareStatement("select attendance.attendanceID from attendance where attendance.PRN = ? and attendance.scheduleID=?");
                        ps.setString(1, studs[i][1]);
                        ps.setInt(2, Integer.parseInt(dates[j][1]));
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            out.print("P");
                        } else {
                            out.print("-");
                        }
                        out.print("</td>");
                    }
                    out.print("</tr>");
                }
                out.print(tableend(null, 1));
                con.close();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                error(e.getMessage());
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
