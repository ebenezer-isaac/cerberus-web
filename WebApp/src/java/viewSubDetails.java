
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

public class viewSubDetails extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            int access = (int) session.getAttribute("access");
            int labcount = 0;
            switch (access) {
                case 1:
                    String subcode = request.getParameter("subcode");
                    request.getRequestDispatcher("side-faculty.html").include(request, response);
                    out.println(subcode + " Details");
                    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
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
                        out.println("<br>Total number of labs: " + labcount);

                        PreparedStatement ps1 = con.prepareStatement("SELECT (select datedata.date from datedata where attendance.dateID=datedata.dateID) as date, \n"
                                + "slot.startTime, slot.endTime,\n"
                                + "(select lab.name from lab where lab.labID=timetable.labID) as lab,\n"
                                + "(select batch.name from batch where batch.batchID=timetable.batchID) as batch,\n"
                                + "(select faculty.name from faculty where faculty.facultyID=facultytimetable.facultyID) as teacher\n"
                                + "from facultytimetable\n"
                                + "INNER JOIN timetable\n"
                                + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                + "INNER JOIN slot\n"
                                + "on slot.slotID=timetable.slotID\n"
                                + "INNER JOIN attendance\n"
                                + "on attendance.scheduleID=facultytimetable.scheduleID\n"
                                + "where timetable.subjectID =? order by date");
                        ps1.setString(1, subcode);
                        ResultSet rs1 = ps1.executeQuery();
                        out.println("<table class=\"table table-striped table-bordered\"><thead>");
                        out.println("<tr align = center>");
                        out.println("<th>Date</th>");
                        out.println("<th>Start Time</th>");
                        out.println("<th>End Time</th>");
                        out.println("<th>Lab</th>");
                        out.println("<th>Batch</th>");
                        out.println("<th>Teacher Name</th>");
                        out.println("</tr></thead><tbody>");
                        while (rs1.next()) {
                            out.println("<tr align='center'>");
                            for (int i =1; i<=6;i++)
                            {
                             out.println("<td>"+rs1.getString(i)+"</td>");   
                            }
                            out.println("</tr>");
                        }
                        out.println("</tbody></table>");
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                case 0:
                    messages m1 = new messages();
                    m1.kids(request, response);
                    break;
                default:
                    messages m2 = new messages();
                    m2.nouser(request, response);
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
