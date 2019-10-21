
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

public class editFaculty extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession(true);
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:

                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        out.println("<style>\n"
                                + "input[type=number]{\n"
                                + "    width: 15px;\n"
                                + "} \n"
                                + "</style>");
                        String flow = "";
                        try {
                            flow = request.getParameter("flow");
                        } catch (Exception e) {
                            flow = "add";
                        }
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
                            out.print("<br><div align='center'><form action='addFaculty' method='post'><table cellspacing='10'>"
                                    + "<tr><td class=\"editSubjectStyle\">Faculty Name</td><td> : </td><td>"
                                    + "<select name='title' class=\"editSelectFaculty\">"
                                    + "<option value='Mr. '>Mr.</option>"
                                    + "<option value='Ms. '>Ms.</option>"
                                    + "<option value='Mrs. '>Mrs.</option>"
                                    + "</select>"
                                    + "<input type='text' name='name' class=\"editSubjectForm\" placeholder='Narendra Modi'/></td></tr>"
                                    + "<tr><td class=\"editSubjectStyle\">Faculty Email</td><td> : </td><td><input type='email' name='email' style='width: 303.3px' class=\"editSubjectForm\" placeholder='narendramodi@gmail.com'/></td></tr> "
                                    + "</table><br><button type='submit' class='btn btn-info'>Add Faculty</button></form>");
                        } else if (flow.equals("del")) {
                            try {
                                out.print("<body onload='myFunction()'>");
                                out.print("<script>"
                                        + "function myFunction()"
                                        + "{if (document.getElementById('warn').checked==true) "
                                        + "{document.getElementById('butt').style.display = 'block';}"
                                        + "else"
                                        + "{document.getElementById('butt').style.display = 'none';}}"
                                        + "</script>");
                                out.print("<form action='deltFaculty' method='post'>");
                                out.print("<div align='center'><br> <font style=\"font-size: 17px; color: red;\"> Select the Faculty you want to delete : </font> <br><br>");
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                                    Statement stmt = con.createStatement();
                                    String sql = "SELECT `facultyID`,`name` from `faculty`;";
                                    ResultSet rs = stmt.executeQuery(sql);
                                    String select = "<select name = 'facultyID' class=\"editSelect\">";
                                    while (rs.next()) {
                                        select += "<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(2) + "</option>";
                                    }
                                    select += "</select>";
                                    out.print(select);
                                    out.print("<br><fieldset>"
                                            + "<legend><br> <font style=\"font-size: 20px;\"> Warning - The following changes will be made: </font> <br></legend>"
                                            + "<p> <font style=\"font-size: 15.5px;\"> 1. A mail will be sent to the respective faculty </font> </p>"
                                            + "<p> <font style=\"font-size: 15.5px;\"> 2. Fingerprint data will be deleted permanently </font> </p>"
                                            + "<p> <font style=\"font-size: 15.5px;\"> 3. Data of the No of Labs conducted will be deleted. </font> </p>"
                                            + "<br><input type='checkbox' id='warn'onclick='myFunction()'/> <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font>"
                                            + "<br></fieldset>");
                                    out.print("<br><div id = 'butt' ><button type='submit' class='btn btn-info'>Submit</button></div>");
                                    out.print("</form></div>");
                                    con.close();
                                }
                            } catch (ClassNotFoundException | SQLException e) {
                                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                                request.setAttribute("message", e.getMessage());
                                request.setAttribute("redirect", "menu");
                                rd.forward(request, response);
                            }
                        }
                        request.getRequestDispatcher("end.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Hey 'Kid'!");
                        request.setAttribute("body", "You are not authorized to view this page");
                        request.setAttribute("url", "homepage");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);
                        break;
                }
            } catch (Exception e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Please login to continue");
                request.setAttribute("url", "index.html");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
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
