
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getTimeID;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class saveRapidAttendance extends HttpServlet {

    private static final long serialVersionUID = -352552068166336225L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                try {
                    int scheduleid = Integer.parseInt(request.getParameter("scheduleid"));
                    int line = Integer.parseInt(request.getParameter("line"));
                    int timeID = getTimeID(getCurrTime());
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement insert = con.prepareStatement("insert into attendance values(null,?,?,?)");
                        PreparedStatement delete = con.prepareStatement("delete from attendance where scheduleid=? and prn = ?");
                        for (int i = 1; i < line; i++) {
                            String prn = request.getParameter("prn" + i);
                            String att = request.getParameter("att" + i);
                            if (att != null) {
                                try {
                                    PreparedStatement select = con.prepareStatement("select attendance.attendanceID from attendance where prn = ? and scheduleid = ?");
                                    select.setString(1, prn);
                                    select.setInt(2, scheduleid);
                                    ResultSet check = select.executeQuery();
                                    int dup = 0;
                                    while (check.next()) {
                                        dup = 1;
                                    }
                                    if (dup != 1) {
                                        insert.setString(1, prn);
                                        insert.setInt(2, scheduleid);
                                        insert.setInt(3, timeID);
                                        insert.executeUpdate();
                                        dbLog(currUserName(request) + " marked attendance for " + prn + " as present for scheduleID " + scheduleid + " using Rapid Attendance");
                                    }
                                } catch (SQLException x) {
                                    errorLogger(x.getMessage());
                                }
                            } else {
                                try {
                                    delete.setInt(1, scheduleid);
                                    delete.setString(2, prn);
                                    delete.executeUpdate();
                                    dbLog(currUserName(request) + " marked attendance for " + prn + " as absent for scheduleID " + scheduleid + " using Rapid Attendance");
                                } catch (SQLException x) {
                                    errorLogger(x.getMessage());
                                }
                            }
                        }
                        con.close();
                        messages a = new messages();
                        a.success(request, response, "Attendance has been saved", "viewTimetable");
                    } catch (ClassNotFoundException | SQLException x) {
                        messages b = new messages();
                        b.error(request, response, x.getMessage(), "viewTimetable");
                    }

                } catch (NumberFormatException e) {
                    messages b = new messages();
                    b.error(request, response, e.getMessage(), "viewTimetable");
                }
                break;
            case 0:
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp?type=login2");
                rd.forward(request, response);
                break;
            default:
                messages c = new messages();
                c.nouser(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response
    )
            throws ServletException,
            IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response
    )
            throws ServletException,
            IOException {
        processRequest(request, response);
    }
}
