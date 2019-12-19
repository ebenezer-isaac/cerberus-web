
import static cerberus.AttFunctions.get_next_schedule;
import static cerberus.AttFunctions.no_of_labs;
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
import java.util.Date;
import javax.servlet.RequestDispatcher;

public class homepage extends HttpServlet {

    private static final long serialVersionUID = -6020013234525993016L;

    int week;
    String[] subs;
    int access;
    HttpServletResponse response;
    HttpServletRequest request;

    protected void processRequest(HttpServletRequest reques, HttpServletResponse respons)
            throws ServletException, IOException {
        response = respons;
        request = reques;
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            access = (int) session.getAttribute("access");
            if (access == 0) {

            }
            switch (access) {
                case 1:
                    String faculty_name = (String) session.getAttribute("name");
                    out.print("<b>" + faculty_name + "</b><br><br><div class='row'>");
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
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/rapidAttendance?scheduleid=" + testing[1].split(",")[1] + "');\">"
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
                    System.out.println("fac" + faculty_id);
                    try {
                        String subcode = request.getParameter("subcode");
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
                            PreparedStatement ps1 = con.prepareStatement("SELECT (STR_TO_DATE(concat((select week.year from week where timetable.weekID = week.weekID),' ',(select week.week from week where timetable.weekID = week.weekID)-1,' ',timetable.dayID),'%X %V %w')) as date, "
                                    + "slot.startTime, slot.endTime,"
                                    + "(select lab.name from lab where lab.labID=timetable.labID) as lab,"
                                    + "(select subject.abbreviation from subject where subject.subjectID=timetable.subjectID) as Subject,"
                                    + "(select batch.name from batch where batch.batchID=timetable.batchID) as batch,timetable.subjectID, timetable.batchID , timetable.scheduleID "
                                    + "from facultytimetable "
                                    + "INNER JOIN timetable "
                                    + "on timetable.scheduleID=facultytimetable.scheduleID "
                                    + "INNER JOIN slot "
                                    + "on slot.slotID=timetable.slotID "
                                    + "where facultytimetable.facultyID =? order by date, slot.startTime;");
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
                                out.print("<tr align='center' onclick = \"javascript:setContent('/Cerberus/newFacultyTimetable?scheduleid=" + rs1.getString(9) + "');\">");
                                for (int i = 1; i <= 6; i++) {
                                    out.print("<td>" + rs1.getString(i) + "</td>");
                                }
                                out.print("</tr>");
                            }
                            out.print(tableend(null, 1));
                        } else {
                            out.print("No Data to show");
                        }
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        error(e.getMessage());
                    }
                    out.print("");
                    break;
                case 0:
                    int count = 0;
                    String prn = (String) session.getAttribute("user");
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("select count(batchID) from studentsubject where prn = ? and batchID !=0");
                        ps.setString(1, prn);
                        ResultSet rs = ps.executeQuery();

                        while (rs.next()) {
                            count = rs.getInt(1);
                        }

                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
                    }
                    if (count < 1) {
                       out.print("<script>window.location.replace('/Cerberus/details.jsp')</script>");
                    } else {
                        out.print("Homepage");
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
