
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addSlot extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String stime = request.getParameter("stime");
            String etime = request.getParameter("etime");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement pps = con.prepareStatement("SELECT max(slotID) FROM `slot`");
                ResultSet rs1 = pps.executeQuery();
                int max = 0;
                while (rs1.next()) {
                    max = rs1.getInt(1);
                }
                PreparedStatement pp = con.prepareStatement("INSERT INTO `slot`(`slotID`, `startTime`, `endTime`) VALUES (?,?,?)");
                pp.setInt(1, max + 1);
                pp.setString(2, stime);
                pp.setString(3, etime);
                pp.executeUpdate();
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
            }
            messages a = new messages();
            a.success(request, response, "New Slot has been added", "viewTimetable");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
