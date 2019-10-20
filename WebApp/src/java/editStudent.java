
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

public class editStudent extends HttpServlet {

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
                        out.println("<style>"
                                + "input[type=number]{"
                                + "width: 15px;"
                                + "} "
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
                                    + "var request;"
                                    + "var id;"
                                    + "function sendInfo(x)"
                                    + "{ "
                                    + "id=x;"
                                    + "if(x==0){"
                                    + "v = document.getElementById('email').value;"
                                    + "var url = \"ajaxCheckEmail?email=\" + v;}"
                                    + "else if (x==1){"
                                    + "v = document.getElementById('prn').value;"
                                    + "var url = \"ajaxCheckPRN?prn=\" + v;}"
                                    + "else if (x==2){"
                                    + "v = document.getElementById('roll').value;"
                                    + "cl = document.getElementById('clas').selectedIndex;"
                                    + "var url = \"ajaxCheckRoll?roll=\" + v+\"&clas=\"+cl;}"
                                    + "if (window.XMLHttpRequest) {"
                                    + "request = new XMLHttpRequest();"
                                    + "} else if (window.ActiveXObject) {"
                                    + "request = new ActiveXObject(\"Microsoft.XMLHTTP\");"
                                    + "}"
                                    + "try"
                                    + "{"
                                    + "request.onreadystatechange = getInfo;"
                                    + "request.open(\"GET\", url, true);"
                                    + "request.send();"
                                    + "} catch (e)"
                                    + "{"
                                    + "alert(\"Unable to connect to server\");"
                                    + "}"
                                    + "}"
                                    + "function getInfo() {"
                                    + "if (request.readyState == 4) {"
                                    + "var val = request.responseText;"
                                    + "if(id==0)"
                                    + "{document.getElementById('disp1').innerHTML = val;}"
                                    + "else if (id==1)"
                                    + "{document.getElementById('disp2').innerHTML = val;}"
                                    + "else if (id==2)"
                                    + "{document.getElementById('disp3').innerHTML = val;}"
                                    + "}"
                                    + "}"
                                    + "</script>");

                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                out.print("<br><div align='center'><form action='addStudent' method='post'><table cellspacing='10'>"
                                        + "<tr><td class=\"editSubjectStyle\">Student Class</td><td> : </td><td>");
                                Statement stmt = con.createStatement();
                                out.print("<select name = 'clas' id = 'clas' class=\"editSelect\">");
                                ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                                int index = 0;
                                while (rs.next()) {
                                    index++;
                                    out.print("<option name='Sub' value= '" + index + "'>" + rs.getString(1) + "</option>");
                                }
                                out.println("</select>");
                                out.print("</td></tr><tr><td class=\"editSubjectStyle\">Student Name</td><td> : </td><td><input type='text' name='name' class=\"editSubjectForm\" placeholder='Mark Zuckerberg'/></td></tr>"
                                        + "<tr><td class=\"editSubjectStyle\">Roll No</td><td> : </td><td><input type='number' name='roll' id='roll' class=\"editSubjectForm\" style= 'width: 216px' onchange='this.value = zeroPad(this.value);sendInfo(2);' value = '01' placeholder='xx' min='1' max='150'/><td><div id='disp3' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></td></tr> "
                                        + "<tr><td class=\"editSubjectStyle\">PRN</td><td> : </td><td><input type='TEXT' name='prn' id='prn' onkeyup='sendInfo(1)' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/><td><div id='disp2' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></td></tr> "
                                        + "<tr><td class=\"editSubjectStyle\">Student Email</td><td> : </td><td><input type='email' id='email' name='email' onkeyup='sendInfo(0)' class=\"editSubjectForm\" placeholder='zuck@gmail.com' /></td><td><div id='disp1' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr> "
                                        + "</table><br><button type='submit' class='btn btn-info'>Add Student</button></form>"
                                );
                            } catch (SQLException e) {
                                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                                request.setAttribute("message", e.getMessage());
                                request.setAttribute("redirect", "menu");
                                rd.forward(request, response);
                            }
                        } else if (flow.equals("del")) {
//                            try {
//                                out.print("<body onload='myFunction()'>");
//                                out.print("<script>"
//                                        + "function myFunction()"
//                                        + "{if (document.getElementById('warn').checked==true) "
//                                        + "{document.getElementById('butt').style.display = 'block';}"
//                                        + "else"
//                                        + "{document.getElementById('butt').style.display = 'none';}}"
//                                        + "</script>");
//                                out.print("<form action='deltFaculty' method='post'>");
//                                out.print("<div align='center'><br>Select the subject you want to delete : <br><br>");
//                                Class.forName("com.mysql.cj.jdbc.Driver");
//                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
//                                Statement stmt = con.createStatement();
//                                String sql = "SELECT `facultyID`,`name` from `faculty`;";
//                                ResultSet rs = stmt.executeQuery(sql);
//                                String select = "<select name = 'facultyID'>";
//                                while (rs.next()) {
//                                    select += "<option name='Sub' value='" + rs.getString(1) + "'> " + rs.getString(2) + "</option>";
//                                }
//                                select += "</select>";
//                                out.print(select);
//                                out.print("<br><br><fieldset>"
//                                        + "<legend><br>Warning - The following changes will be made:<br></legend>"
//                                        + "<p>1. All Attendance Records for the Subject will be deleted.</p>"
//                                        + "<p>2. Subject Selection of all Students will be erased for this subject.</p>"
//                                        + "<p>3. Data of the No of Labs conducted will be deleted.</p>"
//                                        + "<br><input type='checkbox' id='warn'onclick='myFunction()'/>I have read all the Warnings!"
//                                        + "<br><br></fieldset>");
//                                out.print("<br><div id = 'butt' ><button type='submit'>Submit</button></div>");
//                                out.print("</form></div>");
//                                con.close();
//
//                            } catch (ClassNotFoundException | SQLException e) {
//                                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
//                                request.setAttribute("message", e.getMessage());
//                                request.setAttribute("redirect", "menu");
//                                rd.forward(request, response);
//                            }
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
            } catch (IOException | ClassNotFoundException | ServletException e) {
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
