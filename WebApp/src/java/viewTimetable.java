
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrWeekYear;
import static cerberus.AttFunctions.getNextWeekYear;
import static cerberus.AttFunctions.getPrevWeekYear;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.no_of_labs;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.prefSubs;
import static cerberus.printer.nouser;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.threeten.extra.YearWeek;

public class viewTimetable extends HttpServlet {

    private static final long serialVersionUID = 1318699662544398556L;

    String heading;
    int week = 0, year = 0;
    int temp = 0;
    String[] subs;
    int access;
    int classID = 0;
    HttpServletResponse response;
    HttpServletRequest request;
    int no_of_class;
    LocalDate wks, mon, tue, wed, thu, fri, sat, wke;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            access = getAccess(request);
            if (access < 2) {
                HttpSession session = request.getSession(false);
                String user = (String) session.getAttribute("user");
                try {
                    week = Integer.parseInt(request.getParameter("week"));
                    year = Integer.parseInt(request.getParameter("year"));
                } catch (NumberFormatException e) {
                    String weekYear[] = getCurrWeekYear().split(",");
                    week = Integer.parseInt(weekYear[1]);
                    year = Integer.parseInt(weekYear[0]);
                }
                YearWeek weekYeardate = YearWeek.of(year, week);
                mon = weekYeardate.atDay(DayOfWeek.MONDAY);
                tue = weekYeardate.atDay(DayOfWeek.TUESDAY);
                wed = weekYeardate.atDay(DayOfWeek.WEDNESDAY);
                thu = weekYeardate.atDay(DayOfWeek.THURSDAY);
                fri = weekYeardate.atDay(DayOfWeek.FRIDAY);
                sat = weekYeardate.atDay(DayOfWeek.SATURDAY);
                wks = weekYeardate.atDay(DayOfWeek.MONDAY).plusDays(-1);
                wke = wks.plusDays(7);
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
                heading = "<div class='row'>"
                        + "<div class='col-xl-4 col-sm-6 mb-3 mt-3' align='center'><form action=\"";
                String prevweekYear[] = getPrevWeekYear(week, year).split(",");
                int prevweek = Integer.parseInt(prevweekYear[1]);
                int prevyear = Integer.parseInt(prevweekYear[0]);
                String weekYear[] = getCurrWeekYear().split(",");
                int currweek = Integer.parseInt(weekYear[1]);
                String nextweekYear[] = getNextWeekYear(week, year).split(",");
                int nextweek = Integer.parseInt(nextweekYear[1]);
                int nextyear = Integer.parseInt(nextweekYear[0]);
                heading += "javascript:setContent('/Cerberus/viewTimetable?week=" + prevweek + "&year=" + prevyear + "')\" >";
                heading += "<button type=\"submit\" id=\"prev\" class=\"btn btn-primary\">"
                        + "<span>Previous</span>"
                        + "</button>"
                        + "</form></div>"
                        + "<div class='col-xl-4 col-sm-6' align='center'>Current Week : " + currweek + "<p align='center'>Displaying Timetable of Week : " + week + "</p></div>"
                        + "<div class='col-xl-4 col-sm-6 mb-3' align='center'><form action=\"";
                heading += "javascript:setContent('/Cerberus/viewTimetable?week=" + nextweek + "&year=" + nextyear + "')\" >";
                heading += "<button type=\"submit\" id=\"next\" class=\"btn btn-primary\"><span>Next</span>"
                        + "</button>"
                        + "</form></div>";
                heading += "</div>" + "<p align='center'><b>" + wks + "</b> to <b>" + wke + "</b></p>";
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    subs = prefSubs(request, null);
                    out.print("Display Style : <select name = 'timetable_type' id = 'timetable_type' class=\"editSelect\" onchange='changestyle(this.selectedIndex)'>");
                    out.print("<option name='clas' value= '0'>Lab Wise</option>");
                    out.print("<option name='clas' value= '1'>Combined</option>");
                    out.print("</select><br><br>");
                    out.print("Highlight Type: <select name = 'clas' id = 'clas' class=\"editSelect\" onchange='highlight(this.selectedIndex)'>");
                    out.print("<option name='clas' value= '0'>None</option>");
                    out.print("<option name='clas' value= '0'>Preferences</option>");
                    out.print("<option name='clas' value= '0'>No Labs</option>");
                    if (access != 0) {
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        no_of_class = 0;
                        while (rs.next()) {
                            no_of_class++;
                            if (getSem(oddEve(), no_of_class) != 0) {
                                out.print("<option name='clas' value= '" + no_of_class + "'>" + rs.getString(1) + "</option>");
                            }
                        }
                        out.print("</select><br><br><a href=\"downTimetable?week=" + week + "&year=" + year + "\"><i class=\"fas fa-file-download\"></i>&nbsp;&nbsp;&nbsp;Download</a><br><br>");
                    }
                    if (access == 0) {
                        PreparedStatement ps = con.prepareStatement("SELECT rollcall.classID from rollcall where prn=?");
                        ps.setString(1, user);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            classID = rs.getInt(1);
                        }
                        out.print("</select><br><br>");
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    errorLogger(e.getMessage());
                }
                out.print("<div id='lab_timetable'>");
                int no_of_lab = no_of_labs();
                for (int y = 1; y <= no_of_lab; y++) {
                    out.print(lab_printTimetable(y));
                }
                out.print("</div>");
                out.print("<div id='timetable' style='display: none;'>");
                out.print(printTimetable());
                out.print("</div>");
            } else {
                out.print(nouser());
            }
        }
    }

    public String lab_printTimetable(int labid) {
        String timetable = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            timetable += (tablestart(heading + "<p align='center'><b>LAB " + labid + " </b><br></p>", "hover", "studDetails", 0));
            String header = ("<tr align = center>");
            header += ("<th style='vertical-align : middle;text-align:center;'>Start Time</th>");
            header += ("<th style='vertical-align : middle;text-align:center;'>End Time</th>");
            header += ("<th>Monday<br>" + mon + "</th>");
            header += ("<th>Tuesday<br>" + tue + "</th>");
            header += ("<th>Wednesday<br>" + wed + "</th>");
            header += ("<th>Thursday<br>" + thu + "</th>");
            header += ("<th>Friday<br>" + fri + "</th>");
            header += ("<th>Saturday<br>" + sat + "</th>");
            header += ("</tr>");
            timetable += (tablehead(header));
            String sql = "SELECT slot.slotID,slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Saturday "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "inner join subject "
                    + "on timetable.subjectID = subject.subjectID "
                    + "where labID=? and weekID=(select weekID from week where week = ? and year=?) ";
            if (access == 0) {
                sql += " and subject.classID = ? ";
            }
            sql += "GROUP BY slot.startTime, slot.endTime ASC "
                    + "ORDER BY slot.startTime, slot.endTime ASC;";
            PreparedStatement ps4 = con.prepareStatement(sql);
            if (access == 0) {
                ps4.setInt(1, labid);
                ps4.setInt(2, week);
                ps4.setInt(3, year);
                ps4.setInt(4, classID);
            } else {
                ps4.setInt(1, labid);
                ps4.setInt(2, week);
                ps4.setInt(3, year);
            }
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
                lines[lab1.getInt(1) - 1] += ("<th style='vertical-align : middle;text-align:center;'>" + slots[lab1.getInt(1) - 1][0] + "</th>");
                lines[lab1.getInt(1) - 1] += ("<th style='vertical-align : middle;text-align:center;'>" + slots[lab1.getInt(1) - 1][1] + "</th>");
                for (int j = 1; j <= 6; j++) {
                    if (lab1.getString(j + 3) != null) {
                        String[] arrOfStr = lab1.getString(j + 3).split(",");
                        if (isfav(lab1.getString(j + 3))) {
                            lines[lab1.getInt(1) - 1] += ("<td><div id = 'fav" + temp + "'><div id = 'subclass" + arrOfStr[1] + "" + temp + "'><a href=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + arrOfStr[2] + "');\">" + arrOfStr[0] + " </a></div></div></td>");
                        } else {
                            lines[lab1.getInt(1) - 1] += ("<td><div id = 'subclass" + arrOfStr[1] + "" + temp + "'><a href=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + arrOfStr[2] + "');\">" + arrOfStr[0] + " </a></div></td>");

                        }
                    } else {
                        lines[lab1.getInt(1) - 1] += ("<td><div id = 'nolab" + temp + "'>No Lab<br><br></div></td>");
                    }
                    temp++;
                }
                lines[lab1.getInt(1) - 1] += ("</tr>");
            }
            for (int y = 0; y <= no_of_slots; y++) {
                if (lines[y].equals("")) {
                    lines[y] = ("<tr align='center'>");
                    lines[y] += ("<th style='vertical-align : middle;text-align:center;'>" + slots[y][0] + "</th>");
                    lines[y] += ("<th style='vertical-align : middle;text-align:center;'>" + slots[y][1] + "</th>");
                    for (int j = 1; j <= 6; j++) {
                        lines[y] += ("<td><div id = 'nolab" + temp + "'>No Lab<br><br></div></td>");
                        temp++;
                    }
                    lines[y] += ("</tr>");
                }
                timetable += lines[y];
            }
            timetable += (tableend(null, 0));
            con.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            errorLogger(e.getMessage());
            timetable = e.getMessage();
        }
        return timetable;
    }

    public String printTimetable() {
        String timetable = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            timetable += (tablestart(heading, "hover", "studDetails", 0));
            String header = ("<tr align = center>");
            header += ("<th style='vertical-align : middle;text-align:center;'>Start Time</th>");
            header += ("<th style='vertical-align : middle;text-align:center;'>End Time</th>");
            header += ("<th style='vertical-align : middle;text-align:center;'>Lab</th>");
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
                String sql = "SELECT slot.slotID,slot.startTime, slot.endTime, "
                        + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Monday, "
                        + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Tuesday, "
                        + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Wednesday, "
                        + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Thursday, "
                        + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Friday, "
                        + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) as Saturday "
                        + "FROM timetable "
                        + "INNER JOIN slot "
                        + "ON timetable.slotID = slot.slotID "
                        + "inner join subject "
                        + "on timetable.subjectID = subject.subjectID "
                        + "where labID=? and weekID=(select weekID from week where week = ? and year = ?)";
                if (access == 0) {
                    sql += " and subject.classID = ? ";
                }
                sql += "GROUP BY slot.startTime, slot.endTime ASC "
                        + "ORDER BY slot.startTime, slot.endTime ASC;";
                PreparedStatement ps4 = con.prepareStatement(sql);
                if (access == 0) {
                    ps4.setInt(1, l + 1);
                    ps4.setInt(2, week);
                    ps4.setInt(3, year);
                    ps4.setInt(4, classID);
                } else {
                    ps4.setInt(1, l + 1);
                    ps4.setInt(2, week);
                    ps4.setInt(3, year);
                }
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
                                labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'fav" + temp + "'><div id = 'subclass" + arrOfStr[1] + "" + temp + "'><a href=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + arrOfStr[2] + "');\">" + arrOfStr[0] + " </a></div></div></td>");
                            } else {
                                labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'subclass" + arrOfStr[1] + "" + temp + "'><a href=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + arrOfStr[2] + "');\">" + arrOfStr[0] + " </a></div></td>");
                            }
                        } else {
                            labs[l][lab1.getInt(1) - 1] += ("<td><div id = 'nolab" + temp + "'>No Lab<br><br></div></td>");
                        }
                        temp++;
                    }
                }
                for (int y = 0; y <= no_of_slots; y++) {
                    if (labs[l][y].equals("")) {
                        labs[l][y] = "";
                        for (int j = 1; j <= 6; j++) {
                            labs[l][y] += ("<td><div id = 'nolab" + temp + "'>No Lab<br><br></div></td>");
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
                    timetable += ("<td  style='vertical-align : middle;text-align:center;' align='center'>Lab " + (lab + 1) + "</td>");
                    timetable += (labs[lab][slot]);
                    timetable += ("</tr>");
                }
                timetable += ("</tr>");
                slot++;
            }
            timetable += (tableend(null, 0));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            errorLogger(e.getMessage());
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
