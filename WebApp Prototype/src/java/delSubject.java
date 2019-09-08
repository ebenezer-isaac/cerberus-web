
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class delSubject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String sub = request.getParameter("subject");
            String division = "";
            int sem = 0;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("Select `sem` from `subjects` where `code`='" + sub + "'");
                while (rs.next()) {
                    sem = Integer.parseInt(rs.getString(1));
                }
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
                stmt.executeUpdate("Delete from `subjects` where `code` = '" + sub + "';");
                stmt.executeUpdate("DROP TABLE `" + sub + "`");
                stmt.executeUpdate("ALTER TABLE `details_" + division + "` DROP COLUMN `" + sub + "`");
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", "The subject `" + sub + "` has been successfully deleted");
                request.setAttribute("redirect", "disSubject");
                rd.forward(request, response);
                con.close();
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
