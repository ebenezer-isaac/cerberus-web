
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class rapidAttendance extends HttpServlet {

    private static final long serialVersionUID = -2432453047898760987L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    try {
                        int scheduleid = Integer.parseInt(request.getParameter("scheduleid"));
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            PreparedStatement ps = con.prepareStatement("select rollcall.rollNo , student.name, studentsubject.prn from timetable inner join studentsubject on timetable.batchID = studentsubject.batchID and timetable.subjectID = studentsubject.subjectID inner join student on student.PRN = studentsubject.PRN inner join rollcall on student.PRN = rollcall.PRN where timetable.scheduleID=?");
                            ps.setInt(1, scheduleid);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                String schedule[] = get_schedule_det(rs.getInt(1));
                                out.print("<form action='saveRapidAttendance' method='post'>");
                                String head = "";
                                head += ("<table align='center' width = 30%><tr><td align='center' width = 14%><b>Date</b></td><td align='center' width = 2%> : </td><td align='center' width = 14%>" + schedule[0] + "</td></tr>");
                                head += ("<tr><td align='center'><b>Start Time</b></td><td align='center'> : </td><td align='center'>" + schedule[1] + "</td></tr>");
                                head += ("<tr><td align='center'><b>End Time</b></td><td align='center'> : </td><td align='center'>" + schedule[2] + "</td></tr>");
                                head += ("<tr><td align='center'><b>Lab</b></td><td align='center'> : </td><td align='center'>" + schedule[3] + "</td></tr>");
                                head += ("<tr><td align='center'><b>Subject ID</b></td><td align='center'> : </td><td align='center'>" + schedule[4] + "</td></tr>");
                                head += ("<tr><td align='center'><b>Subject</b></td><td align='center'> : </td><td align='center'>" + schedule[5] + "</td></tr></table>");
                                out.print(tablestart("" + head, "hover", "studDetails", 0));
                                String header = "<tr>";
                                header += "<th>Roll No</th>";
                                header += "<th>Name</th>";
                                header += "<th>Status</th>";
                                header += "</tr>";
                                out.print(tablehead(header));
                                rs.previous();
                                int line = 1;
                                while (rs.next()) {
                                    out.print("<td>" + rs.getString(1) + "</td><td>" + rs.getString(2) + "</td>"
                                            + "<td><input type='text' name = 'prn" + line + "' value='" + rs.getString(3) + "' hidden>");
                                    ps = con.prepareStatement("select attendance.attendanceID from attendance where attendance.PRN = ? and attendance.scheduleID=?");
                                    ps.setString(1, rs.getString(3));
                                    ps.setInt(2, scheduleid);
                                    ResultSet rs1 = ps.executeQuery();
                                    if (rs1.next()) {
                                        out.print("<center><input type='checkbox' value='1' id='warn" + line + "' name='att" + line + "' checked ><label for='warn" + line + "'></label></center>");
                                    } else {
                                        out.print("<center><input type='checkbox' value='1' id='warn" + line + "' name='att" + line + "' ><label for='warn" + line + "'></label></center>");
                                    }
                                    out.print("</td></tr>");
                                    line++;
                                }
                                out.print(tableend("No of students : " + (line-1) + "<br><br>"
                                        + "<input type='submit' value='Save' class='btn btn-primary' style='width: 200px;' align='center' id='subBtn'> <br><br>"
                                        + "<input type='text' name='scheduleid' value='" + scheduleid + "' hidden><input type='text' name='line' value='" + line + "' hidden>"
                                        + "</form>", 1));
                            } else {
                                out.print("No students belonging to batch have opted for this subject");
                            }
                        } catch (ClassNotFoundException | SQLException x) {
                            messages b = new messages();
                            b.error(request, response, x.getMessage(), "viewTimetable");
                        }
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