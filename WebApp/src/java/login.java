
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class login extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String email = request.getParameter("email");
            String rawpass = request.getParameter("password");
            String id = "";
            if (AttFunctions.trimSQLInjection(rawpass).equals("'''='")) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Nice Try!");
                request.setAttribute("body", "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F)));
                request.setAttribute("url", "index.html");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
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
                    messages m = new messages();
                    m.dberror(request, response, e.getMessage(), "index.html");
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
                        messages m = new messages();
                        m.dberror(request, response, e.getMessage(), "index.html");
                    }
                }
                if (corrpass.equals(pass)) {
                    RequestDispatcher rd = request.getRequestDispatcher("homepage");
                    HttpSession session = request.getSession();
                    java.util.Date date = new java.util.Date();
                    SimpleDateFormat ft = new SimpleDateFormat("w");
                    int week = Integer.parseInt(ft.format(date));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    System.out.print(calendar.get(Calendar.DAY_OF_WEEK));
                    if (calendar.get(Calendar.DAY_OF_WEEK) <= 2) {
                        week--;
                    }
                    session.setAttribute("week", week);
                    AttFunctions.new_week(week);
                    AttFunctions.new_week(week + 1);
                    session.setAttribute("email", email);
                    session.setAttribute("access", access);
                    session.setAttribute("user", id);
                    rd.forward(request, response);

                } else {
                    messages m = new messages();
                    m.wrongpass(request, response);
                }
            }
        } catch (Exception e) {
            messages m = new messages();
            m.error(request, response, e.getMessage(), "index.html");
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
