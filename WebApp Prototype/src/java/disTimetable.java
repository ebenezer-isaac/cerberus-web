
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

public class disTimetable extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("<head><title>TimeTable</title></head>");
            String subs[] = new String[30];
            int no_of_subs = 0;
            int selesem = Integer.parseInt(request.getParameter("SeleSem"));
            int lab = Integer.parseInt(request.getParameter("lab"));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT `code` from `subjects` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ");");
                while (rs.next()) {
                    subs[no_of_subs] = rs.getString(1);
                    no_of_subs++;
                }
                no_of_subs--;
                out.println("<style>"
                        + "input[type=number]{"
                        + "width: 35px;"
                        + "} "
                        + "</style>");
                out.println("<style>"
                        + "table.fixed { table-layout:fixed; }"
                        + "table.fixed th { overflow: hidden; }"
                        + "table.fixed th {width:80px;}"
                        + "table.fixed th:nth-of-type(1) {width:60px;}"
                        + "table.fixed th:nth-of-type(2) {width:60px;}"
                        + "</style>");
                out.print("<script>"
                        + "function zeroPad(num) {"
                        + "var s = num+'';"
                        + "while (s.length < 2) s = '0' + s;"
                        + "return(s);"
                        + "}"
                        + "</script>");
                out.print("<body align = 'center'><br><br>");
                out.print("<form action='editTimetable' method='post'>");
                out.print("<table cellpadding='5' border ='1' align = 'center'>");
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
                rs = stmt.executeQuery("Select * from `timetable" + lab + "`");
                int line = 1;
                while (rs.next()) {
                    out.print("<tr align='center'>");
                    out.print("<th><input type='number' name='ts" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(0, 2))) + "'>");
                    out.print(":<input type='number' name='ts" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1).substring(3, 5))) + "'></th>");
                    out.print("<th><input type='number' name='te" + line + "1' min='1' max='24' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(0, 2))) + "'>");
                    out.print(":<input type='number' name='te" + line + "2' min='0' max='59' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(2).substring(3, 5))) + "'></th>");
                    for (int j = 1; j <= 6; j++) {
                        out.print("<td>");
                        out.print("<select name = 'c" + line + "" + j + "' id = 'c" + line + "" + j + "'>");
                        out.print("<option name='Sub' value='-'>No Lab</option>");
                        for (int k = 0; k <= no_of_subs; k++) {
                            out.print("<option name='Sub' value='" + subs[k] + "' ");
                            if (subs[k].equals(rs.getString(j + 2))) {
                                out.print("selected ");
                            }
                            out.print(">" + subs[k] + "</option>");
                        }
                        out.print("</select>");
                        out.print("</td>");
                    }
                    out.print("</tr>");
                    line++;
                }
                out.print("</table><br><br>");
                out.print("<input type='text' name='lab' value='" + lab + "' hidden>");
                out.print("<input type='submit' value='Submit' align='center'>");
                out.print("</form>");
                out.println("<form action='menu' method='post'>");
                out.println("<input type='submit' value='Back'>");
                out.println("</form>");
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
