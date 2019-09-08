
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
            String sid = request.getParameter("code").toUpperCase();
            String name = request.getParameter("name");
            int sem = Integer.parseInt(request.getParameter("sem"));
            int labs = Integer.parseInt(request.getParameter("labs"));
            String division = "";
            switch (sem) {
                case 1:
                    division = "fy";
                    break;
                case 2:
                    division = "fy";
                    break;
                case 3:
                    division = "sy";
                    break;
                case 4:
                    division = "sy";
                    break;
                case 5:
                    division = "ty";
                    break;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                stmt.executeUpdate("INSERT INTO `subjects` VALUES ('" + sid + "'," + sem + ",'" + name + "'," + labs + ")");
                stmt.executeUpdate("ALTER TABLE `details_" + division + "` ADD " + sid + " int(1) NOT NULL DEFAULT '0'");
                stmt.executeUpdate("CREATE TABLE " + sid + " (class int (1) not null, roll int (3) not null, total int (2) not null, perc float not null DEFAULT 0    )");
                con.close();
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", "Subject has been added successfully");
                request.setAttribute("redirect", "disSubject");
                rd.forward(request, response);
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
