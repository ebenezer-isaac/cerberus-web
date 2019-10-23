
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

public class NewServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            URL url = new URL("http://msubcdndwn.digitaluniversity.ac/resources/public/msub/Photosign/Photo/2017/D17CJ0000093_P.JPG");
            InputStream inputStream = url.openStream();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps1 = con.prepareStatement("UPDATE student SET photo = ? WHERE PRN=?");
                ps1.setString(2, "2017033800107501");
                if (inputStream != null) {
                    ps1.setBlob(1, inputStream);
                }
                ps1.executeUpdate();
                out.print("<h1> File Inserted into database </h1>");
                String sql2 = "SELECT name, photo FROM student WHERE PRN = ?";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, "2017033800107501");
                byte[] blob = null;
                ResultSet rs = ps2.executeQuery();
                while (rs.next()) {
                    blob = rs.getBytes("photo");
                }
                con.close();
                System.out.println("FILE SAVED");
                String imgString = DatatypeConverter.printBase64Binary(blob);
                out.print("<img src=\"data:image/png;base64," + imgString + "\"/>");
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
