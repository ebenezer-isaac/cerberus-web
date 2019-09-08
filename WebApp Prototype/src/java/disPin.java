/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ebene
 */
public class disPin extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setIntHeader("Refresh", 5);
        try (PrintWriter out = response.getWriter()) {
            String pin = "";
            try {
                FileInputStream file1 = new FileInputStream("D:\\\\pin.txt");
                int line;
                while ((line = file1.read()) != -1) {
                    pin += ((char) line);
                }
            } catch (IOException e) {
                out.write(e.getMessage());
            }
            out.println("<title>");
            out.println("Arduino Pin");
            out.println("</title>");            
            out.println("<body align='center'>");
            out.println("<h1>Pin for Arduino Settings</h1>");
            out.println("<h3>"+pin+"</h3>");
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
