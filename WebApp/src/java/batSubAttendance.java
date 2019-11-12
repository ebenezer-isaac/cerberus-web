
import static cerberus.printer.error;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class batSubAttendance extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int batchID = Integer.parseInt(request.getParameter("batchID"));
            String subjectID = request.getParameter("subjectID");
            String sql = "SELECT rollcall.rollNo as Roll,student.name as Name,";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("select timetable.scheduleID, timetable.weekID, timetable.dayID, (select week.year from week where weekID=timetable.weekID) from timetable \n"
                        + "inner join facultytimetable \n"
                        + "on timetable.scheduleID =  facultytimetable.scheduleID\n"
                        + "inner join week\n"
                        + "on timetable.weekID = week.weekID\n"
                        + "where timetable.subjectID = ? \n"
                        + "and timetable.batchID = ?");
                ps.setString(1, subjectID);
                ps.setInt(2, batchID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String yearweekday = "" + rs.getInt(4) + String.format("%02d", Integer.parseInt(rs.getString(2))) + rs.getInt(3);
                    System.out.println(yearweekday);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyywwu");
                    Date date = null;
                    try {
                        date = sdf.parse(yearweekday);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sql += "MAX(CASE WHEN attendance.PRN = student.PRN THEN '1' END) as  '" + date.getDate() + "/" + date.getMonth() + "/" + (date.getYear() + 1901) + "' ";
                    if (rs.next()) {
                        sql += ", ";
                        rs.previous();
                    }
                }
                sql += "FROM timetable \n"
                        + "inner join facultytimetable \n"
                        + "on timetable.scheduleID = facultytimetable.scheduleID \n"
                        + "Inner join attendance \n"
                        + "on attendance.scheduleID = facultytimetable.scheduleID \n"
                        + "INNER JOIN student \n"
                        + "ON student.PRN = attendance.PRN \n"
                        + "INNER JOIN rollcall  \n"
                        + "on  rollcall.PRN = student.PRN \n"
                        + "where timetable.subjectID = ? and timetable.batchID = ?\n"
                        + "and attendance.PRN in (select studentsubject.PRN from studentsubject where studentsubject.batchID = ? and studentsubject.subjectID = ? ) "
                        + "GROUP BY rollcall.rollNo \n"
                        + "ORDER by LENGTH(rollcall.rollNo),rollcall.rollNo";
                System.out.println(sql);
                PreparedStatement ps4 = con.prepareStatement(sql);
                ps4.setString(1, subjectID);
                ps4.setInt(2, batchID);
                ps4.setInt(3, batchID);
                ps4.setString(4, subjectID);
                rs = ps4.executeQuery();
                ResultSetMetaData rsm = rs.getMetaData();
                out.print(tablestart("Subjects", "hover", "studDetails", 1));
                String header = "<tr>";
                int cols = rsm.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    header += "<th>" + rsm.getColumnLabel(i) + "</th>";
                }
                header += "</tr>";
                out.print(tablehead(header));
                while (rs.next()) {
                    out.print("<tr>");
                    for (int i = 1; i <= cols; i++) {
                        header += "<td>" + rs.getString(i) + "</td>";
                    }
                    out.print("</tr>");
                }
                out.print(tableend(null, 0));
            } catch (SQLException | ClassNotFoundException e) {
                error(e.getMessage());
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
