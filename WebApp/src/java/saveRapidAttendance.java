
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getTimeID;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class saveRapidAttendance extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                try {
                    int scheduleid = Integer.parseInt(request.getParameter("scheduleid"));
                    int line = Integer.parseInt(request.getParameter("line"));
                    System.out.println("no of students rapid att : "+line);
                    int timeID = getTimeID(getCurrTime());
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement insert = con.prepareStatement("insert into attendance values(null,?,?,?)");
                        PreparedStatement delete = con.prepareStatement("delete from attendance where scheduleid=? and prn = ?");
                        for (int i = 1; i < line; i++) {
                            String prn = request.getParameter("prn" + i);
                            String att = request.getParameter("att" + i);
                            int status = 0;
                            if (att != null) {
                                try {
                                    insert.setString(1, prn);
                                    insert.setInt(2, scheduleid);
                                    insert.setInt(3, timeID);
                                    insert.executeUpdate();
                                } catch (SQLException x) {
                                    x.printStackTrace();
                                }
                            } else {
                                try {
                                    delete.setInt(1, scheduleid);
                                    delete.setString(2, prn);
                                    delete.executeUpdate();
                                } catch (SQLException x) {
                                    x.printStackTrace();
                                }
                            }
                        }
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
                messages b = new messages();
                b.kids(request, response);
                break;
            default:
                messages c = new messages();

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
