
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getTimeID;
import static cerberus.AttFunctions.get_class_from_sub;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class saveAttendance extends HttpServlet {

    private static final long serialVersionUID = -6754770041345829667L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                try {
                    String subjectID = request.getParameter("subjectid");
                    String schedules[] = (request.getParameter("schedules")).split(",");
                    int no_of_subs = schedules.length;
                    for (int x = 0; x < no_of_subs; x++) {
                        int scheduleid = Integer.parseInt(schedules[x]);
                        int line = Integer.parseInt(request.getParameter("line"));
                        System.out.println("no of students rapid att : " + line);
                        int timeID = getTimeID(getCurrTime());
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            PreparedStatement insert = con.prepareStatement("insert into attendance values(null,?,?,?)");
                            PreparedStatement delete = con.prepareStatement("delete from attendance where scheduleid=? and prn = ?");
                            for (int i = 1; i < line; i++) {
                                String prn = request.getParameter("prn" + i);
                                String att = request.getParameter("att" + i + "," + scheduleid);
                                System.out.println("prn : " + prn + " " + scheduleid + " " + att);
                                if (att != null) {
                                    try {
                                        PreparedStatement select = con.prepareStatement("select attendance.attendanceID from attendance where prn = ? and scheduleid = ?");
                                        select.setString(1, prn);
                                        select.setInt(2, scheduleid);
                                        ResultSet check = select.executeQuery();
                                        int dup = 0;
                                        while (check.next()) {
                                            dup = 1;
                                            System.out.println("duplicate");
                                        }
                                        if (dup != 1) {
                                            System.out.println("insert : " + prn);
                                            insert.setString(1, prn);
                                            insert.setInt(2, scheduleid);
                                            insert.setInt(3, timeID);
                                            insert.executeUpdate();
                                        }
                                    } catch (SQLException y) {
                                        y.printStackTrace();
                                    }
                                } else {
                                    try {
                                        System.out.println("delete : " + prn);
                                        delete.setInt(1, scheduleid);
                                        delete.setString(2, prn);
                                        delete.executeUpdate();
                                    } catch (SQLException y) {
                                        y.printStackTrace();
                                    }
                                }
                            }
                        } catch (ClassNotFoundException | SQLException z) {
                            z.printStackTrace();
                            messages b = new messages();
                            b.error(request, response, z.getMessage(), "viewTimetable");
                        }
                    }
                    messages a = new messages();
                    a.success(request, response, "Attendance has been saved", "attendance?class="+get_class_from_sub(subjectID));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    messages b = new messages();
                    b.error(request, response, e.getMessage(), "viewTimetable");
                }
                break;

            case 0:
                messages b = new messages();
                b.kids(request, response);
                break;
            default:
                messages c = new messages();
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
