
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

public class seleFile extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                out.print("<body onload='myFunction()'>");
                out.print("<script>"
                        + "function myFunction()"
                        + "{if (document.getElementById('warn').checked==true) "
                        + "{document.getElementById('file').style.display = 'block';}"
                        + "else"
                        + "{document.getElementById('file').style.display = 'none';}}"
                        + "</script>");
                out.print("<form action='uploadFile' method='post' enctype='multipart/form-data' >");
                out.print("<div align='center'><br>Select the year you want to upload data for : <br><br>");
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
                Statement stmt = con.createStatement();
                String sql = "SELECT `code`,`name` from `subjects`;";
                ResultSet rs = stmt.executeQuery(sql);
                out.print("<br><br><fieldset>"
                        + "<legend><br>Warning - The following changes will be made:<br></legend>"
                        + "<p>1. All Student records including roll number, name and subject selections belonging to the selected class will be deleted and new records will be uploaded in their stead.</p>"
                        + "<p>2. New records for the students will be uploaded in the respecitve subjects the student has opted for, if not already present</p>"
                        + "<p>3. The students whose records have been just added will have no attendance record whatsoever.</p>"
                        + "<p>4. New Subjects will not be added from the Excel file to eleminate redundancy issues. New subjects must be added from the Manage Subjects option in the main menu</p>"
                        + "<br><input type='checkbox' id='warn'onclick='myFunction()'/>I have read all the Warnings!"
                        + "<br><br></fieldset>");
                out.print("<br><div id = 'file' >"
                        + "Choose the Excel file of the class to upload:<br><br>"
                        + "<input type='file' name='file' /><br><br>"
                        + "<input type='submit' value='Upload' />"
                        + "</div>");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
