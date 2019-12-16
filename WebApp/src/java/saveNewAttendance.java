
import static cerberus.AttFunctions.get_schedule_det;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class saveNewAttendance extends HttpServlet {

    private static final long serialVersionUID = 5124640296287249007L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                HttpSession session = request.getSession(false);
                int scheduleid = Integer.parseInt(request.getParameter("scheduleid"));
                int facultyid = Integer.parseInt(session.getAttribute("user").toString());
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("insert into facultytimetable (?,?)");
                    ps.setInt(1, scheduleid);
                    ps.setInt(2, facultyid);
                    ps.executeUpdate();
                    ps = con.prepareStatement("select rollcall.rollNo , student.name, studentsubject.prn from timetable inner join studentsubject on timetable.batchID = studentsubject.batchID and timetable.subjectID = studentsubject.subjectID inner join student on student.PRN = studentsubject.PRN inner join rollcall on student.PRN = rollcall.PRN where timetable.scheduleID=?");
                    ps.setInt(1, scheduleid);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String schedule[] = get_schedule_det(rs.getInt(1));
                        out.print(tablestart("Lab Sessions", "hover", "studDetails", 0));
                        String header = "<tr>";
                        header += "<th>Roll No</th>";
                        header += "<th>Name</th>";
                        header += "<th>Status</th>";
                        header += "</tr>";
                        out.print(tablehead(header));
                        rs.previous();
                        int line = 1;
                        while (rs.next()) {
                            out.print("<td>" + rs.getString(1) + "</td><td>" + rs.getString(2) + "</td>"
                                    + "<td><input type='text' name = 'prn" + line + "' value='" + rs.getString(3) + "'>");
                            ps = con.prepareStatement("select attendance.attendanceID from attendance where attendance.PRN = ? and attendance.scheduleID=?");
                            ps.setString(1, rs.getString(3));
                            ps.setInt(2, scheduleid);
                            ResultSet rs1 = ps.executeQuery();
                            if (rs1.next()) {
                                out.print("<input type='checkbox' value='1' name='att" + line + "' checked >");
                            } else {
                                out.print("<input type='checkbox' value='0' name='att" + line + "' >");
                            }
                            out.print("</td></tr>");
                            line++;
                        }
                        out.print(tableend(null, 1));
                    } else {
                        out.print("No students belonging to batch have opted for this subject");
                    }

                } catch (ClassNotFoundException | SQLException x) {

                }
            } catch (NumberFormatException e) {
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
