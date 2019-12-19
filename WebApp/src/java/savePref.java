
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
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
import javax.servlet.http.HttpSession;

public class savePref extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    String subjects[] = request.getParameterValues("subjects");
                    HttpSession session = request.getSession(false);
                    String facultyID = session.getAttribute("user").toString();
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("delete from facultysubject WHERE facultyID = ?");
                         ps.setString(1, facultyID);
                        ps.executeUpdate();
                        for (String subject : subjects) {
                            ps = con.prepareStatement("INSERT INTO `facultysubject` VALUES (?,?)");
                            ps.setString(1, facultyID);
                            ps.setString(2, subject);
                            ps.executeUpdate();
                        }
                        messages a = new messages();
                        a.success(request, response, "Your preferences have been saved", "profile");
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "profile");
                    }
                    break;
                case 0:
                    messages b = new messages();
                    b.kids(request, response);
                    break;
                default:
                    messages c = new messages();
                    c.nouser(request, response);
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
