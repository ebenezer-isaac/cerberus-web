
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newFacultyTimetable extends HttpServlet {

    private static final long serialVersionUID = -3963568346101198223L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String subjectid = request.getParameter("subjectid");
            if (subjectid != null) {
                out.print("This page will display list of labs conducted / will be conducted today.");
                out.print(subjectid);
            } else {
                out.print("Dropdown to select class and then drop down to select the subject.<br>");
                out.print("Then this page will display list of labs conducted / will be conducted today.");
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
}
