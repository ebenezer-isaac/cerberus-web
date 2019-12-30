import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.get_schedule_det;
import static cerberus.printer.error;
import static cerberus.printer.kids;
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

public class ajaxSchedules extends HttpServlet {

    private static final long serialVersionUID = -4410831088721137083L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    String subject = request.getParameter("sub");
                    String batch = request.getParameter("bat");
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("select subject.subject from subject where subjectID = ?");
                        ps.setString(1, subject);
                        String subjectName = "";
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            subjectName = rs.getString(1);
                        }
                        ps = con.prepareStatement("select batch.name from batch where batchID = ?");
                        ps.setString(1, batch);
                        rs = ps.executeQuery();
                        String batchName = "";
                        if (rs.next()) {
                            batchName = rs.getString(1);
                        }
                        ps = con.prepareStatement("select timetable.scheduleID from timetable where timetable.subjectID = ? and timetable.batchID = ? and weekID !=0 order by timetable.weekID and timetable.dayID DESC");
                        ps.setString(1, subject);
                        ps.setString(2, batch);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            out.print(tablestart("List of Lab Session scheduled for " + batchName + " of " + subjectName + ""
                                    + "<div id='validations' style='color:red;font-size:14px;' class='mt-2 mb-2'>"
                                    + "Select a Lab Session listed below to mark it as conducted and to edit its Attendance.</div>", "hover", "studDetails", 1));
                            String header = "<tr>";
                            header += "<th>Date</th>";
                            header += "<th>Start Time</th>";
                            header += "<th>End Time</th>";
                            header += "<th>Lab</th>";
                            header += "<th>Subject ID</th>";
                            header += "<th>Subject</th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            while (rs.next()) {
                                System.out.println(rs.getInt(1));
                                out.print("<tr onclick=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + rs.getString(1) + "');\">");
                                String schedule[] = get_schedule_det(rs.getInt(1));
                                out.print("<td>" + schedule[0] + "</td>");
                                out.print("<td>" + schedule[1] + "</td>");
                                out.print("<td>" + schedule[2] + "</td>");
                                out.print("<td>" + schedule[3] + "</td>");
                                out.print("<td>" + schedule[4] + "</td>");
                                out.print("<td>" + schedule[5] + "</td>");
                                out.print("</tr>");
                            }
                            out.print(tableend(null, 1));
                        } else {
                            out.print("No Lab Sessions were Scheduled for " + subject + " " + batch + ".<br><br>You can schedule Lab Sessions from by <br><br>"
                                    + "<a href=\"javascript:setContent('/Cerberus/editTimetable');\">Editing Timetable</a>");
                        }
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        errorLogger(e.getMessage());
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
