
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
        try (PrintWriter out = response.getWriter()) {
            String sid = request.getParameter("subjectID").toUpperCase();
            String name = request.getParameter("subject");
            String abbreviation = request.getParameter("abbr");

            int oddeve = Integer.parseInt(request.getParameter("sem"));
            int classID = Integer.parseInt(request.getParameter("class"));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO `subject` VALUES (?,?,?,?,?)");
                    int semNum = AttFunctions.getSem(oddeve, classID);
                    ps.setString(1, sid.toUpperCase());
                    ps.setInt(2, semNum);
                    ps.setString(3, name);
                    ps.setString(4, abbreviation);
                    ps.setInt(5, classID);
                    ps.executeUpdate();
                    con.close();
                }
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Subject Added");
                request.setAttribute("body", "The subject was added successfully<br>SubjectID : " + sid);
                request.setAttribute("url", "dispSubject");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
            } catch (SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Database Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("url", "dispSubject");
                rd.forward(request, response);
            }
        } catch (ClassNotFoundException e) {

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
