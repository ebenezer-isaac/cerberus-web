
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

public class editTimetable extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int lab = Integer.parseInt(request.getParameter("lab"));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                stmt.executeUpdate("DELETE FROM `timetable"+lab+"`");
                PreparedStatement ps = con.prepareStatement("INSERT INTO `timetable"+lab+"` VALUES (?,?,?,?,?,?,?,?)");
                for (int i = 1; i <= 4; i++) {
                    switch (i) {
                        case 1:
                            ps.setString(1, "" + String.format("%02d", Integer.parseInt(request.getParameter("ts11"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("ts12"))) + ":00");
                            ps.setString(2, "" + String.format("%02d", Integer.parseInt(request.getParameter("te11"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("te12"))) + ":00");
                            break;
                        case 2:
                            ps.setString(1, "" + String.format("%02d", Integer.parseInt(request.getParameter("ts21"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("ts22"))) + ":00");
                            ps.setString(2, "" + String.format("%02d", Integer.parseInt(request.getParameter("te21"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("te22"))) + ":00");
                            break;
                        case 3:
                            ps.setString(1, "" + String.format("%02d", Integer.parseInt(request.getParameter("ts31"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("ts32"))) + ":00");
                            ps.setString(2, "" + String.format("%02d", Integer.parseInt(request.getParameter("te31"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("te32"))) + ":00");
                            break;
                        case 4:
                            ps.setString(1, "" + String.format("%02d", Integer.parseInt(request.getParameter("ts41"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("ts42"))) + ":00");
                            ps.setString(2, "" + String.format("%02d", Integer.parseInt(request.getParameter("te41"))) + ":" + String.format("%02d", Integer.parseInt(request.getParameter("te42"))) + ":00");
                            break;
                    }
                    ps.setString(3, request.getParameter("c" + i + "" + 1));
                    ps.setString(4, request.getParameter("c" + i + "" + 2));
                    ps.setString(5, request.getParameter("c" + i + "" + 3));
                    ps.setString(6, request.getParameter("c" + i + "" + 4));
                    ps.setString(7, request.getParameter("c" + i + "" + 5));
                    ps.setString(8, request.getParameter("c" + i + "" + 6));
                    ps.executeUpdate();
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("message", "Time Table Updated Successfully");
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
