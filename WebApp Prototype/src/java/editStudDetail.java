
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class editStudDetail extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String division = request.getParameter("division");
            int cols = Integer.parseInt(request.getParameter("cols"));
            int rows = Integer.parseInt(request.getParameter("rows"));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                stmt.executeUpdate("DELETE FROM `details_" + division + "`");
                String sql = "INSERT INTO `details_ty` VALUES (";
                for (int i = 1; i < cols; i++) {
                    sql += "?,";
                }
                sql += "?)";
                PreparedStatement ps = con.prepareStatement(sql);
                for (int i = 1; i <= rows; i++) {
                    ps.setString(1, request.getParameter("roll" + i));
                    ps.setString(2, request.getParameter("name" + i));
                    out.print(request.getParameter("roll" + i) + " " + request.getParameter("name" + i) + " ");
                    for (int j = 3; j <= cols; j++) {
                        //out.print(request.getParameter("sub" +    i + "" + j + ""));
                        if (request.getParameter("sub" + i + "" + j + "") != null) {
                            ps.setInt(j, 1);
                        } else {
                            ps.setInt(j, 0);
                        }
                    }
                    ps.executeUpdate();
                    out.print("<br>");
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("message", ""+division+" Details Updated Successfully");
            request.setAttribute("redirect", "menu");
            rd.forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
