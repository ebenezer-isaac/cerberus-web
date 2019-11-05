
import cerberus.AttFunctions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editDelStudent extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession(true);
            int access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    out.print("<form action='delStudent' method='post'>");
                    out.print("<br>Enter the PRN of the student: <br><br>");
                    out.print("<table><tr><td>PRN : <input type='text' name='prn' id='prn' onkeyup='checkPRN();' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/></td><td><div id='disp4' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr></table>");
                    out.print("<br><br><fieldset>"
                            + "<legend><br>Warning - The following changes will be made:<br></legend>"
                            + "<p>1. All Attendance Records for the Student will be deleted.</p>"
                            + "<p>2. Subject Selection of all Students will be erased for this subject.</p>"
                            + "<p>3. Data of the No of Labs conducted will be deleted.</p>"
                            + "<br><input type='checkbox' id='warn'onclick='myFunction()'/>I have read all the Warnings!"
                            + "<br><br></fieldset>");
                    out.print("<br><div id = 'butt' style='display:none;' ><button id='studbtn2' disabled type='submit'>Submit</button></div>");
                    out.print("</form>");
                    
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
