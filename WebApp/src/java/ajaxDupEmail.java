
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

public class ajaxDupEmail extends HttpServlet {

    private static final long serialVersionUID = -7648313536110146153L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            if (access == 1) {
                String email = request.getParameter("email");
                String prn = request.getParameter("prn");
                if (Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                    int flag = 0;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                            PreparedStatement ps = con.prepareStatement("select prn,email from student where email=?");
                            ps.setString(1, email);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                if (rs.getString(1).equals(prn)) {
                                } else {
                                    flag = 1;
                                }
                            }
                            if (flag == 0) {
                                ps = con.prepareStatement("select email from faculty where email=?");
                                ps.setString(1, email);
                                rs = ps.executeQuery();
                                while (rs.next()) {
                                    flag = 1;
                                }
                            }
                            con.close();
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                    }
                    if (flag == 0) {
                        out.print("1");
                    } else {
                        out.print("0");
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
