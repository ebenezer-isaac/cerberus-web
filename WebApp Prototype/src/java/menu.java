
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

public class menu extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("<title>Menu</title>");
            out.println("<style>"
                    + "table.fixed { table-layout:fixed; }"
                    + "table.fixed th { overflow: hidden; }"
                    + "table.fixed th {width:80px;}"
                    + "table.fixed th:nth-of-type(1) {width:60px;}"
                    + "table.fixed th:nth-of-type(2) {width:60px;}"
                    + "</style>");
            out.print("<table><td width='15%' align='center'>");
            out.println("<form action='seleSem' method='post'>"
                    + "<input type='submit' value='Edit Timetable'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='disSubject' method='post'>"
                    + "<input type='submit' value='Manage Subjects'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='seleSub' method='post'>"
                    + "<input type='submit' value='Manage Attendance'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='seleClass' method='post'>"
                    + "<input type='text' value='disStudDetail' name='redirect' hidden>"
                    + "<button type='submit' >Manage Student Detail</button>"
                    + "</form>"
                    + "<br>"
                    + "<form action='seleFile' method='post'>"
                    + "<input type='submit' value='Upload New Data'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='disAdmin' method='post'>"
                    + "<input type='submit' value='Manage Admins'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='disPin' method='post'>"
                    + "<input type='submit' value='View PinCode'>"
                    + "</form>"
                    + "<br>"
                    + "<form action='index.html'>"
                    + "<input type='submit' value='Logout'>"
                    + "</form>"
                    + "</td><td width='70%'>");
            out.print("<h3 align='center'>TimeTables</h3>");
            for (int timetables = 1; timetables <= 3; timetables++) {
                out.print("<h4 align='center'>Lab " + timetables + "</h4>");
                out.print("<table class='fixed'cellpadding='5' border ='1' align = 'center'>");
                out.print("<tr align = center>");
                out.print("<th>Start_Time</th>");
                out.print("<th>End_Time</th>");
                out.print("<th>Monday</th>");
                out.print("<th>Tuesday</th>");
                out.print("<th>Wednesday</th>");
                out.print("<th>Thursday</th>");
                out.print("<th>Friday</th>");
                out.print("<th>Saturday</th>");
                out.print("</tr>");
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("Select * from `timetable" + timetables + "`");
                    while (rs.next()) {
                        out.print("<tr align='center'>");
                        out.print("<th>" + rs.getString(1).substring(0, 5) + "</th>");
                        out.print("<th>" + rs.getString(2).substring(0, 5) + "</th>");
                        for (int j = 1; j <= 6; j++) {
                            out.print("<td>" + rs.getString(j + 2) + "</td>");
                        }
                        out.print("</tr>");
                    }
                    con.close();
                    out.print("</table><br><br>");

                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("message", e.getMessage());
                    request.setAttribute("redirect", "menu");
                    rd.forward(request, response);
                }
            }
            out.print("</td></tr></table>");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
