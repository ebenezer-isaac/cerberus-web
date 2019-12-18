
import static cerberus.AttFunctions.getAccess;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ajaxCheckPRN extends HttpServlet {

    private static final long serialVersionUID = -1922384727783540241L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            if (access == 1) {
                String prn = request.getParameter("prn");
                if (Pattern.matches("^20\\d{14}$", prn)) {
                    int flag = 0;
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        try (Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123")) {
                            PreparedStatement ps = con.prepareStatement("select prn from student where prn=?");
                            ps.setString(1, prn);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                flag = 1;
                            }
                            con.close();
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                    }
                    if (flag == 0) {
                        out.print("1");
                    } else {
                        out.print("2");
                    }
                } else {
                    out.print("0");
                }
            } else {
                messages b = new messages();
                b.unauthaccess(request, response);
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

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
