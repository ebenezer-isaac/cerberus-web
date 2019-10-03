
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import jxl.read.biff.BiffException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@MultipartConfig
public class uploadFile extends HttpServlet {

    private final String UPLOAD_DIRECTORY = "D:";

    /*
int div(HttpServletRequest request) throws IOException, ServletException
{
    Part idPart = request.getPart("division");
            int division;
            System.out.println(idPart);
            Scanner sc = new Scanner(idPart.getInputStream());
            division = Integer.parseInt(sc.nextLine());
        return division;
}*/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String path = "";
        try (PrintWriter out = response.getWriter()) {
            if (ServletFileUpload.isMultipartContent(request)) {
                try {
                    List<FileItem> multiparts = new ServletFileUpload(
                            new DiskFileItemFactory()).parseRequest(request);
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            item.write(new File(UPLOAD_DIRECTORY + File.separator + name));
                            path = UPLOAD_DIRECTORY + File.separator + name;
                        }
                    }
                } catch (Exception ex) {
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("message", ex.getMessage());
                    request.setAttribute("redirect", "menu");
                    rd.forward(request, response);
                }
            }
            RequestDispatcher rd = request.getRequestDispatcher("seleClass");
            request.setAttribute("redirect", "uploadExcel");
            request.setAttribute("path", path);
            rd.forward(request, response);

        }
    }
}
