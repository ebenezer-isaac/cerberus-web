
import static cerberus.AttFunctions.prefSubs;
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
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class viewTimetable extends HttpServlet {

    String heading;
    int week = 0;
    int temp = 0;
    String[] subs;
    int access;
    HttpServletResponse response;
    HttpServletRequest request;
    int no_of_class;
    String mon, tue, wed, thu, fri, sat;
    String wks;
    String wke;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar cal = Calendar.getInstance();
            Date d = new Date();
            int year = d.getYear();
            cal.set(Calendar.WEEK_OF_YEAR, week - 1);
            cal.set(Calendar.YEAR, year);
            cal.setFirstDayOfWeek(Calendar.SATURDAY);
            cal.set(Calendar.DAY_OF_WEEK, 6);
            mon = sdf.format(cal.getTime());
            cal.set(Calendar.WEEK_OF_YEAR, week);
            cal.set(Calendar.DAY_OF_WEEK, 0);
            tue = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_WEEK, 1);
            wed = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_WEEK, 2);
            thu = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_WEEK, 3);
            fri = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_WEEK, 4);
            sat = sdf.format(cal.getTime());
            HttpSession session = request.getSession();
            try {
                week = Integer.parseInt(request.getParameter("week"));
            } catch (NumberFormatException e) {
            }
            access = (int) session.getAttribute("access");
            if (week == 0) {
                week = (int) session.getAttribute("week");
            }
            switch (access) {
                case 1:
                    LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                    LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                    wks = weekstart.toString();
                    wke = endweek.toString();
                    out.print("<style>"
                            + ".bold {"
                            + " font-weight: bold;"
                            + "}"
                            + "</style>"
                            + "<script>"
                            + "function highlight(classid)"
                            + "{"
                            + "if(classid==1)"
                            + "{"
                            + "$(\"div[id^='fav']\").addClass('bold');"
                            + "$(\"div[id^='nolab']\").removeClass('bold');"
                            + "$(\"div[id^='subclass']\").removeClass('bold');"
                            + "}"
                            + "else if (classid==2)"
                            + "{"
                            + "$(\"div[id^='nolab']\").addClass('bold');"
                            + "$(\"div[id^='fav']\").removeClass('bold');"
                            + "$(\"div[id^='subclass']\").removeClass('bold');"
                            + "}"
                            + "else{classid = classid+-2;"
                            + "$(\"div[id^='fav']\").removeClass('bold');"
                            + "$(\"div[id^='nolab']\").removeClass('bold');"
                            + "$(\"div[id^='subclass']\").removeClass('bold');"
                            + "$(\"div[id^='subclass\" + classid + \"']\").addClass('bold');"
                            + "}}"
                            + "function changestyle(index)"
                            + "{"
                            + "if(index==0)"
                            + "{"
                            + "document.getElementById('lab_timetable').style.display = 'block';"
                            + "document.getElementById('timetable').style.display = 'none';"
                            + "}"
                            + "else"
                            + "{"
                            + "document.getElementById('lab_timetable').style.display = 'none';"
                            + "document.getElementById('timetable').style.display = 'block';"
                            + "}}"
                            + "</script>");
                    heading = "<table width = 100%>"
                            + "<tr><td width = 33% align='center'><form action=\"javascript:setContent('/Cerberus/viewTimetable?week=" + (week - 1) + "')\" >"
                            + "<button type=\"submit\" id=\"prev\" class=\"btn btn-primary\">"
                            + "<span>Previous</span>"
                            + "</button>"
                            + "</form></td>"
                            + "<td width = 33% align='center'>Current Week : " + session.getAttribute("week") + "<p align='center'>Displaying Timetable of Week : " + week + "</p></td>"
                            + "<td width = 33% align='center'><form action=\"javascript:setContent('/Cerberus/viewTimetable?week=" + (week + 1) + "');\">"
                            + "<button type=\"submit\" id=\"next\" class=\"btn btn-primary\"";
                    if (week > Integer.parseInt(session.getAttribute("week").toString())) {
                        heading += "disabled";
                    }
                    heading += "><span>Next</span>"
                            + "</button>"
                            + "</form></td>";
                    heading += "</tr></table>";
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        subs = prefSubs(request, null);
                        out.print("Display Style : <select name = 'timetable_type' id = 'timetable_type' class=\"editSelect\" onchange='changestyle(this.selectedIndex)'>");
                        out.print("<option name='clas' value= '0'>Lab Wise</option>");
                        out.print("<option name='clas' value= '1'>Combined</option>");
                        out.print("</select><br><br>");
                        out.print("Student Class : <select name = 'clas' id = 'clas' class=\"editSelect\" onchange='highlight(this.selectedIndex)'>");
                        out.print("<option name='clas' value= '0'>None</option>");
                        out.print("<option name='clas' value= '0'>Preferences</option>");
                        out.print("<option name='clas' value= '0'>No Labs</option>");
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        no_of_class = 0;
                        while (rs.next()) {
                            no_of_class++;
                            out.print("<option name='clas' value= '" + no_of_class + "'>" + rs.getString(1) + "</option>");
                        }
                        out.print("</select><br><br>");
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                    
                    out.print("<div id='lab_timetable'>");
                    out.print(lab_printTimetable(1));
                    out.print(lab_printTimetable(2));
                    out.print(lab_printTimetable(3));
                    out.print("</div>");
                    out.print("<div id='timetable' style='display: none;'>");
                    out.print(printTimetable());
                    out.print("</div>");
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

    public String lab_printTimetable(int labid) {
        String timetable = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            timetable += (tablestart(heading + "<p align='center'>LAB " + labid + " <br><b>" + wks + "</b> to <b>" + wke + "</b></p>", "hover", "studDetails", 0));
            String header = ("<tr align = center>");
            header += ("<th>Start Time</th>");
            header += ("<th>End Time</th>");
            header += ("<th>Monday<br>" + mon + "</th>");
            header += ("<th>Tuesday<br>" + tue + "</th>");
            header += ("<th>Wednesday<br>" + wed + "</th>");
            header += ("<th>Thursday<br>" + thu + "</th>");
            header += ("<th>Friday<br>" + fri + "</th>");
            header += ("<th>Saturday<br>" + sat + "</th>");
            header += ("</tr>");
            timetable += (tablehead(header));
            PreparedStatement ps4 = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Saturday "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "where labID=? and weekID=(select weekID from week where week = ?) "
                    + "GROUP BY slot.startTime, slot.endTime ASC "
                    + "ORDER BY slot.startTime, slot.endTime ASC;");
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
            String lines[] = new String[(no_of_slots + 2)];
            for (int y = 0; y <= no_of_slots; y++) {
                lines[y] = "";
            }
            while (lab1.next()) {
                lines[lab1.getInt(1) - 1] = ("<tr align='center'>");
                lines[lab1.getInt(1) - 1] += ("<th>" + slots[lab1.getInt(1) - 1][0] + "</th>");
                lines[lab1.getInt(1) - 1] += ("<th>" + slots[lab1.getInt(1) - 1][1] + "</th>");
                for (int j = 1; j <= 6; j++) {
                    if (lab1.getString(j + 3) != null) {
                        String[] arrOfStr = lab1.getString(j + 3).split(",");
                        if (isfav(lab1.getString(j + 3))) {
                            lines[lab1.getInt(1) - 1] += ("<td><div id = 'fav" + temp + "'><div id = 'subclass" + arrOfStr[1] + "" + temp + "'>" + arrOfStr[0] + " </div> </div></td>");
                        } else {
                            lines[lab1.getInt(1) - 1] += ("<td><div id = 'subclass" + arrOfStr[1] + "" + temp + "'>" + arrOfStr[0] + " </div></td>");

                        }
                    } else {
                        lines[lab1.getInt(1) - 1] += ("<td><div id = 'nolab" + temp + "'>No Lab </div></td>");
                    }
                    temp++;
                }
                lines[lab1.getInt(1) - 1] += ("</tr>");
            }
            for (int y = 0; y <= no_of_slots; y++) {
                if (lines[y].equals("")) {
                    lines[y] = ("<tr align='center'>");
                    lines[y] += ("<th>" + slots[y][0] + "</th>");
                    lines[y] += ("<th>" + slots[y][1] + "</th>");
                    for (int j = 1; j <= 6; j++) {
                        lines[y] += ("<td><div id = 'nolab" + temp + "'>No Lab</div></td>");
                        temp++;
                    }
                    lines[y] += ("</tr>");
                }
                timetable += lines[y];
            }
            timetable += (tableend(null,0));
            con.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            timetable = e.getMessage();
        }
        return timetable;
    }

    public String printTimetable() {
        String timetable = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            timetable += (tablestart(heading, "hover", "studDetails", 0));
            String header = ("<tr align = center>");
            header += ("<th>Start Time</th>");
            header += ("<th>End Time</th>");
            header += ("<th>Lab</th>");
            header += ("<th>Monday<br>" + mon + "</th>");
            header += ("<th>Tuesday<br>" + tue + "</th>");
            header += ("<th>Wednesday<br>" + wed + "</th>");
            header += ("<th>Thursday<br>" + thu + "</th>");
            header += ("<th>Friday<br>" + fri + "</th>");
            header += ("<th>Saturday<br>" + sat + "</th>");
            header += ("</tr>");
            timetable += (tablehead(header));
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
            PreparedStatement ps8 = con.prepareStatement("SELECT * from lab");
            ResultSet rs2 = ps8.executeQuery();
            int no_of_labs = 0;
            while (rs2.next()) {
                no_of_labs++;
            }
            String labs[][] = new String[no_of_labs][(no_of_slots + 2)];
            for (int l = 0; l <= no_of_labs - 1; l++) {
                PreparedStatement ps4 = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                        + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Monday, "
                        + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Tuesday, "
                        + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Wednesday, "
                        + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Thursday, "
                        + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Friday, "
                        + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID)) END) as Saturday "
                        + "FROM timetable "
                        + "INNER JOIN slot "
                        + "ON timetable.slotID = slot.slotID "
                        + "where labID=? and weekID=(select weekID from week where week = ?) "
                        + "GROUP BY slot.startTime, slot.endTime;");
                ps4.setInt(1, l + 1);
                ps4.setInt(2, week);
                ResultSet lab1 = ps4.executeQuery();
                for (int y = 0; y <= no_of_slots; y++) {
                    labs[l][y] = "";
                }
                while (lab1.next()) {
                    labs[l][lab1.getInt(1) - 1] = "";
                    for (int j = 1; j <= 6; j++) {
                        if (lab1.getString(j + 3) != null) {
                            String[] arrOfStr = lab1.getString(j + 3).split(",");
                            if (isfav(lab1.getString(j + 3))) {
                                labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'fav" + temp + "'><div id = 'subclass" + arrOfStr[1] + "" + temp + "'>" + arrOfStr[0] + " </div> </div></td>");
                            } else {
                                labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'subclass" + arrOfStr[1] + "" + temp + "'>" + arrOfStr[0] + " </div></td>");
                            }
                        } else {
                            labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'nolab" + temp + "'>No Lab </div></td>");
                        }
                        temp++;
                    }
                }
                for (int y = 0; y <= no_of_slots; y++) {
                    if (labs[l][y].equals("")) {
                        labs[l][y] = "";
                        for (int j = 1; j <= 6; j++) {
                            labs[l][y] += ("<td><div id = 'nolab" + temp + "'>No Lab </div></td>");
                            temp++;
                        }
                    }
                }
            }
            con.close();

            int slot = 0;
            while (slot <= no_of_slots) {
                timetable += ("<tr align='center'>");
                timetable += ("<th style='vertical-align : middle;text-align:center;' rowspan='4'>" + slots[slot][0] + "</th>");
                timetable += ("<th style='vertical-align : middle;text-align:center;' rowspan='4'>" + slots[slot][1] + "</th></tr>");
                for (int lab = 0; lab <= no_of_labs - 1; lab++) {
                    timetable += ("<td  align='center'>Lab " + (lab + 1) + "</td>");
                    timetable += (labs[lab][slot]);
                    timetable += ("</tr>");
                }
                timetable += ("</tr>");
                slot++;
            }
            timetable += (tableend(null,0));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return timetable;
    }

    public boolean isfav(String subject) {
        for (int i = 0; i <= subs.length - 1; i++) {
            int index = subject.indexOf(subs[i]);
            if (index != -1) {
                return true;
            }
        }
        return false;
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
