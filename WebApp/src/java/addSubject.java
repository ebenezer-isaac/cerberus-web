
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addSubject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String sid = request.getParameter("subjectID").toUpperCase();
            String name = request.getParameter("subject");
            String abbr = request.getParameter("abbr");
            int sem = Integer.parseInt(request.getParameter("sem"));
            int div = 0;
            switch (sem) {
                case 1:
                    div=1;
                    break;
                case 2:
                    div = 1;
                    break;
                case 3:
                    div = 2;
                    break;
                case 4:
                    div = 2;
                    break;
                case 5:
                    div = 3;
                    break;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                stmt.executeUpdate("INSERT INTO `subject` VALUES ('" + sid + "'," + sem + ",'" + name + "'," + div + ")");
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Database Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("url", "dispSubject");
                rd.forward(request, response);
            }
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "true");
            request.setAttribute("head", "Subject Added");
            request.setAttribute("body", "The subject was added successfully<br>SubjectID : "+sid);
            request.setAttribute("url", "dispSubject");
            request.setAttribute("sec", "2");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
