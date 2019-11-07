
import cerberus.*;
import cerberus.AttFunctions;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class login extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String email = request.getParameter("email");
            String rawpass = request.getParameter("pwd");
            String id = "";
            if (AttFunctions.trimSQLInjection(rawpass).equals("'''='") || email.equals("admin") || rawpass.equals("admin")) {
                out.print("2");
            } else {
                String pass = AttFunctions.hashIt(rawpass);
                int access = 0;
                String corrpass = "";
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select prn,password from student where email=?");
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        id = rs.getString(1);
                        corrpass = rs.getString(2);
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                if (corrpass.equals("")) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps = con.prepareStatement("select facultyID, password from faculty where email=?");
                        ps.setString(1, email);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            id = rs.getString(1);
                            corrpass = rs.getString(2);
                        }
                        access = 1;
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (corrpass.equals(pass)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("email", email);
                    session.setAttribute("access", access);
                    System.out.println("access is "+access);
                    session.setAttribute("user", id);
                    session.setAttribute("pop", 0);
                    out.print("1");
                } else {
                    HttpSession session = request.getSession();
                    int trial = 1;
                    try {
                        trial = Integer.parseInt(session.getAttribute("count").toString());
                        trial++;
                    } catch (Exception e) {
                    }
                    session.setAttribute("count", ""+trial);
                    if (trial > 5) {
                        out.print("3");
                    } else {
                        out.print("0");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            /*messages m = new messages();
            m.error(request, response, e.getMessage(), "index.jsp");*/
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
