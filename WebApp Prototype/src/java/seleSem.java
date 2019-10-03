
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class seleSem extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<body>"
                    + "<div align='center'>"
                    + "Select Semester: "
                    + "<br><br>"
                    + "<form action='disTimetable' method='post'>"
                    + "<input type='radio' name='lab' value='1' > Lab 1"
                    + "<br>"
                    + "<input type='radio' name='lab' value='2' > Lab 2"
                    + "<br>"
                    + "<input type='radio' name='lab' value='3' > Lab3"
                    + "<br><br>"
                    + "<input type='radio' name='SeleSem' value='1' > Odd Semester"
                    + "<br>"
                    + "<input type='radio' name='SeleSem' value='2' > Even Semester"
                    + "<br><br>"
                    + "<input type='submit' value='Submit'>"
                    + "</form>"
                    + "</div>"
                    + "</body>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
