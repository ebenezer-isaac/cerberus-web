
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class viewTimetable extends HttpServlet {

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
                    out.print("<table width = 100%>"
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
                    out.print("</tr></table><br><br>");
                    out.print("<p align='center'>Displaying Timetable of Week : " + week + "</p>");
                    out.print("<p align='center'>LAB 1 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(fac_printTimetable(1, week));
                    out.print("<p align='center'>LAB 2 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(fac_printTimetable(2, week));
                    out.print("<p align='center'>LAB 3 <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                    out.print(fac_printTimetable(3, week));
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

    public String fac_printTimetable(int labid, int week) {
        String timetable = "";
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
                                timetable += ("<td>" + lab1.getString(j + 3) + "</td>");
                            } else {
                                timetable += ("<td> <b>No Lab </b></td>");
                            }

                        }
                        timetable += ("</tr>");
                        lab1.next();
                    } else {
                        timetable += ("<tr align='center'>");
                        timetable += ("<th>" + slots[line][0] + "</th>");
                        timetable += ("<th>" + slots[line][1] + "</th>");
                        for (int j = 1; j <= 6; j++) {
                            timetable += ("<td> <b>No Lab <br>&nbsp</b></td>");
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
