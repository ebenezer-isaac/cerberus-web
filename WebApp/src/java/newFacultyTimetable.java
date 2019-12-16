
import static cerberus.AttFunctions.get_schedule_det;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newFacultyTimetable extends HttpServlet {

    private static final long serialVersionUID = -3963568346101198223L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                int scheduleID = Integer.parseInt(request.getParameter("scheduleid"));
                String schedule[] = get_schedule_det(scheduleID);
                out.print("<fieldset>");
                out.print("<table align='center' width = 30%><tr><td align='center' width = 14%><b>Date</b></td><td align='center' width = 2%> : </td><td align='center' width = 14%>" + schedule[0] + "</td></tr>");
                out.print("<tr><td align='center'><b>Start Time</b></td><td align='center'> : </td><td align='center'>" + schedule[1] + "</td></tr>");
                out.print("<tr><td align='center'><b>End Time</b></td><td align='center'> : </td><td align='center'>" + schedule[2] + "</td></tr>");
                out.print("<tr><td align='center'><b>Lab</b></td><td align='center'> : </td><td align='center'>" + schedule[3] + "</td></tr>");
                out.print("<tr><td align='center'><b>Subject ID</b></td><td align='center'> : </td><td align='center'>" + schedule[4] + "</td></tr>");
                out.print("<tr><td align='center'><b>Subject</b></td><td align='center'> : </td><td align='center'>" + schedule[5] + "</td></tr></table>"
                        + "<br><font style=\"font-size: 20px;\"><b> Warning - The following changes will be made:</b></font><br><br>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 1. The selected Lab will be marked as conducted. </font> </p>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 2. Any attendance marked via the fingerprint system will be void. </font> </p>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 3. Attendance marked by will be overwritten with express authority. </font> </p>"
                        + "<br><input type='checkbox' id='warn'onclick='myFunction()'/> <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font>"
                        + "<br></fieldset>");
                out.print("<br><div id = 'butt' style='display:none;'><form action='saveNewFacultyTimetable' method='post'>"
                        + "<input type='text' value='" + scheduleID + "' hidden>"
                        + "<button type='submit' style='width:200px;' class='btn btn-primary'>Submit</button></form></div>");
            } catch (NumberFormatException e) {
                String subjectid = request.getParameter("subjectid");
                int batch = 0;
                try {
                    batch = Integer.parseInt(request.getParameter("batch"));
                } catch (NumberFormatException d) {
                    batch = 1;
                }
                if (subjectid != null) {
                    out.print("This page will display list of labs conducted / will be conducted today.");
                    out.print(subjectid);
                } else {
                   //get subject and batch id
                }
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
