
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            int access = getAccess(request);
            switch (access) {
                case 1:
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
                        m.failed(request, response, "Selected Photo is too large", "profile");
                    }
                    if (flag == 0) {
                        messages m = new messages();
                        m.success(request, response, "Profile Picture has been saved", "profile");
                    } else {
                        messages m = new messages();
                        m.failed(request, response, "New Profile Picture is not supported", "homepage");
                    }
                    break;
                case 0:
                    out.print(kids());
                    break;
                default:
                    out.print(nouser());
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
