
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addSlot extends HttpServlet {

    private static final long serialVersionUID = 7825821117413853516L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String stime = request.getParameter("stime");
                String etime = request.getParameter("etime");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement pps = con.prepareStatement("SELECT max(slotID) FROM `slot`");
                    ResultSet rs1 = pps.executeQuery();
                    int max = 0;
                    while (rs1.next()) {
                        max = rs1.getInt(1);
                    }
                    PreparedStatement pp = con.prepareStatement("INSERT INTO `slot`(`slotID`, `startTime`, `endTime`) VALUES (?,?,?)");
                    pp.setInt(1, max + 1);
                    pp.setString(2, stime);
                    pp.setString(3, etime);
                    pp.executeUpdate();
                    con.close();
                    dbLog(currUserName(request)+" added a new slot timing "+stime+" - "+etime);
                    messages a = new messages();
                    a.success(request, response, "New Slot has been added", "viewTimetable");
                } catch (ClassNotFoundException | SQLException e) {
                    messages a = new messages();
                    a.dberror(request, response, e.getMessage(), "homepage");
                }
                break;
            case 0:
                messages a = new messages();
                a.kids(request, response);
                break;
            default:
                messages b = new messages();
                b.nouser(request, response);
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
