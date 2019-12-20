
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getTimeID;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class delFacFingerprint extends HttpServlet {

    private static final long serialVersionUID = 1016900730734897252L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String facultyID = request.getParameter("facultyID");
                try {
                    int id = Integer.parseInt(request.getParameter("id").trim());
                    int dateID = getDateID(getCurrDate());
                    int timeID = getTimeID(getCurrTime());
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement stmt = con.prepareStatement("update `facultyfingerprint` SET template = null, SET dateID = ?, SET timeID = ? where `facultyID` = ? and templateID = ?;");
                    stmt.setInt(1, dateID);
                    stmt.setInt(2, timeID);
                    stmt.setString(3, facultyID);
                    stmt.setInt(4, id);
                    stmt.executeUpdate();
                    con.close();
                    messages a = new messages();
                    a.success(request, response, "Fingerprint " + id + " was deleted successfully", "profile");
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
