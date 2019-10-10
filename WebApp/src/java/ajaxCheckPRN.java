
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ajaxCheckPRN extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String prn = request.getParameter("prn");
            int flag = 0;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("select prn from student where prn=?");
                ps.setString(1, prn);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    flag = 1;
                }
            } catch (ClassNotFoundException | SQLException e) {
            }
            System.out.println(prn.length());
            if (Pattern.matches("^20\\d{14}$", prn)) {
                if (flag == 0) {
                    out.println("<i class=\"fa fa-check\" aria-hidden=\"true\"></i>");
                } else {
                    out.println("<i class=\"fa fa-user\" aria-hidden=\"true\"></i>Student Exists");
                }
            } else {
                out.println("<i class=\"fa fa-times\" aria-hidden=\"true\"></i>");
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
