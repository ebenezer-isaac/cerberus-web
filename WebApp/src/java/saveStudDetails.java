
import cerberus.*;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class saveStudDetails extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            try {
                int access = (int) session.getAttribute("access");
                int classID = Integer.parseInt(request.getParameter("class"));
                int rows = Integer.parseInt(request.getParameter("rows"));
                int cols = Integer.parseInt(request.getParameter("cols"));
                switch (access) {
                    case 1:
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            PreparedStatement ps4 = con.prepareStatement("SELECT rollcall.rollNo, student.PRN, student.name, student.email,"
                                    + "MAX(CASE WHEN studentfingerprint.templateID = 1 and studentfingerprint.template is not null THEN concat(' 1 ',' ') END) as Template1, "
                                    + "MAX(CASE WHEN studentfingerprint.templateID = 2 and studentfingerprint.template is not null THEN concat(' 1 ',' ') END) as Template2 "
                                    + "FROM student "
                                    + "INNER JOIN rollcall  on  rollcall.PRN = student.PRN "
                                    + "INNER JOIN studentfingerprint  on  student.PRN = studentfingerprint.PRN "
                                    + "where student.PRN in (select rollcall.PRN "
                                    + "from rollcall "
                                    + "where rollcall.classID = ?) "
                                    + "GROUP BY student.PRN "
                                    + "ORDER by rollcall.rollNo;");
                            ps4.setInt(1, classID);
                            ResultSet rs = ps4.executeQuery();
                            int index = 1;
                            while (rs.next()) {
                                int eroll = rs.getInt(1);
                                int troll = Integer.parseInt(request.getParameter("roll" + index));
                                int eprn = rs.getInt(2);
                                int tprn = Integer.parseInt(request.getParameter("prn" + index));
                                int ename = rs.getInt(4);
                                int tname = Integer.parseInt(request.getParameter("name" + index));
                                int eemail = rs.getInt(5);
                                int temail = Integer.parseInt(request.getParameter("email" + index));
                                /*PreparedStatement pps = con.prepareStatement("UPDATE `timetable` SET `subjectID`=?,`batchID`=? WHERE slotID=? and labID=? and weekID=? and dayID=?");
                                pps.setString(1, arrOfStr[0]);
                                pps.setInt(2, Integer.parseInt(arrOfStr[1]));
                                pps.setInt(3, (slot + 1));
                                pps.setInt(4, labid);
                                pps.setInt(5, weekID);
                                pps.setInt(6, (j + 1));
                                pps.executeUpdate();
                                String et1 = rs.getString(6);
                                if (et1 == null) {
                                    int tt1 = Integer.parseInt(request.getParameter("t1" + index));
                                }
                                String et2 = rs.getString(6);
                                if (et2 == null) {
                                    int tt2 = Integer.parseInt(request.getParameter("t2" + index));
                                }*/

                                index++;
                            }
                        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                            messages a = new messages();
                            a.dberror(request, response, e.getMessage(), "viewTimetable");
                        }
                        messages a = new messages();
                        a.success(request, response, "TimeTable has been saved", "viewTimetable");
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

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
