
import cerberus.AttFunctions;
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.get_next_schedule;
import static cerberus.AttFunctions.get_next_stud_schedule;
import static cerberus.AttFunctions.no_of_labs;
import static cerberus.AttFunctions.prefStudSubs;
import cerberus.messages;
import static cerberus.printer.error;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static cerberus.printer.nouser;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class homepage extends HttpServlet {

    private static final long serialVersionUID = -6020013234525993016L;

    int week;
    int access;
    HttpServletResponse response;
    HttpServletRequest request;

    protected void processRequest(HttpServletRequest reques, HttpServletResponse respons)
            throws ServletException, IOException {
        response = respons;
        request = reques;
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            access = getAccess(request);
            switch (access) {
                case 1:
                    String faculty_name = (String) session.getAttribute("name");
                    out.print("<b> Welcome " + faculty_name + "</b><br><br><div class='row'>");
                    int labs = no_of_labs();
                    for (int i = 1; i <= labs; i++) {
                        String testing[] = get_next_schedule(request, i);
                        switch (testing[0]) {
                            case "0":
                                out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-danger o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class=\"fas fa-times\"></i>"
                                        + "</div>"
                                        + "<div class='mr-2' align='center'><br>No Labs Today<br><br></div>"
                                        + "</div>"
                                        + "<br><br>"
                                        + "<a class='card-footer text-white clearfix small z-1' "
                                        + "href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "1":
                                out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-success o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-check'></i>"
                                        + "</div>"
                                        + "<div class='mr-2' align='center'><br>All Labs are Over<br><br></div>"
                                        + "</div>"
                                        + "<br><br>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "2":
                                out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-primary o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-desktop'></i>"
                                        + "</div>"
                                        + "<div class='mr-2' align='center'>" + testing[1].split(",")[0] + "</div>"
                                        + "</div>"
                                        + "<span class='float-left'><i class='fas fa-user'></i> Students in Lab : " + testing[1].split(",")[2] + "</span>"
                                        + "<br>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + testing[1].split(",")[1] + "');\">"
                                        + "<span class='float-left'>Edit Attendance</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "3":
                                out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-warning o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-forward'></i>"
                                        + "</div>"
                                        + "<div class='mr-2' align='center'>" + testing[1] + "</div>"
                                        + "</div>"
                                        + "<br><br>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                        }
                    }
                    out.print("</div>");
                    int labcount = 0;
                    String faculty_id = (String) session.getAttribute("user");
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("SELECT count(facultytimetable.scheduleID) "
                                + "from facultytimetable "
                                + "INNER JOIN timetable "
                                + "on timetable.scheduleID=facultytimetable.scheduleID "
                                + "where facultytimetable.facultyID = ?");
                        ps.setString(1, faculty_id);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            labcount = rs.getInt(1);
                        }
                        out.print("<br>");
                        if (labcount >= 1) {
                            PreparedStatement ps1 = con.prepareStatement("SELECT (select week.year from week where timetable.weekID = week.weekID) as year ,(select week.week from week where timetable.weekID = week.weekID) as week ,timetable.dayID as dayid, "
                                    + "slot.startTime, slot.endTime,"
                                    + "(select lab.name from lab where lab.labID=timetable.labID) as lab,"
                                    + "(select subject.abbreviation from subject where subject.subjectID=timetable.subjectID) as Subject,"
                                    + "(select batch.name from batch where batch.batchID=timetable.batchID) as batch,timetable.subjectID, timetable.batchID , timetable.scheduleID "
                                    + "from facultytimetable "
                                    + "INNER JOIN timetable "
                                    + "on timetable.scheduleID=facultytimetable.scheduleID "
                                    + "INNER JOIN slot "
                                    + "on slot.slotID=timetable.slotID "
                                    + "where facultytimetable.facultyID =? order by year and week and dayid and slot.startTime DESC;");
                            ps1.setString(1, faculty_id);
                            ResultSet rs1 = ps1.executeQuery();
                            out.print(tablestart("<b>Number of Lab Sessions conducted by you </b>: " + labcount + "",
                                    "hover", "studDetails", 1) + "");
                            String header = "<tr align = center>";
                            header += "<th>Date</th>";
                            header += "<th>Start Time</th>";
                            header += "<th>End Time</th>";
                            header += "<th>Lab</th>";
                            header += "<th>Subject</th>";
                            header += "<th>Batch</th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            while (rs1.next()) {
                                out.print("<tr align='center' onclick = \"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + rs1.getString(11) + "');\">");
                                LocalDate date = LocalDate.now()
                                        .with(WeekFields.ISO.weekBasedYear(), rs1.getInt(1)) // year
                                        .with(WeekFields.ISO.weekOfWeekBasedYear(), rs1.getInt(2)) // week of year
                                        .with(WeekFields.ISO.dayOfWeek(), rs1.getInt(3));
                                out.print("<td>" + date + "</td>");
                                for (int i = 1; i <= 5; i++) {
                                    out.print("<td>" + rs1.getString(i + 3) + "</td>");
                                }
                                out.print("</tr>");
                            }
                            out.print(tableend(null, 1));
                        } else {
                            out.print("You haven't conducted any labs yet");
                        }
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        errorLogger(e.getMessage());
                        error(e.getMessage());
                    }
                    break;
                case 0:
                    int count = 0;
                    String prn = (String) session.getAttribute("user");
                    String student_name = (String) session.getAttribute("name");
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("select count(batchID) from studentsubject where prn = ? and batchID !=0");
                        ps.setString(1, prn);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            count = rs.getInt(1);
                        }
                        con.close();
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
                    }
                    if (count < 1) {
                        out.print("<script>window.location.replace('/Cerberus/details.jsp')</script>");
                    } else {
                        out.print("<b> Welcome " + student_name + "</b><br><br><div class='card'><div class='card-header'></div>"
                                + "<div class='card-body'>"
                                + "<div class='table-responsive'>"
                                + "<style>"
                                + "tr{vertical-align : middle;text-align:center;}"
                                + "th{vertical-align : middle;text-align:center;}"
                                + "td{vertical-align : middle;text-align:center;}"
                                + "</style>"
                                + "<table class='table table-bordered'  width='100%' cellspacing='0'><thead>");
                        String subs[][] = prefStudSubs(prn);
                        float total = 0;
                        for (int i = 0; i < subs.length; i++) {
                            out.print("<th>" + subs[i][2] + "</th>");
                        }
                        out.print("<th>Average</th></thead><tr>");
                        try {
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            for (int i = 0; i < subs.length; i++) {
                                PreparedStatement ps = con.prepareStatement("select count(facultytimetable.scheduleID) from facultytimetable inner join timetable on facultytimetable.scheduleID = timetable.scheduleID where subjectID = ? and batchID = ?");
                                ps.setString(1, subs[i][0]);
                                ps.setString(2, subs[i][1]);
                                ResultSet rs_subject = ps.executeQuery();
                                int temp = 0;
                                while (rs_subject.next()) {
                                    if (rs_subject.getInt(1) > 0) {
                                        temp = 1;
                                    }
                                }
                                if (subs[i][1].equals("0")) {
                                    out.print("<td>N/A</td>");
                                } else if (temp == 0) {
                                    out.print("<td>No Labs</td>");
                                } else {
                                    count++;
                                    out.print("<td >");
                                    float currPerc = AttFunctions.calPercentage(prn, subs[i][0], subs[i][0]);
                                    total = total + currPerc;
                                    out.print(String.format("%.02f", currPerc) + "%");
                                    out.print("</td>");
                                }
                            }
                        } catch (SQLException e) {
                        }
                        out.print("<td>" + String.format("%.02f", total) + "%</td><tr></table>"
                                + "</div>"
                                + "</div>"
                                + "</div><div class='row'>");
                        labs = no_of_labs();
                        for (int i = 1; i <= labs; i++) {
                            String testing[] = get_next_stud_schedule(request, i, prn);
                            switch (testing[0]) {
                                case "0":
                                    out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                            + "<div class='card text-white bg-primary o-hidden h-100'>"
                                            + "<a class='card-header text-white clearfix'>"
                                            + "<span class='float-middle'>Lab " + i + "</span>"
                                            + "</a>"
                                            + "<div class='card-body'>"
                                            + "<div class='card-body-icon'>"
                                            + "<i class=\"fas fa-times\"></i>"
                                            + "</div>"
                                            + "<div class='mr-2' align='center'><br>No Labs Today<br></div>"
                                            + "</div>"
                                            + "<br>"
                                            + "</div>"
                                            + "</div>");
                                    break;
                                case "1":
                                    out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                            + "<div class='card text-white bg-primary o-hidden h-100'>"
                                            + "<a class='card-header text-white clearfix'>"
                                            + "<span class='float-middle'>Lab " + i + "</span>"
                                            + "</a>"
                                            + "<div class='card-body'>"
                                            + "<div class='card-body-icon'>"
                                            + "<i class='fas fa-check'></i>"
                                            + "</div>"
                                            + "<div class='mr-2' align='center'><br>All Labs are Over<br><br></div>"
                                            + "</div>"
                                            + "<br>"
                                            + "</div>"
                                            + "</div>");
                                    break;
                                case "2":
                                    if (Integer.parseInt(testing[1].split(",")[1]) == 1) {
                                        out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                                + "<div class='card text-white bg-success o-hidden h-100'>");
                                    } else {
                                        out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                                + "<div class='card text-white bg-danger o-hidden h-100'>");
                                    }
                                    out.print("<a class='card-header text-white clearfix'>"
                                            + "<span class='float-middle'>Lab " + i + "</span>"
                                            + "</a>"
                                            + "<div class='card-body'>"
                                            + "<div class='card-body-icon'>"
                                            + "<i class='fas fa-desktop'></i>"
                                            + "</div>"
                                            + "<div class='mr-2' align='center'>" + testing[1].split(",")[0] + "</div>"
                                            + "</div>"
                                            + "<br>"
                                            + "</div>"
                                            + "</div>");
                                    break;
                                case "3":
                                    out.print("<div class='col-xl-4 col-sm-6 mb-3' align='center'>"
                                            + "<div class='card text-white bg-warning o-hidden h-100'>"
                                            + "<a class='card-header text-white clearfix'>"
                                            + "<span class='float-middle'>Lab " + i + "</span>"
                                            + "</a>"
                                            + "<div class='card-body'>"
                                            + "<div class='card-body-icon'>"
                                            + "<i class='fas fa-forward'></i>"
                                            + "</div>"
                                            + "<div class='mr-2' align='center'>" + testing[1] + "</div>"
                                            + "</div>"
                                            + "<br>"
                                            + "</div>"
                                            + "</div>");
                                    break;
                            }
                        }
                        out.print("</div>");
                        labcount = 0;
                        try {
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            PreparedStatement ps = con.prepareStatement("SELECT count(attendance.attendanceID) "
                                    + "from attendance "
                                    + "where prn = ?");
                            ps.setString(1, prn);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                labcount = rs.getInt(1);
                            }
                            out.print("<br>");
                            if (labcount >= 1) {
                                PreparedStatement ps1 = con.prepareStatement("SELECT (select week.year from week where timetable.weekID = week.weekID) as year ,(select week.week from week where timetable.weekID = week.weekID) as week ,timetable.dayID as dayid, "
                                        + "slot.startTime, slot.endTime,"
                                        + "(select lab.name from lab where lab.labID=timetable.labID) as lab,"
                                        + "(select subject.abbreviation from subject where subject.subjectID=timetable.subjectID) as Subject,"
                                        + "(select batch.name from batch where batch.batchID=timetable.batchID) as batch,timetable.subjectID "
                                        + "from timetable "
                                        + "INNER JOIN attendance "
                                        + "on timetable.scheduleID=attendance.scheduleID "
                                        + "INNER JOIN slot "
                                        + "on slot.slotID=timetable.slotID "
                                        + "where attendance.prn =? order by year and week and dayid and slot.startTime DESC");
                                ps1.setString(1, prn);
                                ResultSet rs1 = ps1.executeQuery();
                                out.print(tablestart("<b>Number of Lab Sessions attended by you </b>: " + labcount + "",
                                        "hover", "studDetails", 1) + "");
                                String header = "<tr align = center>";
                                header += "<th>Date</th>";
                                header += "<th>Start Time</th>";
                                header += "<th>End Time</th>";
                                header += "<th>Lab</th>";
                                header += "<th>Subject</th>";
                                header += "<th>Batch</th>";
                                header += "</tr>";
                                out.print(tablehead(header));
                                while (rs1.next()) {
                                    out.print("<tr align='center' onclick = \"javascript:setContent('/Cerberus/studSubAttendance?sub=" + rs1.getString(7) + "');\">");
                                    LocalDate date = LocalDate.now()
                                            .with(WeekFields.ISO.weekBasedYear(), rs1.getInt(1)) // year
                                            .with(WeekFields.ISO.weekOfWeekBasedYear(), rs1.getInt(2)) // week of year
                                            .with(WeekFields.ISO.dayOfWeek(), rs1.getInt(3));
                                    out.print("<td>" + date + "</td>");
                                    for (int i = 1; i <= 5; i++) {
                                        out.print("<td>" + rs1.getString(i + 3) + "</td>");
                                    }
                                    out.print("</tr>");
                                }
                                out.print(tableend(null, 0));
                            } else {
                                out.print("You haven't attended any labs yet");
                            }
                            con.close();
                        } catch (SQLException e) {
                            errorLogger(e.getMessage());
                            error(e.getMessage());
                        }
                    }
                    break;
                default:
                    nouser();
            }
        }
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
