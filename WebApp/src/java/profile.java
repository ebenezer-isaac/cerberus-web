
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

public class profile extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps2 = con.prepareStatement("SELECT name, photo FROM faculty WHERE facultyID = ?");
                HttpSession session = request.getSession(false);
                ps2.setString(1, session.getAttribute("user").toString());
                byte[] blob = null;
                String name = "";
                ResultSet rs = ps2.executeQuery();
                while (rs.next()) {
                    blob = rs.getBytes("photo");
                    name = rs.getString("name");
                }
                con.close();
                if (blob != null) {
                    String imgString = DatatypeConverter.printBase64Binary(blob);
                    out.print("<img style='border-radius: 10%;' src='data:image/png;base64," + imgString + "'/><br>"+name);
                }
            } catch (Exception e) {
            }
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
