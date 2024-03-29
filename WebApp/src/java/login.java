
import static cerberus.AttFunctions.errorLogger;
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
            HttpSession session = request.getSession(true);
            int trial = 0;
            try {
                trial = Integer.parseInt(session.getAttribute("count").toString());
                trial++;
                session.setAttribute("count", trial);
            } catch (NumberFormatException | NullPointerException e) {
                session.setAttribute("count", 0);
            }
            if (trial > 5) {
                out.print("3");
            } else {
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
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
                            errorLogger(e.getMessage());
                        }
                    }
                    if (corrpass.equals(pass)) {
                        session.setAttribute("email", email);
                        session.setAttribute("access", access);
                        session.setAttribute("user", id);
                        session.setAttribute("name", name);
                        session.setAttribute("pop", 0);
                        out.print("1");
                    } else {
                        out.print("0");
                    }
                }
            }
        } catch (Exception e) {
            errorLogger(e.getMessage());
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
