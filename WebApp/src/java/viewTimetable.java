
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class viewTimetable extends HttpServlet {

    int week;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("w");
            week = Integer.parseInt(ft.format(date));
            HttpSession session = request.getSession();
            session.setAttribute("week", week);
            new_week();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        request.getRequestDispatcher("nav.html").include(request, response);
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        LocalDate weekstart = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
                        LocalDate endweek = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
                        out.print("LAB 1. <b>Week: " + week + "</b> from <b>" + weekstart + "</b> to <b>" + endweek + "</b>");

                        out.println(fac_printTimetable(1));
                        out.print("LAB 2");
                        out.println(fac_printTimetable(2));
                        out.print("LAB 3");
                        out.println(fac_printTimetable(3));
                        out.println("</div></div></div></div></div><script src=\"js/Sidebar-Menu.js\"></script><script src=\"js/main.js\"></script>");
                        break;
                    case 0:
                        request.getRequestDispatcher("nav.html").include(request, response);
                        request.getRequestDispatcher("side-student.html").include(request, response);
                        break;
                    default:

                }
            } catch (Exception e) {
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
            PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ?");
            ps5.setInt(1, weekid);
            rs = ps5.executeQuery();
            int flag = 0;
            while (rs.next()) {
                flag = 1;
            }
            if (flag == 0) {
                PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = (select weekID from week where week = ?)");
                ps3.setInt(1, weekid);
                ps3.setInt(2, week - 1);
                ps3.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String fac_printTimetable(int labid) {
        String timetable = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");

            timetable += ("<table class=\"table table-striped table-bordered\"><thead>");
            timetable += ("<tr align = center>");
            timetable += ("<th>Start_Time</th>");
            timetable += ("<th>End_Time</th>");
            timetable += ("<th>Monday</th>");
            timetable += ("<th>Tuesday</th>");
            timetable += ("<th>Wednesday</th>");
            timetable += ("<th>Thursday</th>");
            timetable += ("<th>Friday</th>");
            timetable += ("<th>Saturday</th>");
            timetable += ("</tr></thead><tbody>");
            PreparedStatement ps4 = con.prepareStatement("SELECT slot.startTime, slot.endTime, "
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
            while (lab1.next()) {
                timetable += ("<tr align='center'>");
                timetable += ("<th>" + lab1.getString(1).substring(0, 5) + "</th>");
                timetable += ("<th>" + lab1.getString(2).substring(0, 5) + "</th>");
                for (int j = 1; j <= 6; j++) {
                    if (lab1.getString(j + 2) != null) {
                        timetable += ("<td>" + lab1.getString(j + 2) + "</td>");
                    } else {
                        timetable += ("<td> <b>No Lab </b></td>");
                    }

                }
                timetable += ("</tr>");
            }
            timetable += ("</td></tr></tbody></table><br><br>");
            con.close();
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

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
