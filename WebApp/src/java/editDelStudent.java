
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class editDelStudent extends HttpServlet {

    private static final long serialVersionUID = -2195939141769356066L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    out.print("<form action='delStudent' method='post'>");
                    out.print("<br>Enter the PRN of the student: <br><br>");
                    out.print("<table><tr><td>PRN : <input required type='text' name='prn' id='prn' onkeyup='checkPRN();' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/></td><td><div id='disp4' class=\"tooltipp\" style='width:100px' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i><span class=\"tooltiptext\">No Student with that PRN found</span></div></td></tr>"
                            + "<tr><td class='text-center'><div id='validations' style='color:red;font-size:14px;'></div></td></tr></table>");
                    out.print("<fieldset>"
                            + "<legend><br>Warning :<br></legend>"
                            + "<p>1. Students with attendance records will not be deleted</p>"
                            + "<p>2. Subject selection of the student will be deleted.</p>"
                            + "<p>3. All fingerprint data of the student will be deleted permanently.</p>"
                            + "<br><table><tr><td><input type='checkbox' id='warn'onclick='myFunction()'/> <label for='warn'></label></td><td>&nbsp;&nbsp; <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font></td></tr></table>"
                            + "<br></fieldset>");
                    out.print("<br><div id = 'butt' style='display:none;' ><button class='btn btn-primary' style='width:200px' id='studbtn2' disabled type='submit'>Delete Student</button></div>");
                    out.print("</form>");
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
