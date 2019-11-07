
import cerberus.AttFunctions;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addSubject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:

                String sid = request.getParameter("subjectID").toUpperCase();
                String name = request.getParameter("subject");
                String abbreviation = request.getParameter("abbr");

                int oddeve = Integer.parseInt(request.getParameter("sem"));
                int classID = Integer.parseInt(request.getParameter("class"));
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("INSERT INTO `subject` VALUES (?,?,?,?,?)");
                    int semNum = AttFunctions.getSem(oddeve, classID);
                    ps.setString(1, sid.toUpperCase());
                    ps.setInt(2, semNum);
                    ps.setString(3, name);
                    ps.setString(4, abbreviation);
                    ps.setInt(5, classID);
                    ps.executeUpdate();
                    con.close();
                    messages a = new messages();
                    a.success(request, response, "The subject was added successfully<br>SubjectID : " + sid, "homepage");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private int getAccess(HttpServletRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
