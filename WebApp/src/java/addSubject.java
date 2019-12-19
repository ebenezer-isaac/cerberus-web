
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
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

public class addSubject extends HttpServlet {

    private static final long serialVersionUID = -9146526388180484963L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                try {
                    String sid = request.getParameter("subjectID").toUpperCase();
                    String name = request.getParameter("subject");
                    String abbreviation = request.getParameter("abbr");

                    int oddeve = Integer.parseInt(request.getParameter("sem"));
                    int classID = Integer.parseInt(request.getParameter("class"));

                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("INSERT INTO `subject` VALUES (?,?,?,?,?)");
                    int semNum = AttFunctions.getSem(oddeve, classID);
                    ps.setString(1, sid.toUpperCase());
                    ps.setString(2, name);
                    ps.setInt(3, semNum);
                    ps.setString(4, abbreviation);
                    ps.setInt(5, classID);
                    ps.executeUpdate();
                    con.close();
                    messages a = new messages();
                    a.success(request, response, "The subject was added successfully<br>Subject Code : " + sid, "homepage");
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    messages a = new messages();
                    a.dberror(request, response, e.getMessage(), "viewSubject");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
