
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

public class editSubject extends HttpServlet {

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

                String flow = request.getParameter("flow");
                if (flow.equals("add")) {
                    out.println("<style>"
                            + "input[type=number]{"
                            + "width: 40px;"
                            + "} "
                            + "</style>");
                    out.print("<script>"
                            + "function zeroPad(num)"
                            + "{"
                            + "var s = num+'';"
                            + "while (s.length < 2) s = '0' + s;"
                            + "return(s);"
                            + "}"
                            + "function myFuntion()"
                            + "{alert('hola');}"
                            + "</script>");
                    out.print("<br><div align='center'><form action='addSubject' method='post'><table cellspacing='7'>"
                            + "<tr><td>Subject Code</td><td> : </td><td><input type='text' name='code'/></td></tr>"
                            + "<tr><td>Subject Name</td><td> : </td><td><input type='text' name='name'/></td></tr>"
                            + "<tr><td>Semester</td><td> : </td><td><input type='number' min = '1' max = '5' value = '01' onchange='this.value = zeroPad(this.value)' name='sem'/></td></tr>"
                            + "<tr><td>No of Labs Conducted</td><td> : </td><td><input type='number' min = '0' max = '30' value = '00' onchange='this.value = zeroPad(this.value)' name='labs'/></td></tr>"
                            + "</table><br><button type='submit'>Submit</button>"
                            + "</form>");
                } else {
                    try {
                        out.print("<body onload='myFunction()'>");
                        out.print("<script>"
                                + "function myFunction()"
                                + "{if (document.getElementById('warn').checked==true) "
                                + "{document.getElementById('butt').style.display = 'block';}"
                                + "else"
                                + "{document.getElementById('butt').style.display = 'none';}}"
                                + "</script>");
                        out.print("<form action='delSubject' method='post'>");
                        out.print("<div align='center'><br>Select the subject you want to delete : <br><br>");
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        Statement stmt = con.createStatement();
                        String sql = "SELECT `subjectID`,`subject` from `subject`;";
                        ResultSet rs = stmt.executeQuery(sql);
                        String select = "<select name = 'subject'>";
                        while (rs.next()) {
                            select += "<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(1) + " - " + rs.getString(2) + " </option>";
                        }
                        select += "</select>";
                        out.print(select);
                        out.print("<br><br><fieldset>"
                                + "<legend><br>Warning - The following changes will be made:<br></legend>"
                                + "<p>1. All Attendance Records for the Subject will be deleted.</p>"
                                + "<p>2. Subject Selection of all Students will be erased for this subject.</p>"
                                + "<p>3. Data of the No of Labs conducted will be deleted.</p>"
                                + "<br><input type='checkbox' id='warn'onclick='myFunction()'/>I have read all the Warnings!"
                                + "<br><br></fieldset>");
                        out.print("<br><div id = 'butt' ><button type='submit'>Submit</button></div>");
                        out.print("</form></div>");
                        con.close();

                    } catch (ClassNotFoundException | SQLException e) {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("message", e.getMessage());
                        request.setAttribute("redirect", "menu");
                        rd.forward(request, response);
                    }
                }
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
