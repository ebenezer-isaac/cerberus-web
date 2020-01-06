
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getCurrWeekYear;
import static cerberus.AttFunctions.getWeekID;
import static cerberus.printer.error;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newTimetable extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String pwd = AttFunctions.hashIt(request.getParameter("pwd"));
            if (pwd.equals("0959aab211c167df361128977811cdf1a2a46e8e47200e11dadb68b9dcb6b2ad")) {
                String weekYear[] = getCurrWeekYear().split(",");
                int week = Integer.parseInt(weekYear[1]);
                int year = Integer.parseInt(weekYear[0]);
                int weekid = getWeekID(week, year);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ?");
                ps5.setInt(1, weekid);
                ResultSet rs = ps5.executeQuery();
                if (rs.next()) {
                } else {
                    PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ?");
                    ps3.setInt(1, weekid);
                    ps3.setInt(2, 0);
                    ps3.executeUpdate();
                }
                con.close();
            }
            System.out.println("done");
        } catch (NoSuchAlgorithmException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
