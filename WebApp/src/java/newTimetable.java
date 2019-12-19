
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getWeekID;
import static cerberus.printer.error;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newTimetable extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int week = Integer.parseInt(request.getParameter("week"));
        try {
            String pwd = AttFunctions.hashIt(request.getParameter("pwd"));
            if (pwd.equals("0959aab211c167df361128977811cdf1a2a46e8e47200e11dadb68b9dcb6b2ad")) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int weekid = getWeekID(week,year);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ?");
                ps5.setInt(1, weekid);
                ResultSet rs = ps5.executeQuery();
                int flag = 0;
                while (rs.next()) {
                    flag = 1;
                    break;
                }
                if (flag == 0) {
                    PreparedStatement ps10 = con.prepareStatement("SELECT weekID FROM `week` ORDER BY `week`.`weekID` DESC");
                    rs = ps10.executeQuery();
                    while (rs.next() && flag == 0) {
                        PreparedStatement ps9 = con.prepareStatement("SELECT * FROM timetable where weekID = ?");
                        ps9.setInt(1, rs.getInt(1));
                        ResultSet rs1 = ps9.executeQuery();
                        while (rs1.next()) {
                            PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ?");
                            ps3.setInt(1, weekid);
                            ps3.setInt(2, rs.getInt(1));
                            ps3.executeUpdate();
                            flag = 1;
                            break;
                        }
                        break;
                    }
                }
                con.close();
            }
        } catch (NoSuchAlgorithmException | ClassNotFoundException | SQLException e) {
            error(e.getMessage());
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
