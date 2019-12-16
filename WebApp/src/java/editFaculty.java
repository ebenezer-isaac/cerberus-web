
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editFaculty extends HttpServlet {

    private static final long serialVersionUID = 1542329933061081122L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    out.print("<style>"
                            + "input[type=number]{"
                            + "width: 15px;"
                            + "}"
                            + "</style>");
                    String flow = "";
                    try {
                        flow = request.getParameter("flow");
                    } catch (Exception e) {
                        flow = "add";
                    }
                    if (flow.equals("add")) {
                        out.print("<style>"
                                + "input[type=number]{"
                                + "width: 40px;"
                                + "} "
                                + "</style>");
                        out.print("<br><br><div align='center'><form action='addFaculty' method='post'><table cellspacing='10'>"
                                + "<tr><td class=\"editSubjectStyle\">Faculty Name</td><td> : </td><td>"
                                + "<select name='title' class=\"editSelectFaculty\">"
                                + "<option value='Mr. '>Mr.</option>"
                                + "<option value='Ms. '>Ms.</option>"
                                + "<option value='Mrs. '>Mrs.</option>"
                                + "<option value='Dr. '>Dr.</option>"
                                + "<option value='Prof. '>Prof.</option>"
                                + "</select>"
                                + "<input type='text' name='name' class=\"editSubjectForm\" placeholder='First Name' minlength='5' maxlength='15'/></td></tr>"
                                + "<tr><td class=\"editSubjectStyle\">Faculty Email</td><td> : </td><td><input required type='email' name='email' id='email' onchange='checkValidations(0);' style='width: 303.3px' class=\"editSubjectForm\" placeholder='zuck@gmail.com'/></td><td><div id='disp1' class=\"tooltipp\" style='width:100px'><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr> "
                                + "</table><br><button style='width:200px;' required type='submit' class='btn btn-primary'>Add Faculty</button></form></div>");
                    } else if (flow.equals("del")) {
                        HttpSession session = request.getSession(false);
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
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            String sql = "SELECT `facultyID`,`name` from `faculty`;";
                            ResultSet rs = stmt.executeQuery(sql);
                            String select = "<select name = 'facultyID' class=\"editSelect\">";
                            while (rs.next()) {
                                if (session.getAttribute("user").equals(rs.getString(1))) {
                                } else {
                                    select += "<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(2) + "</option>";
                                }
                            }
                            select += "</select>";
                            out.print(select);
                            con.close();
                            out.print("<br><fieldset>"
                                    + "<legend><br> <font style=\"font-size: 20px;\"> Warning - The following changes will be made: </font> <br></legend>"
                                    + "<p> <font style=\"font-size: 15.5px;\"> 1. A mail will be sent to the respective faculty </font> </p>"
                                    + "<p> <font style=\"font-size: 15.5px;\"> 2. Fingerprint data will be deleted permanently </font> </p>"
                                    + "<p> <font style=\"font-size: 15.5px;\"> 3. Data of the No of Labs conducted will be deleted. </font> </p>"
                                    + "<br><input type='checkbox' id='warn'onclick='myFunction()'/> <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font>"
                                    + "<br></fieldset>");
                            out.print("<br><div id = 'butt' style='display:none;'><button type='submit' style='width:200px;' class='btn btn-primary'>Submit</button></div>");
                            out.print("</form></div></div>");
                        } catch (ClassNotFoundException | SQLException e) {
                            error(e.getMessage());
                        }
                    }
                    break;
                case 0:
                    out.print(kids());
                    break;
                default:
                    out.print(nouser());
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
