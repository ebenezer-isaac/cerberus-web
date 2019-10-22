import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = 16177215)
public class editProfile extends HttpServlet {

    private static final int BUFFER_SIZE = 4096;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String name = request.getParameter("firstname");

            Part filePart = request.getPart("avatar-file");

            InputStream inputStream = null;
            
            out.println("<h1>Welcome : " + name + " Filepart : " + filePart + "</h1>");

            if (filePart != null) {
                System.out.println("IMAGE NAME : " + filePart.getName());
                System.out.println("IMAGE SIZE : " + filePart.getSize());
                System.out.println("IMAGE CONTENT TYPE : " + filePart.getContentType());
                inputStream = filePart.getInputStream();
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb?zeroDateTimeBehavior=convertToNull", "root", "");

                PreparedStatement ps1 = con.prepareStatement("INSERT INTO profile_pic VALUES(?,?)");
                ps1.setString(1, name);

                if (inputStream != null) {
                    ps1.setBlob(2, inputStream);
                }

                int row = ps1.executeUpdate();
                if (row > 0) {
                    out.println("<h1> File Inserted into database </h1>");
                }

                String path = "D:\\neww\\logo-circle.png";
                String sql2 = "SELECT photo FROM profile_pic WHERE id = ?";

                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, name);

                ResultSet rs = ps2.executeQuery();

                while (rs.next()) {
                    Blob blob = rs.getBlob("photo");

                    InputStream inputStream2 = blob.getBinaryStream();
                    OutputStream outputStream = new FileOutputStream(path);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while ((bytesRead = inputStream2.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    inputStream2.close();
                    outputStream.close();
                    con.close();
                    System.out.println("FILE SAVED");
                }

            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }


// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
/**
 * Handles the HTTP <code>GET</code> method.
 *
 * @param request servlet request
 * @param response servlet response
 * @throws ServletException if a servlet-specific error occurs
 * @throws IOException if an I/O error occurs
 */
@Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
        public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
