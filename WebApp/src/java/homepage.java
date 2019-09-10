
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("w");
                System.out.println("Current Week: " + ft.format(date));

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
