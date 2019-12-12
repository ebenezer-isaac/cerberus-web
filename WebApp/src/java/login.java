
import cerberus.AttFunctions;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;

public class login extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String email = request.getParameter("email").toLowerCase();
            String rawpass = request.getParameter("pwd");
            String id = "";
            String name = "";
            if (AttFunctions.trimSQLInjection(rawpass).equals("'''='") || rawpass.equals("admin")) {
                out.print("2");
            } else {
                String pass = AttFunctions.hashIt(rawpass);
                int access = 0;
                String corrpass = "";
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select prn,password,name from student where email=?");
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        id = rs.getString(1);
                        corrpass = rs.getString(2);
                        name = rs.getString(3);
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                }
                if (corrpass.equals("")) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps = con.prepareStatement("select facultyID,password,name from faculty where email=?");
                        ps.setString(1, email);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            id = rs.getString(1);
                            corrpass = rs.getString(2);
                            name = rs.getString(3);
                        }
                        access = 1;
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                    }
                }
                if (corrpass.equals(pass)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("email", email);
                    session.setAttribute("access", access);
                    session.setAttribute("user", id);
                    session.setAttribute("name", name);
                    session.setAttribute("pop", 0);
                    out.print("1");
                } else {
                    HttpSession session = request.getSession();
                    int trial = 1;
                    try {
                        trial = Integer.parseInt(session.getAttribute("count").toString());
                        trial++;
                    } catch (NumberFormatException e) {
                         session.setAttribute("count", 0);
                    }
                    session.setAttribute("count", "" + trial);
                    if (trial > 5) {
                        out.print("3");
                    } else {
                        out.print("0");
                    }
                }
            }
        } catch (Exception e) {
            messages m = new messages();
            m.error(request, response, e.getMessage(), "index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        messages m = new messages();
        m.unauthaccess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
