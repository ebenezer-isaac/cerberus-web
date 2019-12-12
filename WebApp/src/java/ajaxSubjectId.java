
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

public class ajaxSubjectId extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            if (access == 1 || access == 0) {
                int flag = 0;
                String subjectID = request.getParameter("subjectid").toUpperCase();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                        PreparedStatement ps = con.prepareStatement("select subjectID from subject where subjectID=?");
                        ps.setString(1, subjectID);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            flag = 1;
                        }
                        
                        
                        con.close();
                    }
                } catch (ClassNotFoundException | SQLException e) {
                }
                if (flag == 1) {
                    out.print("1");
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
