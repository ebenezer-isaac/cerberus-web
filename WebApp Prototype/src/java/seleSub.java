
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

public class seleSub extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Select Subject</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div align='center'>");
            try {
                out.print("<form action='disAtt' method='post'>");
                out.print("<div align='center'><br>Select the subject you want to view : <br><br>");
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                String sql = "SELECT `code`,`name` from `subjects` order by `code` asc;";
                ResultSet rs = stmt.executeQuery(sql);
                out.print("<select name = 'subject'>");
                while (rs.next()) {
                    out.print("<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(1) + " - " + rs.getString(2) + " </option>");
                }
                out.print("</select><br><br>Sort By:<br><br>");
                out.print("<select name='sort'>");
                out.print("<option value='roll'>Roll Number</option>");
                out.print("<option value='perc'>Percentage</option><select>");
                out.print("<br><br><button type='submit'>Submit</button>");
                out.print("</form></div>");
                out.print("<br><form action='menu' method='post' align='center'>");
                out.print("<button type='submit'>Back</button>");
                out.print("</form>");
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
