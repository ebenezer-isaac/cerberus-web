import cerberus.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editTimetable extends HttpServlet {

    int week = 0;
    int no_of_subs = 0;
    String subs[][];

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    try {
                        week = Integer.parseInt(request.getParameter("week"));
                    } catch (NumberFormatException e) {
                    }
                    if (week == 0) {
                        week = (int) session.getAttribute("week");
                    }
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
                            ResultSet rs = stmt.executeQuery("SELECT subjectID,`Abbreviation` FROM `subject` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ") ORDER BY `subject`.`Abbreviation` ASC;");
                            while (rs.next()) {
                                no_of_subs++;
                            }
                            rs.first();
                            rs.previous();
                            no_of_subs++;
                            subs = new String[no_of_subs][2];
                            no_of_subs = 0;
                            while (rs.next()) {
                                subs[no_of_subs][0] = rs.getString(1);
                                subs[no_of_subs][1] = rs.getString(2);
                                no_of_subs++;
                            }
                            no_of_subs--;
                            out.print("<style>"
                                    + "input[type=number]{"
                                    + "width: 61px;"
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
                                    + "<tr><td width = 33% align='center'><form action=\"javascript:setContent('/Cerberus/editTimetable?week=" + (week - 1) + "&lab=" + labid + "')\">"
                                    + "<button type=\"submit\" id=\"prev\" class=\"btn btn-info\"");
                            if (week <= Integer.parseInt(session.getAttribute("week").toString())) {
                                out.print("disabled");
                            }
                            out.print("><span>Previous</span>"
                                    + "</button>"
                                    + "</form></td>"
                                    + "<td width = 33% align='center'>Current Week : " + session.getAttribute("week") + "</td>");
                            out.print("<td width = 33% align='center'><form action=\"javascript:setContent('/Cerberus/editTimetable?week=" + (week + 1) + "&lab=" + labid + "')\">"
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
                            out.print("<div class=\"table-responsive\">");
                            out.print("<form id='ajaxform' action='saveTimetable' method='post'    2 align='right'>");
                            out.print(printTimetable(labid));
                            out.print("<input type='text' name='lab' value='" + labid + "' hidden>");
                            out.print("<input type='text' name='week' value='" + week + "' hidden>");
                            out.print("<button type=\"submit\" id=\"sub\" class=\"btn btn-info\">"
                                    + "<span>Save</span>"
                                    + "</button>");
                            out.print("</form>");
                            out.print("</div>");
                            con.close();
                        } catch (ParseException ex) {
                            Logger.getLogger(editTimetable.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        messages m = new messages();
                        m.dberror(request, response, e.getMessage(), "homepage");
                    }
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

    public String printTimetable(int labID) throws ParseException {
        String date[] = new String[6];
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy");
        Calendar cal = Calendar.getInstance();
        Date d = new Date();
        int year = d.getYear();
        cal.set(Calendar.WEEK_OF_YEAR, week - 1);
        cal.set(Calendar.YEAR, year);
        cal.setFirstDayOfWeek(Calendar.SATURDAY);
        cal.set(Calendar.DAY_OF_WEEK, 6);
        date[0] = dt.format(cal.getTime());
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, 0);
        date[1] = dt.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, 1);
        date[2] = dt.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, 2);
        date[3] = dt.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, 3);
        date[4] = dt.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, 4);
        date[5] = dt.format(cal.getTime());
        String table = "";
        table += ("<table class=\"table table-hover table-bordered\"> <thead style=\"font-size: 13.5px; background-color: #f0f2f5;\">");
        table += ("<tr align = center>");
        table += ("<th style=\"white-space:nowrap;\" >Start Time</th>");
        table += ("<th>End Time</th>");
        table += ("<th>Monday<br>" + date[0] + "</th>");
        table += ("<th>Tuesday<br>" + date[1] + "</th>");
        table += ("<th>Wednesday<br>" + date[2] + "</th>");
        table += ("<th>Thursday<br>" + date[3] + "</th>");
        table += ("<th>Friday<br>" + date[4] + "</th>");
        table += ("<th>Saturday<br>" + date[5] + "</th>");
        table += ("</tr></thead><tbody>");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");

            PreparedStatement ps = con.prepareStatement("SELECT slot.slotID, slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = 1 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 2 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 3 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 4 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 5 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 6 THEN concat(timetable.subjectID,' - ',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Saturday "
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
                String time = rs.getString(2);
                lines[rs.getInt(1) - 1] += ("<th>" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + " : " + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "</th>");
                lines[rs.getInt(1) - 1] += ("<th>" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(0, 2))) + " : " + String.format("%02d", Integer.parseInt(rs.getString(3).substring(3, 5))) + "</th>");
                for (int j = 1; j <= 6; j++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
                    String dateInString = date[j - 1] + " " + time;
                    Date datetime = sdf.parse(dateInString);
                    Date now = new Date();

                    long nowmill = now.getTime();
                    long datetimemill = datetime.getTime();
                    String disabled = "";
                    String disabledstyl = "style='border:solid 1px green'";
                    if (nowmill > datetimemill) {
                        disabled = " disabled ";
                        disabledstyl = "style='border:solid 1px red'";
                    }
                    lines[rs.getInt(1) - 1] += ("<td align='center'>");
                    lines[rs.getInt(1) - 1] += ("<select class=\"editSelectTimeTable\" name = 'c" + (rs.getInt(1) - 1) + "" + j + "' id = 'c" + (rs.getInt(1) - 1) + "" + j + "'  onchange = 'batchdisable(this.id)' " + disabledstyl + ">");
                    lines[rs.getInt(1) - 1] += ("<option name='Sub' value='-'");
                    String[] arrOfsub = null;
                    int flag;
                    if (rs.getString(j + 3) == null) {
                        lines[rs.getInt(1) - 1] += ("selected ");
                        flag = 0;
                    } else {
                        lines[rs.getInt(1) - 1] += (disabled);
                        arrOfsub = rs.getString(j + 3).split(" - ");
                        flag = 1;
                    }
                    lines[rs.getInt(1) - 1] += (">No Lab</option>");
                    for (int k = 0; k <= no_of_subs; k++) {
                        lines[rs.getInt(1) - 1] += ("<option name='Sub' value='" + subs[k][0] + "' ");
                        if (flag == 1) {
                            if (subs[k][0].equals(arrOfsub[0])) {
                                lines[rs.getInt(1) - 1] += ("selected ");
                            } else {
                                lines[rs.getInt(1) - 1] += (disabled);
                            }
                        }
                        if (flag == 0) {
                            lines[rs.getInt(1) - 1] += (disabled);
                        }
                        lines[rs.getInt(1) - 1] += (">" + subs[k][1] + "</option>");
                    }

                    lines[rs.getInt(1) - 1] += ("</select>");
                    lines[rs.getInt(1) - 1] += ("<select onchange = 'subsdisable(this.id)' class=\"editSelectTimeTable\" name = 'batch" + (rs.getInt(1) - 1) + "" + j + "' id = 'batch" + (rs.getInt(1) - 1) + "" + j + "'");
                    if (flag == 0) {
                        lines[rs.getInt(1) - 1] += ("disabled  class='not-allowed';");
                    }
                    lines[rs.getInt(1) - 1] += ("><option name='-' value='-'");
                    if (flag == 0) {
                        lines[rs.getInt(1) - 1] += ("selected");
                    } else {
                        lines[rs.getInt(1) - 1] += (disabled);
                    }
                    lines[rs.getInt(1) - 1] += (">No Batch</option>");
                    PreparedStatement ps11 = con.prepareStatement("Select name from batch");
                    ResultSet rs3 = ps11.executeQuery();
                    int index = 1;
                    while (rs3.next()) {
                        lines[rs.getInt(1) - 1] += ("<option name='" + rs3.getString(1) + "' value='" + index + "'");
                        if (flag == 1) {
                            if (rs3.getString(1).equals(arrOfsub[1])) {
                                lines[rs.getInt(1) - 1] += ("selected ");
                            } else {
                                lines[rs.getInt(1) - 1] += (disabled);
                            }
                        }
                        if (flag == 0) {
                            lines[rs.getInt(1) - 1] += (disabled);
                        }
                        lines[rs.getInt(1) - 1] += (">" + rs3.getString(1) + "</option>");
                        index++;
                    }
                    lines[rs.getInt(1) - 1] += ("</select>");
                    lines[rs.getInt(1) - 1] += ("</td>");
                }
                lines[rs.getInt(1) - 1] += ("</tr>");
            }
            for (int y = 0; y <= no_of_slots; y++) {
                if (lines[y].equals("")) {
                    lines[y] = ("<tr> ");
                    lines[y] += ("<th>" + String.format("%02d", Integer.parseInt(slots[y][0].substring(0, 2))) + " : " + String.format("%02d", Integer.parseInt(slots[y][0].substring(3, 5))) + "</th>");
                    lines[y] += ("<th>" + String.format("%02d", Integer.parseInt(slots[y][1].substring(0, 2))) + " : " + String.format("%02d", Integer.parseInt(slots[y][1].substring(3, 5))) + "</th>");
                    for (int j = 1; j <= 6; j++) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
                        String dateInString = date[j - 1] + " " + slots[y][0] + ":00";
                        Date datetime = sdf.parse(dateInString);
                        Date now = new Date();
                        long nowmill = now.getTime();
                        long datetimemill = datetime.getTime();
                        String disabled = "";
                        String disabledstyl = "style='border:solid 1px green'";
                        if (nowmill > datetimemill) {
                            disabled = " disabled ";
                            disabledstyl = "style='border:solid 1px red'";
                        }
                        lines[y] += ("<td align='center'>");
                        lines[y] += ("<select class=\"editSelectTimeTable\" name = 'c" + y + "" + j + "' id = 'c" + y + "" + j + "' onchange = 'batchdisable(this.id)' " + disabledstyl + ">");
                        lines[y] += ("<option " + disabled + " name='Sub' value='-' selected>No Lab</option>");
                        for (int k = 0; k <= no_of_subs; k++) {
                            lines[y] += ("<option " + disabled + " name='Sub' value='" + subs[k][0] + "'>" + subs[k][1] + "</option>");
                        }
                        lines[y] += ("</select>");
                        lines[y] += ("<select class=\"editSelectTimeTable\" name = 'batch" + y + "" + j + "' id = 'batch" + y + "" + j + "' disabled class='not-allowed'>");
                        lines[y] += ("<option " + disabled + " name='-' value='-' selected>No Batch</option>");
                        PreparedStatement ps11 = con.prepareStatement("Select name from batch");
                        ResultSet rs3 = ps11.executeQuery();
                        int index = 1;
                        while (rs3.next()) {
                            lines[y] += ("<option " + disabled + " name='A' value='" + index + "'>" + rs3.getString(1) + "</option>");
                            index++;
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
