
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editTimetable extends HttpServlet {

    int week;
    int no_of_subs = 0;
    String subs[] = new String[30];

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            if (access == 1) {
                request.getRequestDispatcher("nav.html").include(request, response);
                request.getRequestDispatcher("side-faculty.html").include(request, response);
            }

            week = (int) session.getAttribute("week");
            int selesem = 1;
            int lab = 1;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT `abbreviation` from `subject` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ");");
                while (rs.next()) {
                    subs[no_of_subs] = rs.getString(1);
                    no_of_subs++;
                }
                no_of_subs--;
                out.println("<style>"
                        + "input[type=number]{"
                        + "width: 62px;"
                        + "height: 40px;"
                        + "} "
                        + ".not-allowed {cursor: not-allowed;}"
                        + "</style>");
                out.print("<script>"
                        + "function zeroPad(num) {"
                        + "var s = num+'';"
                        + "while (s.length < 2) s = '0' + s;"
                        + "return(s);"
                        + "}"
                        + "function batchdisable(id) {"
                        + "var index = document.getElementById(id).selectedIndex;"
                        + "if(index == 0){"
                        + "id = id.substr(1);"
                        + "document.getElementById('batch' + id).selectedIndex=0;"
                        + "  document.getElementById('batch' + id).disabled=true;"
                        + "document.getElementById('batch' + id).classList.add('not-allowed');"
                        + "}"
                        + "else"
                        + "{id = id.substr(1);"
                        + "document.getElementById('batch' + id).disabled=false;"
                        + "document.getElementById('batch' + id).classList.remove('not-allowed');}"
                        + "}"
                        + "</script>");

                out.println("<style> th { white-space: nowrap; } </style>");
                LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                out.print("LAB 1. <b>Week: " + week + "</b> from <b>" + weekstart + "</b> to <b>" + endweek + "</b>");
                out.print("<form action='saveTimetable' method='post'>");
                out.print(printTimetable(1));
                out.print("<input type='text' name='lab' value='1' hidden>");
                out.print("<input type='submit' value='Submit' align='center'>");
                out.print("</form>");
                out.print("LAB 1. <b>Week: " + week + "</b> from <b>" + weekstart + "</b> to <b>" + endweek + "</b>");
                out.print("<form action='saveTimetable' method='post'>");
                out.print(printTimetable(2));
                out.print("<input type='text' name='lab' value='2' hidden>");
                out.print("<input type='submit' value='Submit' align='center'>");
                out.print("</form>");
                out.print("LAB 1. <b>Week: " + week + "</b> from <b>" + weekstart + "</b> to <b>" + endweek + "</b>");
                out.print("<form action='saveTimetable' method='post'>");
                out.print(printTimetable(3));
                out.print("<input type='text' name='lab' value='3' hidden>");
                out.print("<input type='submit' value='Submit' align='center'>");
                out.print("</form>");
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            out.println("</div></div></div></div></div><script src=\"js/Sidebar-Menu.js\"></script><script src=\"js/main.js\"></script>");
        }
    }

    public String printTimetable(int labID) {
        no_of_subs = 0;
        String table = "";
        table += ("<table class=\"table table-striped table-bordered\"> <thead>");
        table += ("<tr align = center>");
        table += ("<th style=\"white-space:nowrap;\" >Start_Time</th>");
        table += ("<th>End_Time</th>");
        table += ("<th>Monday</th>");
        table += ("<th>Tuesday</th>");
        table += ("<th>Wednesday</th>");
        table += ("<th>Thursday</th>");
        table += ("<th>Friday</th>");
        table += ("<th>Saturday</th>");
        table += ("</tr></thead><tbody>");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");

            PreparedStatement ps = con.prepareStatement("SELECT slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = 'mon' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 'tue' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 'wed' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 'thu' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 'fri' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 'sat' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Saturday "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "where labID=? and weekID=(select weekID from week where week = ?) "
                    + "GROUP BY slot.startTime, slot.endTime;");
            ps.setInt(1, labID);
            ps.setInt(2, week);
            ResultSet rs = ps.executeQuery();
            int line = 1;
            while (rs.next()) {
                table += ("<tr> ");
                table += ("<th><input type='number' style='border:1px solid ;' name='ts" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(0, 2))) + "'>");
                table += (" : <input type='number'  style='border:1px solid ;' name='ts" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(3, 5))) + "'></th>");
                table += ("<th><input type='number'  style='border:1px solid ;' name='te" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                table += (" : <input type='number'  style='border:1px solid ;' name='te" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                for (int j = 1; j <= 6; j++) {
                    table += ("<td align='center'>");
                    table += ("<select name = 'c" + line + "" + j + "' id = 'c" + line + "" + j + "'  onchange = 'batchdisable(this.id)'>");
                    table += ("<option name='Sub' value='-'");
                    String[] arrOfsub = null;
                    int flag = 0;
                    if (rs.getString(j + 2) == null) {
                        table += ("selected ");
                        flag = 0;
                    } else {
                        arrOfsub = rs.getString(j + 2).split(" - ");
                        flag = 1;
                    }
                    table += (">No Lab</option>");

                    for (int k = 0; k <= no_of_subs; k++) {
                        table += ("<option name='Sub' value='" + subs[k] + "' ");
                        if (flag == 1) {
                            if (subs[k].equals(arrOfsub[0])) {
                                table += ("selected ");
                            }
                        }
                        table += (">" + subs[k] + "</option>");
                    }
                    String batch[] = {"Batch A", "Batch B", "Batch C"};
                    table += ("</select>");
                    table += ("<select name = 'batch" + line + "" + j + "' id = 'batch" + line + "" + j + "'");
                    if (flag == 0) {
                        table += ("disabled class='not-allowed';");
                    }
                    table += ("><option name='-' value='-'");
                    if (flag == 0) {
                        table += ("selected");
                    }
                    table += (">No Batch</option>");
                    for (int x = 0; x <= batch.length - 1; x++) {
                        table += ("<option name='A' value='" + batch[x] + "'");
                        if (flag == 1) {
                            if (batch[x].equals(arrOfsub[1])) {
                                table += ("selected ");
                            }
                        }
                        table += (">" + batch[x] + "</option>");
                    }
                    table += ("</select>");
                    table += ("</td>");
                }
                table += ("</tr>");
                line++;
            }
            table += ("</tbody></table><br><br>");
        } catch (Exception e) {
            table=e.getMessage();
        }
        return table;
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
