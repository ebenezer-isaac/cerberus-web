
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class studentProgression extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("Thank You for showing your interest"
                    + "<br><br>"
                    + "<b>This section of the website is still under construction.<br>"
                    + "You will be notified when this page is available."
                    + "<br><br></b>"
                    + "This section of out website will have the following features :<br>"
                    + "<ul style=' display:table; margin:0 auto;'>"
                    + "<li>Transfer students to next year (FY to SY, SY to TY)</li>"
                    + "<li>Manage KT Students</li>"
                    + "<li>Change the number of labs, batches</li>"
                    + "<li>Add or Remove classes (FY,SY,TY,MSc.IT)</li>"
                    + "<li>Take Backup of all Data</li>"
                    + "<li>Upload Data of Students from Excel File</li>"
                    + "</ul><br>We would love to hear your suggestions<br>Please contact us at:<br>+91 89807 77667<br>+91 98791 92084");
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
