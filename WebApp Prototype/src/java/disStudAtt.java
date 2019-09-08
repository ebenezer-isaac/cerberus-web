
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class disStudAtt extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("<head><title>Attendance</title></head><div align = 'center'>");
            int no_of_subs = 0;
            String sub_code[] = null;
            String sub_name[] = null;
            String name = "";
            int roll = Integer.parseInt(request.getParameter("roll"));
            String division = request.getParameter("class");
            String sort = request.getParameter("sort");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("Select * from `details_" + division + "` where roll = " + roll);
                ResultSetMetaData rsm = rs.getMetaData();
                sub_code = new String[rsm.getColumnCount()];

                while (rs.next()) {
                    name = rs.getString(2);
                    for (int col = 3; col <= rsm.getColumnCount(); col++) {
                        if (Integer.parseInt(rs.getString(col)) == 1) {
                            sub_code[no_of_subs] = rsm.getColumnName(col);

                            no_of_subs++;
                        }
                    }
                }
                no_of_subs--;
                out.print("<table><tr><td align='left'>");
                out.print("<fieldset><legend>Details</legend>");
                out.print("Name  : " + name + "<br>");
                out.print("Class : " + division.toUpperCase() + "<br>");
                out.print("Roll &nbsp: " + roll + "<br>");
                out.print("</fieldset></td></tr><tr><td>");
                out.print("<fieldset><legend>Subjects Selected</legend>");
                for (int print = 0; print <= no_of_subs; print++) {
                    rs = stmt.executeQuery("Select `name` from `subjects` where `code` = '" + sub_code[print].toUpperCase() + "'");
                    while (rs.next()) {
                        out.println(" Subject " + (print + 1) + " : " + sub_code[print] + " - " + rs.getString(1) + "<br>");
                    }
                }
                con.close();
                out.print("</fieldset></td></tr></table>");
                if (sort.equals("sub")) {
                    out.print("<h2>Sort by Subject</h2>");
                } else {
                    out.print("<h2>Summary</h2>");
                }
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "index.html");
                rd.forward(request, response);
            }
            if (no_of_subs > -1) {
                int divi = -1;
                switch (division) {
                    case "fy":
                        divi = 0;
                        break;
                    case "sy":
                        divi = 1;
                        break;
                    case "ty":
                        divi = 2;
                        break;
                }

                if (sort.equals("sub")) {
                    out.print("</div>");
                    String dates[] = new String[20];
                    String status[] = new String[20];
                    for (int tables = 0; tables <= no_of_subs; tables++) {
                        int no_of_labs = 0;
                        out.print("<hr>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("Select `name` from `subjects` where `code` = '" + sub_code[tables].toUpperCase() + "'");
                            while (rs.next()) {
                                out.print("<h4 align ='center'>" + rs.getString(1) + "</h4>");
                            }
                            out.print("<table border='1' cellpadding='5' align = 'center'>");
                            out.print("<tr align = 'center'>");
                            out.print("<th>Total</th>");
                            rs = stmt.executeQuery("Select * from `" + sub_code[tables] + "` where roll = " + roll + " and `class` = " + divi);
                            ResultSetMetaData rsm = rs.getMetaData();
                            while (rs.next()) {
                                for (int col = 5; col <= rsm.getColumnCount(); col++) {
                                    dates[no_of_labs] = rsm.getColumnName(col);
                                    status[no_of_labs] = rs.getString(col);
                                    out.print("<th>" + dates[no_of_labs] + "</th>");
                                    no_of_labs++;
                                }
                            }
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("message", e.getMessage());
                            request.setAttribute("redirect", "index.html");
                            rd.forward(request, response);
                        }
                        out.print("</tr>");
                        out.print("<tr align = 'center'>");
                        out.print("<th>Status</th>");
                        for (int col = 0; col < no_of_labs; col++) {
                            out.print("<td>" + status[col] + "</td>");
                        }
                        out.print("</tr>");
                        out.print("</table>");
                        out.print("<br><br>");
                    }
                } else {
                    out.println("<style>"
                            + "table.fixed { table-layout:fixed; }"
                            + "table.fixed th { overflow: hidden; }"
                            + "table.fixed th {width:80px;}"
                            + "</style>");
                    out.print("<table border='1' cellpadding='5' align='center'>");
                    out.print("<tr align = 'center'>");
                    out.print("<th>Subjects</th>");
                    for (int col = 0; col <= no_of_subs; col++) {
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("Select `name` from `subjects` where `code` = '" + sub_code[col].toUpperCase() + "'");
                            while (rs.next()) {
                                out.print("<th>" + rs.getString(1) + "</th>");
                            }
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("message", e.getMessage());
                            request.setAttribute("redirect", "index.html");
                            rd.forward(request, response);
                        }

                    }
                    out.print("</tr>");
                    out.print("<tr align = 'center'>");
                    out.print("<th>Percentage</th>");
                    for (int col = 0; col <= no_of_subs; col++) {
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();

                            ResultSet rs = stmt.executeQuery("SELECT `perc` from `" + sub_code[col] + "` where `roll` = " + roll + ";");
                            while (rs.next()) {
                                out.print("<td>" + rs.getString(1) + "%</td>");
                            }
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("message", e.getMessage());
                            request.setAttribute("redirect", "index.html");
                            rd.forward(request, response);
                        }
                    }
                    out.print("</tr>");
                    out.print("</table>");
                }
                out.println("</form><br>");
                out.println("<form  align='center' action='index.html'>");
                out.println("<input type='submit' value='Back'>");
                out.println("</form>");
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", "Student Record Not Found or No Valid Subject Selection");
                request.setAttribute("redirect", "index.html");
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
