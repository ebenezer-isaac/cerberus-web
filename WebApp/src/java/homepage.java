import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class homepage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("nav.html").include(request, response);
                        HttpSession session = request.getSession();
            int access = (int) session.getAttribute("access");
            if (access == 1) {
                request.getRequestDispatcher("side-faculty.html").include(request, response);
                out.print("LAB 1");
            out.println(fac_printTimetable(1));
            out.print("LAB 2");
            out.println(fac_printTimetable(2));
            out.print("LAB 3");
            out.println(fac_printTimetable(3));
            out.println("</div></div></div></div></div><script src=\"js/Sidebar-Menu.js\"></script><script src=\"js/main.js\"></script>");
            } else if (access == 0){
                request.getRequestDispatcher("side-student.html").include(request, response);
            }
            else
            {
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

    public String stud_printTimetable(int labid) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("w");
        String timetable = "";
        int week = Integer.parseInt(ft.format(date));
        int weekid;
        int maxweek = 0;
        int maxweekid = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps1 = con.prepareStatement("SELECT * FROM WEEK where weekID = (select MAX(weekID) from week)");
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                maxweekid = rs.getInt(1);
                maxweek = rs.getInt(2);
            }
            if (week != maxweek) {
                PreparedStatement ps2 = con.prepareStatement("insert into week values(?,?)");
                weekid = maxweekid + 1;
                ps2.setInt(1, weekid);
                ps2.setInt(2, week);
                ps2.executeUpdate();
                PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ?");
                ps3.setInt(1, weekid);
                ps3.setInt(2, maxweekid);
                ps3.executeUpdate();
            }

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
                    + "MAX(CASE WHEN dayID = 'mon' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 'tue' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 'wed' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 'thu' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 'fri' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 'sat' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Saturday "
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
                    timetable += ("<td>" + lab1.getString(j + 2) + "</td>");
                }
                timetable += ("</tr>");
            }
            timetable += ("</td></tr></tbody></table><br><br>");
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            timetable = e.getMessage();
        }
        return timetable;
    }
    
    public String fac_printTimetable(int labid) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("w");
        String timetable = "";
        int week = Integer.parseInt(ft.format(date));
        int weekid;
        int maxweek = 0;
        int maxweekid = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps1 = con.prepareStatement("SELECT * FROM WEEK where weekID = (select MAX(weekID) from week)");
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                maxweekid = rs.getInt(1);
                maxweek = rs.getInt(2);
            }
            if (week != maxweek) {
                PreparedStatement ps2 = con.prepareStatement("insert into week values(?,?)");
                weekid = maxweekid + 1;
                ps2.setInt(1, weekid);
                ps2.setInt(2, week);
                ps2.executeUpdate();
                PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ?");
                ps3.setInt(1, weekid);
                ps3.setInt(2, maxweekid);
                ps3.executeUpdate();
            }

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
                    + "MAX(CASE WHEN dayID = 'mon' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Monday, "
                    + "MAX(CASE WHEN dayID = 'tue' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Tuesday, "
                    + "MAX(CASE WHEN dayID = 'wed' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Wednesday, "
                    + "MAX(CASE WHEN dayID = 'thu' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Thursday, "
                    + "MAX(CASE WHEN dayID = 'fri' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Friday, "
                    + "MAX(CASE WHEN dayID = 'sat' THEN concat((select subject.subject from subject where timetable.subjectID=subject.subjectID),' - ',timetable.batchID) END) as Saturday "
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
                    timetable += ("<td>" + lab1.getString(j + 2) + "</td>");
                }
                timetable += ("</tr>");
            }
            timetable += ("</td></tr></tbody></table><br><br>");
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
