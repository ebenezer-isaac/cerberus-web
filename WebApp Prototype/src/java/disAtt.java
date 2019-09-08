
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

public class disAtt extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String sub = request.getParameter("subject");
        String sort = request.getParameter("sort");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>" + sub.toUpperCase() + " Details </title>");
            out.println("<style>"
                    + "table.fixed { table-layout:fixed; }"
                    + "table.fixed th { overflow: hidden; }"
                    + "table.fixed th {width:80px;}"
                    + "table.fixed th:nth-of-type(1) {width:50px;}"
                    + "table.fixed th:nth-of-type(2) {width:50px;}"
                    + "table.fixed th:nth-of-type(3) {width:50px;}"
                    + "table.fixed th:nth-of-type(4) {width:50px;}"
                    + ".absent{color:white;background-color:#ef3e3e;}"
                    + ".present{color:white;background-color:##c9ed53;}"
                    + "</style>");
            out.println("</head>");
            out.println("<body align='center'>");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                out.println("<h1>" + sub.toUpperCase() + "</h1>");
                ResultSet rs = stmt.executeQuery("select `name` from `subjects` where `code` = '" + sub + "'");
                while (rs.next()) {
                    out.println("<h1>" + rs.getString(1) + "</h1>");
                }
                out.println("<table border='1' align='center' cellpadding='5' class='fixed'>");
                out.println("<col width='20px' />");
                out.println("<tr>");
                out.println("<th> Class </th>");
                out.println("<th> Roll </th>");
                out.println("<th> Total </th>");
                out.println("<th> Percentage </th>");

                rs = stmt.executeQuery("select * from " + sub + " ORDER BY `"+sort+"` ASC");
                ResultSetMetaData rsm = rs.getMetaData();
                int cols = rsm.getColumnCount();
                for (int print = 5; print <= cols; print++) {
                    out.println("<th> " + rsm.getColumnLabel(print).toUpperCase() + " </th>");
                }
                out.println("</tr>");
                while (rs.next()) {
                    out.println("<tr>");
                    for (int col = 1; col <= cols; col++) {
                        switch (col) {
                            case 1:
                                switch (Integer.parseInt(rs.getString(col))) {
                                    case 0:
                                        out.println("<th>FY</th>");
                                        break;
                                    case 1:
                                        out.println("<th>SY</th>");
                                        break;
                                    case 2:
                                        out.println("<th>TY</th>");
                                        break;
                                }
                                break;
                            case 2:
                                out.println("<th>" + rs.getString(col) + "</th>");
                                break;
                            case 3:
                                out.println("<td>" + rs.getString(col) + "</td>");
                                break;
                            case 4:
                                if (rs.getFloat(col) >= 75) {
                                    out.println("<td class='present'>" + rs.getString(col) + "%</td>");
                                } else {
                                    out.println("<td class='absent'>" + rs.getString(col) + "%</td>");
                                }
                                break;
                            default:
                                if (rs.getString(col).equals("A")) {
                                    out.println("<td class='absent'>" + rs.getString(col) + "</td>");
                                } else {
                                    out.println("<td class='present'>" + rs.getString(col) + "</td>");
                                }

                                break;
                        }
                    }
                    out.println("</tr>");
                }
                out.println("</table><br><br>");
                out.println("<br>");
                out.println("<form action='seleSub' method='post'>");
                out.println("<input type='submit' value='Back'>");
                out.println("</form>");
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "seleSub");
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
