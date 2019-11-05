
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class delSlot extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String slotID = request.getParameter("slotID");
            System.out.println(slotID);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement stmt = con.prepareStatement("Delete from `slot` where `slotID` = ?;");
                stmt.setString(1, slotID);
                stmt.executeUpdate();
                con.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                messages a = new messages();
                a.dberror(request, response, e.getMessage(), "homepage");
            }
            messages a = new messages();
            a.success(request, response, "Slot has been deleted", "viewTimetable");
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
