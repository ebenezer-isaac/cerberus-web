
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

public class disStudDetail extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String division = request.getParameter("year");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>" + division.toUpperCase() + " Details </title>");
            out.println("</head>");
            out.println("<body align='center'>");
            out.println("<style>"
                    + "table.fixed { table-layout:fixed; }"
                    + "table.fixed th { overflow: hidden; }"
                    + "table.fixed th {width:80px;}"
                    + "table.fixed th:nth-of-type(2) {width:150px;}"
                    + "</style>");
            out.println("<style>"
                    + "input[type=number]{"
                    + "width: 35px;"
                    + "} "
                    + "</style>");
            out.print("<script>"
                    + "function zeroPad(num) {"
                    + "var s = num+'';"
                    + "while (s.length < 2) s = '0' + s;"
                    + "return(s);"
                    + "}"
                    + "</script>");
            out.println("<div>");
            out.print("<form action='editStudDetail' method='post'>");
            out.println("<h1>" + division.toUpperCase() + "</p> Details </h1>");
            out.println("<table border='1' align='center' cellpadding='5' class='fixed'>");
            out.println("<col width='20px' />");
            out.println("<tr>");
            out.println("<th> Roll </th>");
            out.println("<th> Name </th>");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from details_" + division + " ORDER BY `roll` ASC");
                ResultSetMetaData rsm = rs.getMetaData();
                int cols = rsm.getColumnCount();
                for (int i = 3; i <= cols; i++) {
                    ResultSet subname = stmt.executeQuery("select `name` from subjects where `code`= '" + rsm.getColumnLabel(i).toUpperCase() + "'");
                    while (subname.next()) {
                        out.println("<th> " + subname.getString(1) + " </th>");
                    }
                    //out.println("<th> " + rsm.getColumnLabel(i).toUpperCase() + " </th>");
                }
                rs = stmt.executeQuery("select * from details_" + division);
                out.println("</tr>");
                int line = 0;
                while (rs.next()) {
                    line++;
                    out.println("<tr>");
                    out.println("<td><input type='number' name='roll" + line + "' min='1' max='120' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1))) + "'></td>");
                    out.println("<td><input type='text' name='name" + line + "' value='" + rs.getString(2) + "'></td>");
                    for (int i = 3; i <= cols; i++) {
                        out.println("<td><input type='checkbox' name='sub" + line + "" + i + "'");
                        if (Integer.parseInt(rs.getString(i)) == 1) {
                            out.println(" checked ");
                        }
                        out.println("></td>");
                    }
                    out.println("</tr>");
                }
                out.println("</table><br><br>");
                out.println("<input type='submit' value='Submit' align='center'>"
                        + "<input type='text' name='division' value='" + division + "' hidden>"
                        + "<input type='text' name='cols' value='" + cols + "' hidden>"
                        + "<input type='text' name='rows' value='" + line + "' hidden>");
                out.println("</form><br>");
                out.println("<form action='menu' method='post'>");
                out.println("<input type='submit' value='Back'>");
                out.println("</form>");
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
