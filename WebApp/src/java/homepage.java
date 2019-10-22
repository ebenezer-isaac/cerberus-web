
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

public class homepage extends HttpServlet {

    int week;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            int access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    request.getRequestDispatcher("side-faculty.html").include(request, response);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        String sql2 = "SELECT name, photo FROM faculty WHERE facultyID = 1";
                        PreparedStatement ps2 = con.prepareStatement(sql2);
                        byte[] blob = null;
                        String name = "";
                        ResultSet rs = ps2.executeQuery();
                        while (rs.next()) {
                            blob = rs.getBytes("photo");
                            name = rs.getString("name");
                        }
                        con.close();
                        System.out.println("FILE SAVED");
                        String imgString = DatatypeConverter.printBase64Binary(blob);
                        out.print("<script>document.getElementById('pic').innerHTML=\"<a href='editProfilePage.html'><img src='data:image/png;base64," + imgString + "'/><br>"+name+"<br><br></a>\";</script>");
                    } catch (ClassNotFoundException | SQLException e) {
                        System.out.println(e);
                    }
                    //
                    //
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                case 0:
                    request.getRequestDispatcher("side-student.html").include(request, response);
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                default:
                    messages m = new messages();
                    m.nouser(request, response);
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
