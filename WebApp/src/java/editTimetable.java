
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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editTimetable extends HttpServlet {

    int week;
    int no_of_subs = 0;
    String subs[];

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        week = (int) session.getAttribute("week");
                        int labid = Integer.parseInt(request.getParameter("lab"));
                        System.out.println(labid);
                        if (labid >= 4 || labid <= 0) {
                            labid = 1;
                        }
                        System.out.println("new " + labid);
                        new_week();
                        try {
                            int selesem = 1;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
                                    + "return(s);}");
                            out.println("function batchdisable(id) {"
                                    + "var index = document.getElementById(id).selectedIndex;"
                                    + "if(index == 0)"
                                    + "{id = id.substr(1);"
                                    + "document.getElementById('batch' + id).selectedIndex=0;"
                                    + "document.getElementById('batch' + id).disabled=true;"
                                    + "document.getElementById('batch' + id).classList.add('not-allowed');}"
                                    + "else{id = id.substr(1);"
                                    + "document.getElementById('batch' + id).disabled=false;"
                                    + "document.getElementById('batch' + id).classList.remove('not-allowed');}}"
                                    + "</script>");
                            out.println("<style> th { white-space: nowrap; } </style>");
                            LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                            LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                            out.print("LAB 1. <b>Week: " + week + "</b> from <b>" + weekstart + "</b> to <b>" + endweek + "</b>");
                            out.print("<form action='saveTimetable' method='post' align='center'>");
                            out.print(printTimetable(labid));
                            out.print("<input type='text' name='lab' value='" + labid + "' hidden>");
                            out.print("<input type='submit' value='Submit' align='center'>");
                            out.print("</form>");
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("message", e.getMessage());
                            request.setAttribute("redirect", "menu");
                            rd.forward(request, response);
                        }

                        request.getRequestDispatcher("end.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Hey 'Kid'!");
                        request.setAttribute("body", "You are not authorized to view this page");
                        request.setAttribute("url", "index.html");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);

                }
            } catch (IOException | ServletException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Please login to continue");
                request.setAttribute("url", "index.html");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
            }

        }
    }

    public void new_week() {
        int weekid = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps6 = con.prepareStatement("SELECT weekID FROM WEEK where week = ?");
            ps6.setInt(1, week);
            ResultSet rs = ps6.executeQuery();
            while (rs.next()) {
                weekid = rs.getInt(1);
            }
            if (weekid == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into week(`week`) values(?)");
                ps2.setInt(1, week);
                ps2.executeUpdate();
            }
            rs = ps6.executeQuery();
            while (rs.next()) {
                weekid = rs.getInt(1);
            }
            int labcount = 0;
            PreparedStatement ps8 = con.prepareStatement("SELECT count(labID) FROM lab");
            ps6.setInt(1, week);
            rs = ps8.executeQuery();
            while (rs.next()) {
                labcount = rs.getInt(1);
            }
            for (int i = 1; i <= labcount; i++) {
                PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                ps5.setInt(1, weekid);
                ps5.setInt(2, i);
                rs = ps5.executeQuery();
                int flag = 0;
                while (rs.next()) {
                    flag = 1;
                    break;
                }
                if (flag == 0) {
                    PreparedStatement ps10 = con.prepareStatement("SELECT weekID FROM `week` ORDER BY `week`.`weekID` DESC");
                    rs = ps10.executeQuery();
                    while (rs.next()) {
                        PreparedStatement ps9 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                        ps9.setInt(1, rs.getInt(1));
                        ps9.setInt(2, i);
                        ResultSet rs1 = ps9.executeQuery();
                        flag = 0;
                        while (rs1.next() && flag == 0) {
                            flag = 1;
                        }
                        PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ? and labID=?");
                        ps3.setInt(1, weekid);
                        ps3.setInt(2, rs.getInt(1));
                        ps3.setInt(3, i);
                        ps3.executeUpdate();
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
    }

    public String printTimetable(int labID) {

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
            PreparedStatement ps1 = con.prepareStatement("SELECT count(slotID) from slot");
            ResultSet rs1 = ps1.executeQuery();
            int no_of_slots = 0;
            while (rs1.next()) {
                no_of_slots = rs1.getInt(1);
            }
            int line = 1;
            rs.next();
            while (line <= no_of_slots) {
                if (rs.getInt(1) == line) {
                    table += ("<tr> ");
                    table += ("<th><input type='number' style='border:1px solid ;' name='ts" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                    table += (" : <input type='number'  style='border:1px solid ;' name='ts" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                    table += ("<th><input type='number'  style='border:1px solid ;' name='te" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(0, 2))) + "'>");
                    table += (" : <input type='number'  style='border:1px solid ;' name='te" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(3, 5))) + "'></th>");
                    for (int j = 1; j <= 6; j++) {
                        table += ("<td align='center'>");
                        table += ("<select name = 'c" + line + "" + j + "' id = 'c" + line + "" + j + "'  onchange = 'batchdisable(this.id)'>");
                        table += ("<option name='Sub' value='-'");
                        String[] arrOfsub = null;
                        int flag;
                        if (rs.getString(j + 3) == null) {
                            table += ("selected ");
                            flag = 0;
                        } else {
                            arrOfsub = rs.getString(j + 3).split(" - ");
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
                    rs.next();
                } else {
                    table += ("<tr> ");
                    table += ("<th><input type='number' style='border:1px solid ;' name='ts" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                    table += (" : <input type='number'  style='border:1px solid ;' name='ts" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                    table += ("<th><input type='number'  style='border:1px solid ;' name='te" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(0, 2))) + "'>");
                    table += (" : <input type='number'  style='border:1px solid ;' name='te" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(3).substring(3, 5))) + "'></th>");
                    for (int j = 1; j <= 6; j++) {
                        table += ("<td align='center'>");
                        table += ("<select name = 'c" + line + "" + j + "' id = 'c" + line + "" + j + "' onchange = 'batchdisable(this.id)'>");
                        table += ("<option name='Sub' value='-' selected>No Lab</option>");
                        for (int k = 0; k <= no_of_subs; k++) {
                            table += ("<option name='Sub' value='" + subs[k] + "'>" + subs[k] + "</option>");
                        }
                        String batch[] = {"Batch A", "Batch B", "Batch C"};
                        table += ("</select>");
                        table += ("<select name = 'batch" + line + "" + j + "' id = 'batch" + line + "" + j + "' disabled class='not-allowed'>");
                        table += ("<option name='-' value='-' selected>No Batch</option>");
                        for (int x = 0; x <= batch.length - 1; x++) {
                            table += ("<option name='A' value='" + batch[x] + "'>" + batch[x] + "</option>");
                        }
                        table += ("</select>");
                        table += ("</td>");
                    }
                    table += ("</tr>");
                }
                line++;
            }
            table += ("</tbody></table><br><br>");
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            table = e.getMessage();
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
