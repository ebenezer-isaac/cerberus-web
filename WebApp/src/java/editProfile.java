import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

@MultipartConfig(maxFileSize = 16177215)
public class editProfile extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String name = request.getParameter("firstname");
            Part filePart = request.getPart("avatar-file");
            InputStream inputStream = null;
            if (filePart != null) {
                inputStream = filePart.getInputStream();
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps1 = con.prepareStatement("INSERT INTO profile_pic VALUES(?,?)");
                ps1.setString(1, name);
                if (inputStream != null) {
                    ps1.setBlob(2, inputStream);
                }
                int row = ps1.executeUpdate();
                if (row > 0) {
                    out.print("<h1> File Inserted into database </h1>");
                }
                String sql2 = "SELECT photo FROM profile_pic WHERE id = ?";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, name);
                byte [] blob = null;
                ResultSet rs = ps2.executeQuery();
                while (rs.next()) {
                   blob = rs.getBytes("photo");
                    }
                    con.close();
                    System.out.println("FILE SAVED");
                     String imgString = DatatypeConverter.printBase64Binary(blob);
                     out.print("<img src=\"data:image/png;base64,"+imgString+"\"/>");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(e);
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
