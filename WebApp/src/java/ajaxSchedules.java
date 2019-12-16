
import static cerberus.AttFunctions.get_schedule_det;
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
import java.sql.Statement;
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
            String subject = request.getParameter("sub");
            String batch = request.getParameter("bat");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("select timetable.scheduleID from timetable where timetable.subjectID = ? and timetable.batchID = ? order by timetable.weekID and timetable.dayID");
                ps.setString(1, subject);
                ps.setString(2, batch);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    out.print(tablestart("Lab Sessions", "hover", "studDetails", 0));
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
                    out.print(tableend(null, 0));
                }else{out.print("No Lab Sessions were Scheduled for "+subject+" "+batch+".<br>You can schedule Lab Sessions from by <a href=\"javascript:setContent('/Cerberus/editTimetable');\">Editing Timetable</a>");}
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                error(e.getMessage());
            }
            out.print("");
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
