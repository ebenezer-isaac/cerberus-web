
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class dispSubject extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("nav.html").include(request, response);
            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            if (access == 1) {

                out.print("Faculty Panel </li>"
                        + "<li> <a href=\"/Cerberus/editTimetable\"> <i class=\"far fa-calendar-alt\"></i> &nbsp; Timetable Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-list\"></i> &nbsp;  Subjects Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-list-alt\"></i> &nbsp;  Student Management</a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"far fa-folder-open\"></i> &nbsp;  Attendance Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-user-cog\"></i> &nbsp; Admin Management </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-upload\"></i> &nbsp; Student Progression </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Device OTP </a> </li>\n"
                        + "<li> <a href=\"#\"> <i class=\"fas fa-braille\"></i> &nbsp; Profile </a> </li>\n"
                        + "</ul></div>");
                out.print("<div class=\"page-content-wrapper\">\n"
                        + "<button class=\"btn btn-link\" id=\"menu-toggle\" style=\"background-color: #0d0d0d;\"> <i class=\"fas fa-align-justify\" style=\"color: white;\"></i> </button>\n"
                        + "<div class=\"row\">"
                        + "<div class=\"col-md-12\">\n"
                        + "<div class=\"container my-5\" style=\"padding: 0 70px;\">\n"
                        + "");
                out.println("<style>\n"
                        + "input[type=number]{\n"
                        + "    width: 35px;\n"
                        + "} \n"
                        + "</style>");

                out.print("<table cellpadding='5' border ='1' align = 'center'>");
                out.print("<tr>");
                out.print("<th>Subject Code</th>");
                out.print("<th>Semester</th>");
                out.print("<th>Subject Name</th>");
                out.print("<th>Class</th></tr>");
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("Select * from `subject`  ORDER BY `subjectID` ASC");
                    while (rs.next()) {
                        out.print("</tr>");
                        for (int i = 1; i <= 4; i++) {
                            if (i != 4) {
                                out.print("<td>" + rs.getString(i) + "</td>");
                            } else {
                                int sem = rs.getInt(i);
                                String div = "";
                                switch (sem) {
                                    case 1:
                                        div = "FY";
                                        break;
                                    case 2:
                                        div = "SY";
                                        break;
                                    case 3:
                                        div = "TY";
                                        break;
                                }
                                out.print("<td>" + div + "</td>");
                            }

                        }
                        out.print("</tr>");
                    }
                    out.print("</table><br>");
                    out.print("<form action='editSubject' method='post' align='center'>");
                    out.print("<input type='radio' name='flow' value='add'> Add Subject<br>");
                    out.print("<input type='radio' name='flow' value='delete'> Delete  Subject<br><br>");
                    out.print("<input type='submit' value='Submit'>");
                    out.print("</form>");
                    con.close();
                } catch (ClassNotFoundException | SQLException e) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("message", e.getMessage());
                    request.setAttribute("redirect", "menu");
                    rd.forward(request, response);
                }
                out.println("<form action='menu' method='post' align='center'>"
                        + "<input type='submit' value='Back'>"
                        + "</form>");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
