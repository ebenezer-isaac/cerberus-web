
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class checkPin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String corrpin = "";
            try {
                FileInputStream file1 = new FileInputStream("D:\\\\pin.txt");
                int line;
                while ((line = file1.read()) != -1) {
                    corrpin += ((char) line);
                }
            } catch (IOException e) {
                out.write(e.getMessage());
            }
            String password = request.getParameter("password");
            String pin = request.getParameter("pin");
            if (password.equals("1234abcd") && pin.equals(corrpin)) {
                try {
                    Random rand = new Random();
                    corrpin = String.format("%04d", rand.nextInt(10000));
                    FileOutputStream file = new FileOutputStream("D:\\\\pin.txt");
                    file.write(corrpin.getBytes());
                } catch (IOException e) {
                    out.write(e.getMessage());
                }
                out.write("0");
            } else {
                out.write("1");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
