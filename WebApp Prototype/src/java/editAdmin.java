
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

public class editAdmin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String flow = request.getParameter("flow");
            if (flow.equals("add")) {
                out.print("<form action='addAdmin' method='post'>"
                        + "Username : <input type='text' name='username'/><br><br>"
                        + "Password : <input type='password' name='password'/><br><br>"
                        + "<button type='submit'>Submit</button>"
                        + "</form>");
            } else {
                try {
                    out.print("<form action='delAdmin' method='post'>");
                    out.print("<div align='center'><br>Select the Account you want to delete : <br><br>");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                    Statement stmt = con.createStatement();
                    String sql = "SELECT `username` from `login`;";
                    ResultSet rs = stmt.executeQuery(sql);
                    String select = "<select name = 'subject'>";
                    while (rs.next()) {
                        select += "<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(1) + " </option>";
                    }
                    select += "</select>";
                    out.print(select);
                    out.print("<button type='submit'>Submit</button>");
                    out.print("</form></div>");
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("message", e.getMessage());
                    request.setAttribute("redirect", "menu");
                    rd.forward(request, response);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
