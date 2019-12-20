
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.error;
import static cerberus.printer.nouser;
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
import javax.servlet.http.HttpSession;

public class studSubAttendance extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1: {
                    String prn = request.getParameter("prn");
                    String subjectID = request.getParameter("sub");
                    if (prn != null && subjectID != null) {
                        String dates[][];
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            PreparedStatement ps = con.prepareStatement("select studentsubject.batchID from studentsubject where studentsubject.PRN = ? and studentsubject.subjectID = ?");
                            ps.setString(1, prn);
                            ps.setString(2, subjectID);
                            ResultSet rs = ps.executeQuery();
                            int batchID = 0;
                            while (rs.next()) {
                                batchID = rs.getInt(1);
                            }
                            ps = con.prepareStatement("SELECT (STR_TO_DATE(concat((select week.year from week where timetable.weekID = week.weekID),' ',(select week.week from week where timetable.weekID = week.weekID)-1,' ',timetable.dayID),'%X %V %w')) as date,"
                                    + " timetable.scheduleID as ScheduleID "
                                    + "from facultytimetable\n"
                                    + "INNER JOIN timetable\n"
                                    + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                    + "INNER JOIN slot\n"
                                    + "on slot.slotID=timetable.slotID\n"
                                    + "where timetable.subjectID =? and timetable.batchID=? order by date,slot.startTime;");
                            ps.setString(1, subjectID);
                            ps.setInt(2, batchID);
                            rs = ps.executeQuery();
                            int no_of_dates = 0;
                            while (rs.next()) {
                                no_of_dates++;
                            }
                            dates = new String[no_of_dates][2];
                            rs.first();
                            rs.previous();
                            int index = 0;
                            while (rs.next()) {
                                dates[index][0] = rs.getString(1);
                                dates[index][1] = rs.getString(2);
                                index++;
                            }
                            out.print("<form action='saveStudSubAttendance' method='post'>");
                            ps = con.prepareStatement("select subject.subject from subject where subject.subjectID = ?");
                            ps.setString(1, subjectID);
                            rs = ps.executeQuery();
                            String subject = "";
                            while (rs.next()) {
                                subject = rs.getString(1);
                            }
                            String batch = "";
                            ps = con.prepareStatement("select batch.name from batch where batch.batchID = ?");
                            ps.setInt(1, batchID);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                batch = rs.getString(1);
                            }
                            String name = "";
                            String roll = "";
                            ps = con.prepareStatement("select rollcall.rollno , student.name from student inner join rollcall on student.prn = rollcall.prn where student.prn = ?");
                            ps.setString(1, prn);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                roll = rs.getString(1);
                                name = rs.getString(2);
                            }
                            out.print(tablestart("<b>PRN</b> : " + prn + "<br>" + "<b>Roll</b> : " + roll + "<br>" + "<b>Name</b> : " + name + "<br><b>" + subject + " - " + batch + "</b>", "hover", "studDetails", 0) + "");
                            String header = "<tr align = center>";
                            header += "<th>Date</th>";
                            header += "<th>Status</th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            int temp = 0;
                            for (int i = 0; i < no_of_dates; i++) {
                                out.print("<tr><td>" + dates[i][0] + "</td><td>");
                                ps = con.prepareStatement("select attendance.attendanceID from attendance where attendance.PRN = ? and attendance.scheduleID=?");
                                ps.setString(1, prn);
                                ps.setInt(2, Integer.parseInt(dates[i][1]));
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    out.print("<center><input type='checkbox' value='1' id='" + temp + "' name='att" + (i + 1) + "," + dates[i][1] + "' checked ><label for='" + temp + "'></label></center>");
                                    temp++;
                                } else {
                                    out.print("<center><input type='checkbox' value='1' id='" + temp + "' name='att" + (i + 1) + "," + dates[i][1] + "' ><label for='" + temp + "'></label></center>");
                                    temp++;
                                }
                                out.print("</td>");
                                out.print("</tr>");
                            }
                            if (temp == 0) {
                                out.print("<tr><td colspan=2>No Labs were conducted</td></tr>");
                            }
                            String schedules = "";
                            if (dates.length > 1) {
                                for (int x = 0; x < (dates.length - 1); x++) {
                                    schedules += dates[x][1] + ",";
                                }
                                schedules += dates[dates.length - 1][1];
                            } else if (dates.length == 1) {
                                schedules = dates[0][1];
                            }
                            out.print(tableend("No of Labs : " + (dates.length) + "<br><br>"
                                    + "<input type='submit' value='Save' class='btn btn-primary' style='width: 200px;' align='center' id='subBtn'> <br><br>"
                                    + "<input type='text' name='prn' value='" + prn + "' hidden>"
                                    + "<input type='text' name='schedules' value='" + schedules + "' hidden>"
                                    + "<input type='text' name='subjectid' value='" + subjectID + "' hidden>"
                                    + "</form><style type='text/css'>\n"
                                    + "@import url('css/checkbox.css');\n"
                                    + "</style>", 0));

                            con.close();
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            error(e.getMessage());
                        }
                    } else {
                        out.print("<script>setContent('/Cerberus/viewTimetable');</script>");
                    }
                }
                break;
                case 0:
                    HttpSession session = request.getSession();
                    String prn = (String) session.getAttribute("user");
                    String subjectID = request.getParameter("sub");
                    if (prn != null && subjectID != null) {
                        String dates[][];
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            PreparedStatement ps = con.prepareStatement("select studentsubject.batchID from studentsubject where studentsubject.PRN = ? and studentsubject.subjectID = ?");
                            ps.setString(1, prn);
                            ps.setString(2, subjectID);
                            ResultSet rs = ps.executeQuery();
                            int batchID = 0;
                            while (rs.next()) {
                                batchID = rs.getInt(1);
                            }
                            ps = con.prepareStatement("SELECT (STR_TO_DATE(concat((select week.year from week where timetable.weekID = week.weekID),' ',(select week.week from week where timetable.weekID = week.weekID)-1,' ',timetable.dayID),'%X %V %w')) as date,"
                                    + " timetable.scheduleID as ScheduleID "
                                    + "from facultytimetable\n"
                                    + "INNER JOIN timetable\n"
                                    + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                    + "INNER JOIN slot\n"
                                    + "on slot.slotID=timetable.slotID\n"
                                    + "where timetable.subjectID =? and timetable.batchID=? order by date,slot.startTime;");
                            ps.setString(1, subjectID);
                            ps.setInt(2, batchID);
                            rs = ps.executeQuery();
                            int no_of_dates = 0;
                            while (rs.next()) {
                                no_of_dates++;
                            }
                            dates = new String[no_of_dates][2];
                            rs.first();
                            rs.previous();
                            int index = 0;
                            while (rs.next()) {
                                dates[index][0] = rs.getString(1);
                                dates[index][1] = rs.getString(2);
                                index++;
                            }
                            ps = con.prepareStatement("select subject.subject from subject where subject.subjectID = ?");
                            ps.setString(1, subjectID);
                            rs = ps.executeQuery();
                            String subject = "";
                            while (rs.next()) {
                                subject = rs.getString(1);
                            }
                            String batch = "";
                            ps = con.prepareStatement("select batch.name from batch where batch.batchID = ?");
                            ps.setInt(1, batchID);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                batch = rs.getString(1);
                            }
                            out.print(tablestart("<b>" + subject + " - " + batch + "</b>", "hover", "studDetails", 0) + "");
                            String header = "<tr align = center>";
                            header += "<th>Date</th>";
                            header += "<th>Status</th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            int temp = 0;
                            for (int i = 0; i < no_of_dates; i++) {
                                temp++;
                                out.print("<tr><td>" + dates[i][0] + "</td><td>");
                                ps = con.prepareStatement("select attendance.attendanceID from attendance where attendance.PRN = ? and attendance.scheduleID=?");
                                ps.setString(1, prn);
                                ps.setInt(2, Integer.parseInt(dates[i][1]));
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    out.print("P");
                                } else {
                                    out.print("-");
                                }
                                out.print("</td>");
                                out.print("</tr>");
                            }
                            if (temp == 0) {
                                out.print("<tr><td colspan=2>No Labs were conducted</td></tr>");
                            }
                            out.print(tableend("No of Labs : " + (dates.length) + "<br><br>", 0));
                            con.close();
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                            error(e.getMessage());
                        }
                    } else {
                        out.print("<script>setContent('/Cerberus/homepage');</script>");
                    }
                    break;
                default:
                    out.print(nouser());
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
