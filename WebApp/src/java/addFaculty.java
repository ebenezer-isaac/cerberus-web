
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addFaculty extends HttpServlet {

    String name = "";
    String email = "";
    String rawpass = "";
    String body = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            this.name = request.getParameter("title") + request.getParameter("name");
            this.email = request.getParameter("email");
            String pass = "";
            int index = this.email.indexOf("@");
            if (index != -1) {
                pass = this.email.substring(0, index);
            }
            this.rawpass = pass;
            pass = AttFunctions.hashIt(pass);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                    PreparedStatement ps = con.prepareStatement("select name from faculty where email=?");
                    ps.setString(1, this.email);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Request Failed");
                        request.setAttribute("body", "A faculty by name " + rs.getString(1) + " with email address as " + this.email + " already exits.");
                        request.setAttribute("url", "homepage");
                        rd.forward(request, response);
                    } else {
                        ps = con.prepareStatement("INSERT INTO `faculty`(`name`, `email`, `password`) VALUES (?,?,?)");
                        ps.setString(1, this.name);
                        ps.setString(2, this.email);
                        ps.setString(3, pass);
                        ps.executeUpdate();
                        this.body = "Hello " + this.name + ",\n    This mail is in response to a request to add you as a faculty at MSU-CA Department.\n\n"
                                + "Email/Username : " + this.email + "\n"
                                + "One Time Password : " + this.rawpass + "\n\n"
                                + "You can now login with given username and password at at CA Department's Intranet WebSite\n"
                                + "and wield admin privileges to manage timetable, students' details and attendance amoung other things through this portal.\n\n"
                                + "Note: You will be prompted to change password on first login.\n\n"
                                + "This is an auto-generated e-mail, please do not reply.\n"
                                + "Regards\nCerberus Support Team";
                        Mailer mail = new Mailer();
                        mail.send(this.email, "Account Registration", this.body);
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "false");
                        request.setAttribute("head", "Faculty Added");
                        request.setAttribute("body", this.name + " was added to the list of faculties. A mail has been sent to their registered email address.");
                        request.setAttribute("url", "homepage");
                        rd.forward(request, response);
                    }
                    con.close();
                }
            } catch (SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Database Error");
                request.setAttribute("body", e.getMessage());
                request.setAttribute("url", "homepage");
                rd.forward(request, response);

            }
        } catch (Exception e) {
            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "false");
            request.setAttribute("head", "Error");
            request.setAttribute("body", e.getMessage());
            request.setAttribute("url", "index.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
