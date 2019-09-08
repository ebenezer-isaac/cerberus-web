
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class checkLogin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            int flag = 0;
            String un = request.getParameter("username");
            String pw = request.getParameter("password");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("select * from login where username=? and password=?");
                ps.setString(1, un);
                ps.setString(2, pw);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    flag = 1;
                }
                if (flag == 1) {
                    RequestDispatcher rd = request.getRequestDispatcher("menu");
                    rd.forward(request, response);
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("message", "Incorrect Username or Password");
                    request.setAttribute("redirect", "login.html");
                    rd.forward(request, response);
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                out.print(e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
