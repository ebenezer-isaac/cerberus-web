
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class homepage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("nav.html").include(request, response);
            System.out.println("teacher");
            HttpSession session = request.getSession(true);
            System.out.println("teacher1");
            int access = (int) session.getAttribute("access");
            if (access == 1) {
                out.print("Faculty Panel </li>"
                        + "<li> <a href=\"#\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Timetable Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-list\"></i> &nbsp;  Subjects Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-list-alt\"></i> &nbsp;  Student Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-folder-open\"></i> &nbsp;  Attendance Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-user-cog\"></i> &nbsp; Admin Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-upload\"></i> &nbsp; Student Progression </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Device OTP </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Profile </a> </li>\n"
                        + "</ul></div>");
                /*out.print("<div class=\"page-content-wrapper\">\n"
                        + "<button class=\"btn btn-link\" id=\"menu-toggle\" style=\"background-color: #0d0d0d;\"> <i class=\"fas fa-align-justify\" style=\"color: white;\"></i> </button>\n"
                        + "<div class=\"row\">"
                        + "<div class=\"col-md-12\">\n"
                        + "<div class=\"container my-5\" style=\"padding: 0 70px;\">\n"
                        + "");*/
                String[][] slots = null;
                int no_of_slots = 0;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select * from slot order by startTime");
                    ResultSet rs = ps.executeQuery();
                    while(rs.next())
                    {
                        slots[no_of_slots][0]=rs.getString(1);
                        slots[no_of_slots][1]=rs.getString(2);
                        slots[no_of_slots][2]=rs.getString(3);
                        no_of_slots++;
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "true");
                    request.setAttribute("head", "Database Error");
                    request.setAttribute("body", e.getMessage());
                    request.setAttribute("url", "index.html");
                    request.setAttribute("sec", "2");
                    rd.forward(request, response);
                }
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("w");
                int weekID=Integer.parseInt(ft.format(date));
                String[][] timetable = new String[8][no_of_slots];
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    PreparedStatement ps = con.prepareStatement("select * from timetable where weekID=?");
                    ps.setInt(1,weekID);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next())
                    {
                        no_of_slots++;
                    }
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "true");
                    request.setAttribute("head", "Database Error");
                    request.setAttribute("body", e.getMessage());
                    request.setAttribute("url", "index.html");
                    request.setAttribute("sec", "2");
                    rd.forward(request, response);
                }
                
            } else {
                out.print("Student Panel </li>"
                        + "<li> <a href=\"#\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Attendance</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-list\"></i> &nbsp; Profile </a> </li>\n"
                        + "</ul></div>");
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
