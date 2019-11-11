
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
import javax.servlet.http.HttpSession;

public class saveSubSelection extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            try {
                int access = (int) session.getAttribute("access");
                int classID = Integer.parseInt(request.getParameter("division"));
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
                                String tprn = request.getParameter("prn" + index);
                                String ename = rs.getString(3);
                                String tname = request.getParameter("name" + index);
                                String eemail = rs.getString(4);
                                String temail = request.getParameter("email" + index);
                                System.out.println(troll + " " + tprn + " " + tname + " " + temail);
                                System.out.println(eroll + " " + ename + " " + eemail);
                                if (ename.equals(tname)) {
                                } else {
                                    PreparedStatement pps = con.prepareStatement("UPDATE student SET name = ? where prn = ?");
                                    pps.setString(1, tname);
                                    pps.setString(2, tprn);
                                    pps.executeUpdate();
                                }
                                if (eemail.equals(temail)) {
                                } else {
                                    PreparedStatement pps = con.prepareStatement("UPDATE student SET email = ? where prn = ?");
                                    pps.setString(1, temail);
                                    pps.setString(2, tprn);
                                    pps.executeUpdate();
                                }
                                if (eroll == troll) {
                                } else {
                                    PreparedStatement pps = con.prepareStatement("UPDATE rollcall SET rollNo = ? where prn = ?");
                                    pps.setInt(1, troll);
                                    pps.setString(2, tprn);
                                    pps.executeUpdate();
                                }
                                String et1 = rs.getString(6);
                                if (et1 != null) {
                                    int tt1 = Integer.parseInt(request.getParameter("t1" + index));
                                    if (tt1 != 1) {
                                        PreparedStatement pps = con.prepareStatement("UPDATE studentfingerprint SET template = null where prn = ? and templateID = 1");
                                        pps.setString(1, tprn);
                                        pps.executeUpdate();
                                    }
                                }
                                String et2 = rs.getString(6);
                                if (et2 != null) {
                                    int tt2 = Integer.parseInt(request.getParameter("t2" + index));
                                    if (tt2 != 1) {
                                        PreparedStatement pps = con.prepareStatement("UPDATE studentfingerprint SET template = null where prn = ? and templateID = 2");
                                        pps.setString(1, tprn);
                                        pps.executeUpdate();
                                    }
                                }
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
    }
}
