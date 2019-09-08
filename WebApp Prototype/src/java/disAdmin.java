
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

public class disAdmin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<style>\n"
                    + "input[type=number]{\n"
                    + "    width: 35px;\n"
                    + "} \n"
                    + "</style>");
            out.print("<body><br><br>");
            out.print("<table cellpadding='5' border ='1' align = 'center'>");
            out.print("<tr>");
            out.print("<th>Username</th></tr>");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("Select `username` from `login` ORDER BY `username` ASC");
                while (rs.next()) {
                    out.print("</tr>");
                        out.print("<td>" + rs.getString(1) + "</td>");
                    out.print("</tr>");
                }
                out.print("</table><br>");
                out.print("<form action='editAdmin' method='post' align='center'>");
                out.print("<input type='radio' name='flow' value='add'> Add Acccount<br>");
                out.print("<input type='radio' name='flow' value='delete'> Delete  Account<br><br>");
                out.print("<input type='submit' value='Submit'>");
                out.print("</form>");
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            out.println("<form action='menu' method='post' align='center'>"
                    + "<input type='submit' value='Back'>"
                    + "</form>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
