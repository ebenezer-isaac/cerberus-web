
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editTimetable extends HttpServlet {

    int week = 0;
    int no_of_subs = 0;
    String subs[];

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    request.getRequestDispatcher("side-faculty.jsp").include(request, response);
                    try {
                        week = Integer.parseInt(request.getParameter("week"));
                    } catch (NumberFormatException e) {
                    }
                    if (week == 0) {
                        week = (int) session.getAttribute("week");
                    }
                    System.out.println(week);
                    int labid = Integer.parseInt(request.getParameter("lab"));
                    if (labid >= 4 || labid <= 0) {
                        labid = 1;
                    }
                    try {
                        int selesem = 1;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                            PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
                            st.setInt(1, week);
                            ResultSet rs2 = st.executeQuery();
                            while (rs2.next()) {
                                selesem = (rs2.getInt(1) % 2);
                                if (selesem == 0) {
                                    selesem += 2;
                                }
                            }
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT `Abbreviation` FROM `subject` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ") ORDER BY `subject`.`Abbreviation` ASC;");
                            while (rs.next()) {
                                no_of_subs++;
                            }
                            rs.first();
                            rs.previous();
                            no_of_subs++;
                            subs = new String[no_of_subs];
                            no_of_subs = 0;
                            while (rs.next()) {
                                subs[no_of_subs] = rs.getString(1);
                                no_of_subs++;
                            }
                            no_of_subs--;
                            out.print("<style>"
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
                                    + "return(s);}");
                            out.print("function batchdisable(id) {"
                                    + "var index = document.getElementById(id).selectedIndex;"
                                    + "if(index == 0)"
                                    + "{id = id.substr(1);"
                                    + "document.getElementById('batch' + id).selectedIndex=0;"
                                    + "document.getElementById('batch' + id).disabled=true;"
                                    + "document.getElementById('batch' + id).classList.add('not-allowed');}"
                                    + "else{id = id.substr(1);"
                                    + "document.getElementById('batch' + id).selectedIndex=1;"
                                    + "document.getElementById('batch' + id).disabled=false;"
                                    + "document.getElementById('batch' + id).classList.remove('not-allowed');}}");
                            out.print("function subsdisable(id) {"
                                    + "var index = document.getElementById(id).selectedIndex;"
                                    + "if(index == 0)"
                                    + "{id = id.substr(5);"
                                    + "document.getElementById('c' + id).selectedIndex=0;}"
                                    + "document.getElementById('batch' + id).disabled=true;"
                                    + "document.getElementById('batch' + id).classList.add('not-allowed');}"
                                    + "</script>");
                            out.print("<style> th { white-space: nowrap; } </style>");
                            out.print("<table width = 100%>"
                                    + "<tr><td width = 33% align='center'><form action='editTimetable' method='post'>"
                                    + "<input type='text' name='week' value='" + (week - 1) + "' hidden>"
                                    + "<input type='text' name='lab' value='" + labid + "' hidden>"
                                    + "<button type=\"submit\" id=\"prev\" class=\"btn btn-info\">"
                                    + "<span>Previous</span>"
                                    + "</button>"
                                    + "</form></td>"
                                    + "<td width = 33% align='center'>Current Week : " + session.getAttribute("week") + "</td>");
                            out.print("<td width = 33% align='center'><form action='editTimetable' method='post'>"
                                    + "<input type='text' name='week' value='" + (week + 1) + "' hidden>"
                                    + "<input type='text' name='lab' value='" + labid + "' hidden>"
                                    + "<button type=\"submit\" id=\"next\" class=\"btn btn-info\"");
                            if (week > Integer.parseInt(session.getAttribute("week").toString())) {
                                out.print("disabled");
                            }
                            out.print("><span>Next</span>"
                                    + "</button>"
                                    + "</form></td>");
                            out.print("</tr></table><br><br>");
                            out.print("<p align='center'>Displaying Timetable of Week : " + week + "</p>");
                            LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                            LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                            out.print("<p align='center'>LAB " + labid + " <br><b>" + weekstart + "</b> to <b>" + endweek + "</b></p>");
                            out.print("<form action='saveTimetable' method='post' align='right'>");
                            out.print(printTimetable(labid));
                            out.print("<input type='text' name='lab' value='" + labid + "' hidden>");
                            out.print("<button type=\"submit\" id=\"sub\" class=\"btn btn-info\">"
                                    + "<span>Save</span>"
                                    + "</button>");
                            out.print("</form>");
                            con.close();
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        messages m = new messages();
                        m.dberror(request, response, e.getMessage(), "homepage");
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

    public String printTimetable(int labID) {
        LocalDate mon = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
        LocalDate tue = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(2)));
        LocalDate wed = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(3)));
        LocalDate thu = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(4)));
        LocalDate fri = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(5)));
        LocalDate sat = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));

        String table = "";
        table += ("<table class=\"table table-striped table-bordered\"> <thead>");
        table += ("<tr align = center>");
        table += ("<th style=\"white-space:nowrap;\" >Start_Time</th>");
        table += ("<th>End_Time</th>");
        table += ("<th>Monday<br>" + mon + "</th>");
        table += ("<th>Tuesday<br>" + tue + "</th>");
        table += ("<th>Wednesday<br>" + wed + "</th>");
        table += ("<th>Thursday<br>" + thu + "</th>");
        table += ("<th>Friday<br>" + fri + "</th>");
        table += ("<th>Saturday<br>" + sat + "</th>");
        table += ("</tr></thead><tbody>");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");

            PreparedStatement ps = con.prepareStatement("SELECT slot.slotID, slot.startTime, slot.endTime, "
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
            String lines[] = new String[(no_of_slots + 2)];
            for (int y = 0; y <= no_of_slots; y++) {
                lines[y] = "";
            }
            while (rs.next()) {
                lines[rs.getInt(1) - 1] = ("<tr> ");
                lines[rs.getInt(1) - 1] += ("<th><input type='number' class=\"editTimeTimeTable\" name='ts" + (rs.getInt(1) - 1) + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                lines[rs.getInt(1) - 1] += (" : <input type='number'  class=\"editTimeTimeTable\" name='ts" + (rs.getInt(1) - 1) + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                lines[rs.getInt(1) - 1] += ("<th><input type='number'  class=\"editTimeTimeTable\" name='te" + (rs.getInt(1) - 1) + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(0, 2))) + "'>");
                lines[rs.getInt(1) - 1] += (" : <input type='number'  class=\"editTimeTimeTable\" name='te" + (rs.getInt(1) - 1) + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(3, 5))) + "'></th>");
                for (int j = 1; j <= 6; j++) {
                    lines[rs.getInt(1) - 1] += ("<td align='center'>");
                    lines[rs.getInt(1) - 1] += ("<select class=\"editSelectTimeTable\" name = 'c" + (rs.getInt(1) - 1) + "" + j + "' id = 'c" + (rs.getInt(1) - 1)+ "" + j + "'  onchange = 'batchdisable(this.id)'>");
                    lines[rs.getInt(1) - 1] += ("<option name='Sub' value='-'");
                    String[] arrOfsub = null;
                    int flag;
                    if (rs.getString(j + 3) == null) {
                        lines[rs.getInt(1) - 1] += ("selected ");
                        flag = 0;
                    } else {
                        arrOfsub = rs.getString(j + 3).split(" - ");
                        flag = 1;
                    }
                    lines[rs.getInt(1) - 1] += (">No Lab</option>");
                    for (int k = 0; k <= no_of_subs; k++) {
                        lines[rs.getInt(1) - 1] += ("<option name='Sub' value='" + subs[k] + "' ");
                        if (flag == 1) {
                            if (subs[k].equals(arrOfsub[0])) {
                                lines[rs.getInt(1) - 1] += ("selected ");
                            }
                        }
                        lines[rs.getInt(1) - 1] += (">" + subs[k] + "</option>");
                    }

                    lines[rs.getInt(1) - 1] += ("</select>");
                    lines[rs.getInt(1) - 1] += ("<select onchange = 'subsdisable(this.id)' class=\"editSelectTimeTable\" name = 'batch" + (rs.getInt(1) - 1) + "" + j + "' id = 'batch" + (rs.getInt(1) - 1) + "" + j + "'");
                    if (flag == 0) {
                        lines[rs.getInt(1) - 1] += ("disabled class='not-allowed';");
                    }
                    lines[rs.getInt(1) - 1] += ("><option name='-' value='-'");
                    if (flag == 0) {
                        lines[rs.getInt(1) - 1] += ("selected");
                    }
                    lines[rs.getInt(1) - 1] += (">No Batch</option>");
                    PreparedStatement ps11 = con.prepareStatement("Select name from batch");
                    ResultSet rs3 = ps11.executeQuery();
                    while (rs3.next()) {
                        lines[rs.getInt(1) - 1] += ("<option name='" + rs3.getString(1) + "' value='" + rs3.getString(1) + "'");
                        if (flag == 1) {
                            if (rs3.getString(1).equals(arrOfsub[1])) {
                                lines[rs.getInt(1) - 1] += ("selected ");
                            }
                        }
                        lines[rs.getInt(1) - 1] += (">" + rs3.getString(1) + "</option>");
                    }
                    lines[rs.getInt(1) - 1] += ("</select>");
                    lines[rs.getInt(1) - 1] += ("</td>");
                }
                lines[rs.getInt(1) - 1] += ("</tr>");
            }
            for (int y = 0; y <= no_of_slots; y++) {
                if (lines[y].equals("")) {
                    lines[y] = ("<tr> ");
                    lines[y] += ("<th><input type='number' style='border:1px solid ;' name='ts" + y + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(slots[y][0].substring(0, 2))) + "'>");
                    lines[y] += (" : <input type='number'  style='border:1px solid ;' name='ts" + y + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(slots[y][0].substring(3, 5))) + "'></th>");
                    lines[y] += ("<th><input type='number'  style='border:1px solid ;' name='te" + y + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(slots[y][1].substring(0, 2))) + "'>");
                    lines[y] += (" : <input type='number'  style='border:1px solid ;' name='te" + y + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(slots[y][1].substring(3, 5))) + "'></th>");
                    for (int j = 1; j <= 6; j++) {
                        lines[y] += ("<td align='center'>");
                        lines[y] += ("<select class=\"editSelectTimeTable\" name = 'c" + y + "" + j + "' id = 'c" + y + "" + j + "' onchange = 'batchdisable(this.id)'>");
                        lines[y] += ("<option name='Sub' value='-' selected>No Lab</option>");
                        for (int k = 0; k <= no_of_subs; k++) {
                            lines[y] += ("<option name='Sub' value='" + subs[k] + "'>" + subs[k] + "</option>");
                        }
                        String batch[] = {"Batch A", "Batch B", "Batch C"};
                        lines[y] += ("</select>");
                        lines[y] += ("<select class=\"editSelectTimeTable\" name = 'batch" + y+ "" + j + "' id = 'batch" + y + "" + j + "' disabled class='not-allowed'>");
                        lines[y] += ("<option name='-' value='-' selected>No Batch</option>");
                        for (int x = 0; x <= batch.length - 1; x++) {
                            lines[y] += ("<option name='A' value='" + batch[x] + "'>" + batch[x] + "</option>");
                        }
                        lines[y] += ("</select>");
                        lines[y] += ("</td>");
                    }
                    lines[y] += ("</tr>");
                }
                table += lines[y];
            }
            table += ("</tbody></table><br><br>");
            con.close();
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
        return table;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
