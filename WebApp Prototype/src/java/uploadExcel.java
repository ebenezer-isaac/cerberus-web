
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.read.biff.BiffException;

public class uploadExcel extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String division = request.getParameter("year");
            String path = request.getParameter("path");
            readExcel file = new readExcel(path, 2);
            int flag = 0;
            try {
                flag = file.read();
            } catch (IOException | BiffException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("message", e.getMessage());
                request.setAttribute("redirect", "menu");
                rd.forward(request, response);
            }
            if (flag == 1) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "uploadExcel");
                request.setAttribute("message", "File Data has been Uploaded Successfully");
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
