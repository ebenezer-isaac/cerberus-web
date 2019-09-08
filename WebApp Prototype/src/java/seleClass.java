
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class seleClass extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String redirect;
            String path = null;
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet seleclass</title>");
            redirect = request.getParameter("redirect");
            if (redirect == null) {
                redirect = (String) request.getAttribute("redirect");
                path = (String) request.getAttribute("path");
                out.print("<script>"
                        + "function myFunction(divi)"
                        + "{alert('You have selected '+divi);}"
                        + "</script>");
            }
            out.println("</head>");
            out.println("<body>");
            out.println("<div align='center'>");
            out.print("<form method='post' action='" + redirect + "' >");
            out.println("<input type='radio' name='year' value='FY' onclick='myFunction(this.value)'> FY");
            out.println("<input type='radio' name='year' value='SY' onclick='myFunction(this.value)'> SY");
            out.println("<input type='radio' name='year' value='TY' onclick='myFunction(this.value)'> TY");
            out.println("<input type='text' name='path' value='" + path + "' hidden > ");
            out.print("<br>");
            out.println("<button type='submit'>Submit</button></form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
