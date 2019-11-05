
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cerberus.*;

public class saveSlot extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession(true);
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            PreparedStatement ps4 = con.prepareStatement("SELECT `slotID`,`startTime`,`endTime` from `slot` order by startTime,endTime ASC;");
                            ResultSet rs = ps4.executeQuery();
                            while (rs.next()) {
                                String exist = rs.getString(2) + " , " + rs.getString(3);
                                String stime = request.getParameter("stime" + rs.getString(1))+":00";
                                String etime = request.getParameter("etime" + rs.getString(1))+":00";
                                String table = stime + " , " + etime;
                                if (exist.equals(table)) {
                                } else {
                                    PreparedStatement pps = con.prepareStatement("UPDATE `slot` SET `startTime`=?,`endTime`=? WHERE slotID=?");
                                    pps.setString(1, stime);
                                    pps.setString(2, etime);
                                    pps.setInt(3, rs.getInt(1));
                                    pps.executeUpdate();
                                }
                            }
                        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                            messages a = new messages();
                            a.dberror(request, response, e.getMessage(), "viewTimetable");
                        }
                        messages a = new messages();
                        a.success(request, response, "Timings have been saved", "viewTimetable");
                        break;

                    default:
                        messages b = new messages();
                        b.kids(request, response);
                        break;
                }
            } catch (IOException | ServletException e) {
                messages a = new messages();
                a.nouser(request, response);
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
