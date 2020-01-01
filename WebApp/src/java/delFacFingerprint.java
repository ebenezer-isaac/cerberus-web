
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getTimeID;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class delFacFingerprint extends HttpServlet {

    private static final long serialVersionUID = 1016900730734897252L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("text/html;charset=UTF-8");
            int access = getAccess(request);
            switch (access) {
                case 1:
                    try {
                        HttpSession session = request.getSession(false);
                        String facultyID = (String) session.getAttribute("user");
                        int id = Integer.parseInt(request.getParameter("id").trim());
                        int dateID = getDateID(getCurrDate());
                        int timeID = getTimeID(getCurrTime());
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement stmt = con.prepareStatement("update `facultyfingerprint` SET template = null, dateID = ?, timeID = ? where `facultyID` = ? and templateID = ?;");
                        stmt.setInt(1, dateID);
                        stmt.setInt(2, timeID);
                        stmt.setString(3, facultyID);
                        stmt.setInt(4, id);
                        stmt.executeUpdate();
                        con.close();
                        out.print("<script>setContent('/Cerberus/profile');</script>");
                    } catch (ClassNotFoundException | SQLException e) {
                        out.print(error(e.getMessage()));
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
