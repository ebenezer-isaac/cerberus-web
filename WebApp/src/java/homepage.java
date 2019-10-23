
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class homepage extends HttpServlet {

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
            switch (access) {
                case 1:
                    request.getRequestDispatcher("side-faculty.jsp").include(request, response);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps1 = con.prepareStatement("select subject.Abbreviation from facultysubject "
                                + "inner join subject "
                                + "on subject.subjectID=facultysubject.subjectID "
                                + "where facultyID = ?");
                        ps1.setString(1, session.getAttribute("user").toString());
                        ResultSet rs = ps1.executeQuery();
                        int no_of_subs = 0;
                        while (rs.next()) {
                            no_of_subs++;
                        }
                        subs = new String[no_of_subs];
                        rs = ps1.executeQuery();
                        no_of_subs = 0;
                        while (rs.next()) {
                            subs[no_of_subs] = rs.getString(1);
                            no_of_subs++;
                        }
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        messages m = new messages();
                        m.dberror(request, response, e.getMessage(), "homepage");
                    }
                    printTimetable(Integer.parseInt(session.getAttribute("week").toString()));
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                case 0:
                    request.getRequestDispatcher("side-student.jsp").include(request, response);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps1 = con.prepareStatement("select subject.Abbreviation from studentsubject "
                                + "inner join subject "
                                + "on subject.subjectID=studentsubject.subjectID "
                                + "where PRN = ?");
                        ps1.setString(1, session.getAttribute("user").toString());
                        ResultSet rs = ps1.executeQuery();
                        int no_of_subs = 0;
                        while (rs.next()) {
                            no_of_subs++;
                        }
                        subs = new String[no_of_subs];
                        rs = ps1.executeQuery();
                        no_of_subs = 0;
                        while (rs.next()) {
                            subs[no_of_subs] = rs.getString(1);
                            no_of_subs++;
                        }
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        messages m = new messages();
                        m.dberror(request, response, e.getMessage(), "homepage");
                    }
                    printTimetable(Integer.parseInt(session.getAttribute("week").toString()));
                    System.out.println("hello");
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                default:
                    messages m = new messages();
                    m.nouser(request, response);
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

    public boolean isfav(String subject) {
        for (int i = 0; i <= subs.length - 1; i++) {
            int index = subject.indexOf(subs[i]);
            if (index != -1) {
                return true;
            }
        }
        return false;
    }

    public void printTimetable(int week) throws IOException, ServletException {
        try (PrintWriter out = response.getWriter()) {
            LocalDate mon = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
            LocalDate tue = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(2)));
            LocalDate wed = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(3)));
            LocalDate thu = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(4)));
            LocalDate fri = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(5)));
            LocalDate sat = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week + 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(6)));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                out.print("<table class=\"table table-striped table-bordered\"><thead>");
                out.print("<tr align = center>");
                out.print("<th>Start Time</th>");
                out.print("<th>End Time</th>");
                out.print("<th>Lab</th>");
                out.print("<th>Monday<br>" + mon + "</th>");
                out.print("<th>Tuesday<br>" + tue + "</th>");
                out.print("<th>Wednesday<br>" + wed + "</th>");
                out.print("<th>Thursday<br>" + thu + "</th>");
                out.print("<th>Friday<br>" + fri + "</th>");
                out.print("<th>Saturday<br>" + sat + "</th>");
                out.print("</tr></thead><tbody>");
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
                    int line = 0;
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
                    ps4.setInt(1, l + 1);
                    ps4.setInt(2, week);
                    ResultSet lab1 = ps4.executeQuery();
                    lab1.next();
                    while (line <= no_of_slots) {
                        labs[l][line] = "";
                        if (lab1.getInt(1) == (line + 1)) {
                            for (int j = 1; j <= 6; j++) {
                                if (lab1.getString(j + 3) != null) {
                                    if (isfav(lab1.getString(j + 3))) {
                                        labs[l][line] += ("<td align='center'><b>" + lab1.getString(j + 3) + "</b></td>");
                                    } else {
                                        if (access == 1) {
                                            labs[l][line] += ("<td align='center'>" + lab1.getString(j + 3) + "</td>");
                                        } else {
                                            labs[l][line] += ("<td align='center'> No Lab </td>");
                                        }
                                    }
                                } else {
                                    labs[l][line] += ("<td align='center'> No Lab </td>");
                                }

                            }
                            lab1.next();
                        } else {
                            for (int j = 1; j <= 6; j++) {
                                labs[l][line] += ("<td align='center'> No Lab <br>&nbsp</td>");
                            }
                        }
                        line++;
                    }
                }
                con.close();
                int slot = 0;
                while (slot <= no_of_slots) {
                    out.print("<tr align='center'>");
                    out.print("<th style='vertical-align : middle;text-align:center;' rowspan='4'>" + slots[slot][0] + "</th>");
                    out.print("<th style='vertical-align : middle;text-align:center;' rowspan='4'>" + slots[slot][1] + "</th></tr>");
                    for (int lab = 0; lab <= no_of_labs - 1; lab++) {
                        out.print("<td  align='center'>Lab " + (lab + 1) + "</td>");
                        out.print(labs[lab][slot]);
                        out.print("</tr>");
                    }
                    out.print("</tr>");
                    slot++;
                }
                out.print("</tbody></table><br><br>");
                request.getRequestDispatcher("end.html").include(request, response);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
