
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getTimeID;
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

public class saveStudDetails extends HttpServlet {

    private static final long serialVersionUID = 470143293826528224L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        try {
            int access = (int) session.getAttribute("access");
            int classID = Integer.parseInt(request.getParameter("division"));
            switch (access) {
                case 1:
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
                        PreparedStatement pps = con.prepareStatement("Delete from rollcall where classID =?");
                        pps.setInt(1, classID);
                        pps.executeUpdate();
                        while (rs.next()) {
                            int eroll = rs.getInt(1);
                            int troll = Integer.parseInt(request.getParameter("roll" + index));
                            String tprn = request.getParameter("prn" + index);
                            String ename = rs.getString(3);
                            String tname = request.getParameter("name" + index);
                            String eemail = rs.getString(4);
                            String temail = request.getParameter("email" + index);
                            if (ename.equals(tname)) {
                            } else {
                                pps = con.prepareStatement("UPDATE student SET name = ? where prn = ?");
                                pps.setString(1, tname);
                                pps.setString(2, tprn);
                                pps.executeUpdate();
                                dbLog(currUserName(request) + " changed name of student with prn : " + tprn + " to " + tname);
                            }
                            if (eemail.equals(temail)) {
                            } else {
                                pps = con.prepareStatement("UPDATE student SET email = ? where prn = ?");
                                pps.setString(1, temail);
                                pps.setString(2, tprn);
                                pps.executeUpdate();
                                dbLog(currUserName(request) + " changed email of student with prn : " + tprn + " to " + temail);
                            }
                            pps = con.prepareStatement("Insert into rollcall values(?,?,?)");
                            pps.setInt(1, classID);
                            pps.setInt(2, troll);
                            pps.setString(3, tprn);
                            pps.executeUpdate();
                            dbLog(currUserName(request) + " changed Roll Number of student with prn : " + tprn + " to " + troll);
                            String et1 = rs.getString(5);
                            if (et1 != null) {
                                String tt1 = request.getParameter("t1" + index);
                                if (tt1 == null) {
                                    int dateID = getDateID(getCurrDate());
                                    int timeID = getTimeID(getCurrTime());
                                    try {
                                        pps = con.prepareStatement("UPDATE studentfingerprint SET template = null, dateID = ?, timeID = ? where prn = ? and templateID = 1");
                                        pps.setInt(1, dateID);
                                        pps.setInt(2, timeID);
                                        pps.setString(3, tprn);
                                        pps.executeUpdate();
                                        dbLog(currUserName(request) + " deleted fingerprint template : 1 of student with prn : " + tprn);
                                    } catch (SQLException x) {
                                        errorLogger(x.getMessage());
                                    }
                                }
                            }
                            String et2 = rs.getString(6);
                            if (et2 != null) {
                                String tt2 = request.getParameter("t2" + index);
                                if (tt2 == null) {
                                    int dateID = getDateID(getCurrDate());
                                    int timeID = getTimeID(getCurrTime());
                                    pps = con.prepareStatement("UPDATE studentfingerprint SET template = null, dateID = ?, timeID = ? where prn = ? and templateID = 2");
                                    pps.setInt(1, dateID);
                                    pps.setInt(2, timeID);
                                    pps.setString(3, tprn);
                                    pps.executeUpdate();
                                    dbLog(currUserName(request) + " deleted fingerprint template : 2 of student with prn : " + tprn);
                                }
                            }
                            index++;
                        }
                        messages a = new messages();
                        a.success(request, response, "Student Details has been saved", "editStudDetails?class=" + classID);
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        errorLogger(e.getMessage());
                        messages a = new messages();
                        a.dberror(request, response, e.getMessage(), "viewTimetable");
                    }
                    break;
                case 0:
                    messages b = new messages();
                    b.kids(request, response);
                    break;
                default:
                    messages c = new messages();
                    c.nouser(request, response);
                    break;
            }
        } catch (IOException | ServletException e) {
            messages a = new messages();
            a.nouser(request, response);
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
