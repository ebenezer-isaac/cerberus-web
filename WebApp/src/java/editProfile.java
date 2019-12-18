import cerberus.messages;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = 16177215)
public class editProfile extends HttpServlet {

    private static final long serialVersionUID = -3034680778908831068L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            Part filePart = request.getPart("avatar-file");
            InputStream inputStream = null;
            if (filePart != null) {
                inputStream = filePart.getInputStream();
            }
            int flag = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                PreparedStatement ps1 = con.prepareStatement("UPDATE faculty SET photo = ? WHERE facultyID=?;");
                ps1.setString(2, session.getAttribute("user").toString());
                if (inputStream != null) {
                    ps1.setBlob(1, inputStream);
                } else {
                    flag = 1;
                }
                ps1.executeUpdate();
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                messages m = new messages();
                m.dberror(request, response, "Selected Photo is too large", "homepage");
            }
            if (flag == 0) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Request Successfull");
                request.setAttribute("body", "Your Profile Picture has been changed");
                request.setAttribute("url", "homepage");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
            } else {
                messages m = new messages();
                m.dberror(request, response, "New Profile Picture is not supported", "homepage");
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
