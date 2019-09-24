
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class homepage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("nav.html").include(request, response);
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            if (access == 1) {
                out.print("Faculty Panel </li>"
                        + "<li> <a href=\"#\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Timetable Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-list\"></i> &nbsp;  Subjects Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-list-alt\"></i> &nbsp;  Student Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-folder-open\"></i> &nbsp;  Attendance Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-user-cog\"></i> &nbsp; Admin Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-upload\"></i> &nbsp; Student Progression </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Device OTP </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Profile </a> </li>\n"
                        + "</ul></div>");
                out.print("<div class=\"page-content-wrapper\">\n"
                        + "<button class=\"btn btn-link\" id=\"menu-toggle\" style=\"background-color: #0d0d0d;\"> <i class=\"fas fa-align-justify\" style=\"color: white;\"></i> </button>\n"
                        + "<div class=\"row\">"
                        + "<div class=\"col-md-12\">\n"
                        + "<div class=\"container my-5\" style=\"padding: 0 70px;\">\n"
                        + "");
                out.println("hello");
                out.println("</div></div></div></div></div><script src=\"js/Sidebar-Menu.js\"></script><script src=\"js/main.js\"></script>");
                out.println(printTimetable(1));
                out.println(printTimetable(1));
                out.println(printTimetable(1));
            } else {
                out.print("Student Panel </li>"
                        + "<li> <a href=\"#\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Attendance</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-list\"></i> &nbsp; Profile </a> </li>\n"
                        + "</ul></div>");
            }
        }
    }

    public String printTimetable(int labid) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("w");
        String timetable = "";
        int week = Integer.parseInt(ft.format(date));
        timetable += ("<table class=\"table table-striped table-bordered\"><thead>");
        timetable += ("<tr align = center>");
        timetable += ("<th>Start_Time</th>");
        timetable += ("<th>End_Time</th>");
        timetable += ("<th>Monday</th>");
        timetable += ("<th>Tuesday</th>");
        timetable += ("<th>Wednesday</th>");
        timetable += ("<th>Thursday</th>");
        timetable += ("<th>Friday</th>");
        timetable += ("<th>Saturday</th>");
        timetable += ("</tr></thead><tbody>");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT slot.startTime, slot.endTime,"
                    + "MAX(CASE WHEN dayID = 'mon' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Monday,"
                    + "MAX(CASE WHEN dayID = 'tue' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Tuesday,"
                    + "MAX(CASE WHEN dayID = 'wed' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Wednesday,"
                    + "MAX(CASE WHEN dayID = 'thu' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Thursday,"
                    + "MAX(CASE WHEN dayID = 'fri' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Friday,"
                    + "MAX(CASE WHEN dayID = 'sat' THEN concat(timetable.subjectID,' - ',timetable.batchID) END) as Saturday "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "where labID=? and weekID=(select weekID from week where week = ?) "
                    + "GROUP BY slot.startTime, slot.endTime;");
            ps.setInt(1, labid);
            ps.setInt(2, week);
            ResultSet lab1 = ps.executeQuery();
            while (lab1.next()) {
                timetable += ("<tr align='center'>");
                timetable += ("<th>" + lab1.getString(1).substring(0, 5) + "</th>");
                timetable += ("<th>" + lab1.getString(2).substring(0, 5) + "</th>");
                for (int j = 1; j <= 6; j++) {
                    timetable += ("<td>" + lab1.getString(j + 2) + "</td>");
                }
                timetable += ("</tr>");
            }
            timetable += ("</td></tr></tbody></table><br><br>");
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            timetable = e.getMessage();
        }
        return timetable;
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
