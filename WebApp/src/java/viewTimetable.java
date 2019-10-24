
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class viewTimetable extends HttpServlet {

    int no_of_class;
    String classsubs[][];

    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty()) {
            return false;
        }
        return true;
    }

    public int findclass(String subject) {
        subject = subject.substring(0, subject.indexOf(" "));
        for (int i = 0; i < no_of_class; i++) {
            for (int j = 0; j < 15; j++) {
                if (isNullOrEmpty(classsubs[i][j])) {
                       break;
                }
                else if(subject.equals(classsubs[i][j])) {
                    return i;
                }
            }
        }
        return 0;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            int week = 0;
            try {
                week = Integer.parseInt(request.getParameter("week"));
            } catch (NumberFormatException e) {
            }
            int access = (int) session.getAttribute("access");
            if (week == 0) {
                week = (int) session.getAttribute("week");
            }
            switch (access) {
                case 1:
                    request.getRequestDispatcher("side-faculty.jsp").include(request, response);
                    LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                    LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                    out.print("<script>"
                            + "function highlight(classid)"
                            + "{"
                            + "alert(classid);"
                            + "}"
                            + "</script>"
                            + "<table width = 100%>"
                            + "<tr><td width = 33% align='center'><form action='viewTimetable' method='post'>"
                            + "<input type='text' name='week' value='" + (week - 1) + "' hidden>"
                            + "<button type=\"submit\" id=\"prev\" class=\"btn btn-info\">"
                            + "<span>Previous</span>"
                            + "</button>"
                            + "</form></td>"
                            + "<td width = 33% align='center'>Current Week : " + session.getAttribute("week") + "</td>");
                    out.print("<td width = 33% align='center'><form action='viewTimetable' method='post'>"
                            + "<input type='text' name='week' value='" + (week + 1) + "' hidden>"
                            + "<button type=\"submit\" id=\"next\" class=\"btn btn-info\"");
                    if (week > Integer.parseInt(session.getAttribute("week").toString())) {
                        out.print("disabled");
                    }
                    out.print("><span>Next</span>"
                            + "</button>"
                            + "</form></td>");
                    out.print("</tr></table><br>");
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        out.print("<br><div align='center'>"
                                + "Student Class : ");
                        out.print("<select name = 'clas' id = 'clas' class=\"editSelect\" onchange='highlight(this.selectedIndex)'>");
                        out.print("<option name='clas' value= '0'>All Classes</option>");
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        no_of_class = 0;
                        while (rs.next()) {
                            no_of_class++;
                            out.print("<option name='clas' value= '" + no_of_class + "'>" + rs.getString(1) + "</option>");
                        }
                        out.print("</select>");
                    } catch (ClassNotFoundException | SQLException e) {

                    }
                    out.print("<br>");
                    out.print("<p align='center'>Displaying Timetable of Week : " + week + "</p>");
                    out.print("<p align='center'>LAB 1 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(printTimetable(1, week));
                    out.print("<p align='center'>LAB 2 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(printTimetable(2, week));
                    out.print("<p align='center'>LAB 3 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(printTimetable(3, week));
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;

                default:
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "true");
                    request.setAttribute("head", "Hey 'Kid'!");
                    request.setAttribute("body", "You are not authorized to view this page");
                    request.setAttribute("url", "homepage");
                    request.setAttribute("sec", "2");
                    rd.forward(request, response);
                    break;
            }
        }
    }

    public String printTimetable(int labid, int week) {
        String timetable = "";
        int temp = 0;
        LocalDate mon = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
        LocalDate tue = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(2)));
        LocalDate wed = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(3)));
        LocalDate thu = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(4)));
        LocalDate fri = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(5)));
        LocalDate sat = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                timetable += ("<table class=\"table table-striped table-bordered\"><thead>");
                timetable += ("<tr align = center>");
                timetable += ("<th>Start Time</th>");
                timetable += ("<th>End Time</th>");
                timetable += ("<th>Monday<br>" + mon + "</th>");
                timetable += ("<th>Tuesday<br>" + tue + "</th>");
                timetable += ("<th>Wednesday<br>" + wed + "</th>");
                timetable += ("<th>Thursday<br>" + thu + "</th>");
                timetable += ("<th>Friday<br>" + fri + "</th>");
                timetable += ("<th>Saturday<br>" + sat + "</th>");
                timetable += ("</tr></thead><tbody>");
                PreparedStatement ps4 = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                        + "MAX(CASE WHEN dayID = 'mon' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Monday, "
                        + "MAX(CASE WHEN dayID = 'tue' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Tuesday, "
                        + "MAX(CASE WHEN dayID = 'wed' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Wednesday, "
                        + "MAX(CASE WHEN dayID = 'thu' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Thursday, "
                        + "MAX(CASE WHEN dayID = 'fri' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Friday, "
                        + "MAX(CASE WHEN dayID = 'sat' THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Saturday "
                        + "FROM timetable "
                        + "INNER JOIN slot "
                        + "ON timetable.slotID = slot.slotID "
                        + "where labID=? and weekID=(select weekID from week where week = ?) "
                        + "GROUP BY slot.startTime, slot.endTime;");
                ps4.setInt(1, labid);
                ps4.setInt(2, week);
                ResultSet lab1 = ps4.executeQuery();
                PreparedStatement ps7 = con.prepareStatement("SELECT * from slot");
                ResultSet rs1 = ps7.executeQuery();
                String slots[][];
                int no_of_slots = 0;
                while (rs1.next()) {
                    no_of_slots++;
                }
                rs1.first();
                rs1.previous();
                slots = new String[no_of_slots][2];
                no_of_slots = 0;
                while (rs1.next()) {
                    slots[no_of_slots][0] = rs1.getString(2).substring(0, 5);
                    slots[no_of_slots][1] = rs1.getString(3).substring(0, 5);
                    no_of_slots++;
                }
                PreparedStatement ps8 = con.prepareStatement("SELECT * from slot");
                ResultSet rs3 = ps8.executeQuery();
                classsubs = new String[no_of_class][15];
                while (rs1.next()) {
                    no_of_slots++;
                }
                rs1.first();
                rs1.previous();
                slots = new String[no_of_slots][2];
                no_of_slots = 0;
                while (rs1.next()) {
                    slots[no_of_slots][0] = rs1.getString(2).substring(0, 5);
                    slots[no_of_slots][1] = rs1.getString(3).substring(0, 5);
                    no_of_slots++;
                }
                no_of_slots--;
                int line = 0;
                lab1.next();
                while (line <= no_of_slots) {
                    if (lab1.getInt(1) == (line + 1)) {
                        timetable += ("<tr align='center'>");
                        timetable += ("<th>" + slots[line][0] + "</th>");
                        timetable += ("<th>" + slots[line][1] + "</th>");
                        for (int j = 1; j <= 6; j++) {
                            if (lab1.getString(j + 3) != null) {
                                int cla = findclass(lab1.getString(j + 3));
                                timetable += ("<td><p id = 'subclass" + cla + "" + temp + "'>" + lab1.getString(j + 3) + " </p></td>");
                                temp++;
                            } else {
                                timetable += ("<td><p id = 'nolab'>No Lab </p></td>");
                            }

                        }
                        timetable += ("</tr>");
                        lab1.next();
                    } else {
                        timetable += ("<tr align='center'>");
                        timetable += ("<th>" + slots[line][0] + "</th>");
                        timetable += ("<th>" + slots[line][1] + "</th>");
                        for (int j = 1; j <= 6; j++) {
                            timetable += ("<td><p id = 'nolab'>No Lab</p></td>");
                        }
                        timetable += ("</tr>");
                    }
                    line++;
                }
                timetable += ("</tbody></table><br><br>");
                con.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
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
