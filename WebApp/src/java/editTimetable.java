
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

public class editTimetable extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("nav.html").include(request, response);
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            if (access == 1) {
                out.print("Faculty Panel </li>"
                        + "<li> <a href=\"/Cerberus/editTimetable\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Timetable Management </a> </li>\n"
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
            String subs[] = new String[30];
            int no_of_subs = 0;
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("w");
            int week = Integer.parseInt(ft.format(date));
            int selesem = 1;
            int lab = 1;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT `subjectID` from `subject` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ");");
                while (rs.next()) {
                    subs[no_of_subs] = rs.getString(1);
                    no_of_subs++;
                }
                no_of_subs--;
                out.println("<style>"
                        + "input[type=number]{"
                        + "width: 65px;"
                        + "} "
                        + "</style>");
                out.println("<style> th { white-space: nowrap; } </style>");
//                out.print("<script>"
//                        + "function zeroPad(num) {"
//                        + "var s = num+'';"
//                        + "while (s.length < 2) s = '0' + s;"
//                        + "return(s);"
//                        + "}"
//                        + "</script>");
                out.print("<body align = 'center'><br><br>");
                out.print("<form action='saveTimetable' method='post'>");
                out.print("<table class=\"table table-bordered\"><thead>");
                out.print("<tr align = center>");
                out.print("<th style=\"white-space:nowrap;\" >Start_Time</th>");
                out.print("<th>End_Time</th>");
                out.print("<th>Monday</th>");
                out.print("<th>Tuesday</th>");
                out.print("<th>Wednesday</th>");
                out.print("<th>Thursday</th>");
                out.print("<th>Friday</th>");
                out.print("<th>Saturday</th>");
                out.print("</tr></thead><tbody>");
                PreparedStatement ps = con.prepareStatement("SELECT slot.startTime, slot.endTime, "
                        + "MAX(CASE WHEN dayID = 'mon' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Monday, "
                        + "MAX(CASE WHEN dayID = 'tue' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Tuesday, "
                        + "MAX(CASE WHEN dayID = 'wed' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Wednesday, "
                        + "MAX(CASE WHEN dayID = 'thu' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Thursday, "
                        + "MAX(CASE WHEN dayID = 'fri' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Friday, "
                        + "MAX(CASE WHEN dayID = 'sat' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Saturday "
                        + "FROM timetable "
                        + "INNER JOIN slot "
                        + "ON timetable.slotID = slot.slotID "
                        + "where labID=? and weekID=(select weekID from week where week = ?) "
                        + "GROUP BY slot.startTime, slot.endTime;");
                ps.setInt(1, 1);
                ps.setInt(2, week);
                rs = ps.executeQuery();
                int line = 1;
                while (rs.next()) {
                    out.print("<tr> ");
                    out.print("<th><input type='number' style='border:1px solid ;' name='ts" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(0, 2))) + "'>");
                    out.print(" : <input type='number'  style='border:1px solid ;' name='ts" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(3, 5))) + "'></th>");
                    out.print("<th><input type='number'  style='border:1px solid ;' name='te" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                    out.print(" : <input type='number'  style='border:1px solid ;' name='te" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                    for (int j = 1; j <= 6; j++) {
                        out.print("<td align='center'>");
                        out.print("<select name = 'c" + line + "" + j + "' id = 'c" + line + "" + j + "'>");
                        out.print("<option name='Sub' value='-'>No Lab</option>");
                        for (int k = 0; k <= no_of_subs; k++) {
                            out.print("<option name='Sub' value='" + subs[k] + "' ");
                            if (subs[k].equals(rs.getString(j + 2))) {
                                out.print("selected ");
                            }
                            out.print(">" + subs[k] + "</option>");
                        }
                        out.print("</select>");
                         out.print("<select name = 'batch" + line + "" + j + "' id = 'batch" + line + "" + j + "'>");
                        out.print("<option name='A' value='-'>Batch-A</option>");
                        out.print("<option name='B' value='-'>Batch-B</option>");
                        out.print("<option name='C' value='-'>Batch-C</option>");
                        out.print("</select>");
                        out.print("</td>");
                    }
                    out.print("</tr>");
                    line++;
                }
                out.print("</tbody></table><br><br>");
                out.print("<input type='text' name='lab' value='" + lab + "' hidden>");
                out.print("<input type='submit' value='Submit' align='center'>");
                out.print("</form>");
                out.println("<form action='homepage' method='post'>");
                out.println("<input type='submit' value='Back'>");
                out.println("</form>");
                out.println("</div></div></div></div></div><script src=\"js/Sidebar-Menu.js\"></script><script src=\"js/main.js\"></script>");
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
        }
    }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}