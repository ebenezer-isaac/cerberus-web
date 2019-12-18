
import static cerberus.AttFunctions.getAccess;
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
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;

public class viewSubDetails extends HttpServlet {

    private static final long serialVersionUID = -8921011621421523305L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    int labcount = 0;
                    try {
                        String subcode = request.getParameter("subcode");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("SELECT count(facultytimetable.scheduleID)\n"
                                + "from facultytimetable \n"
                                + "INNER JOIN timetable\n"
                                + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                + "where timetable.subjectID = ?");
                        ps.setString(1, subcode);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            labcount = rs.getInt(1);
                        }
                        out.print("<br>Total number of labs Conducted : " + labcount+"<br>");
                        if (labcount >= 1) {
                            PreparedStatement ps1 = con.prepareStatement("SELECT (STR_TO_DATE(concat((select week.year from week where timetable.weekID = week.weekID),' ',(select week.week from week where timetable.weekID = week.weekID)-1,' ',timetable.dayID),'%X %V %w')) as date, \n"
                                    + "slot.startTime, slot.endTime,\n"
                                    + "(select lab.name from lab where lab.labID=timetable.labID) as lab,\n"
                                    + "(select batch.name from batch where batch.batchID=timetable.batchID) as batch,\n"
                                    + "(select faculty.name from faculty where faculty.facultyID=facultytimetable.facultyID) as teacher, timetable.scheduleid \n"
                                    + "from facultytimetable\n"
                                    + "INNER JOIN timetable\n"
                                    + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                    + "INNER JOIN slot\n"
                                    + "on slot.slotID=timetable.slotID\n"
                                    + "where timetable.subjectID =? order by date,slot.startTime;");
                            ps1.setString(1, subcode);
                            ResultSet rs1 = ps1.executeQuery();
                            out.print(tablestart(subcode + " Details", "hover", "studDetails", 1) + "");
                            String header = "<tr align = center>";
                            header += "<th>Date</th>";
                            header += "<th>Start Time</th>";
                            header += "<th>End Time</th>";
                            header += "<th>Lab</th>";
                            header += "<th>Batch</th>";
                            header += "<th>Teacher Name</th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            while (rs1.next()) {
                                out.print("<tr align='center' onclick = \"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + rs1.getString(7) + "');\">");
                                for (int i = 1; i <= 6; i++) {
                                    out.print("<td>" + rs1.getString(i) + "</td>");
                                }
                                out.print("</tr>");
                            }
                            out.print(tableend(null, 1));
                        } else {
                            out.print("No Data to show");
                        }
                        con.close();
                    } catch (SQLException e) {
                        error(e.getMessage());
                    }
                    break;
                case 0:
                    out.print(kids());
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
