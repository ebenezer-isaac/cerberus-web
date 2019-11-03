
import cerberus.Mailer;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class deltFaculty extends HttpServlet {

    String name = "";
    String email = "";
    String body = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String facultyID = request.getParameter("facultyID");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                    PreparedStatement ps = con.prepareStatement("Select * from `faculty` where `facultyID` = ?;");
                    ps.setString(1, facultyID);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        this.name = rs.getString(2);
                        this.email = rs.getString(3);
                    }
                    PreparedStatement stmt = con.prepareStatement("Delete from `faculty` where `facultyID` = ?;");
                    stmt.setString(1, facultyID);
                    stmt.executeUpdate();
                    con.close();
                }
                this.body = "Hello " + this.name + ",\n    This mail is in response to a request to remove you as a faculty at MSU-CA Department.\n\n"
                        + "Email/Username : " + this.email + "\n\n"
                        + "Note: You cannot login into the portal anymore\n\n"
                        + "This is an auto-generated e-mail, please do not reply.\n"
                        + "Regards\nCerberus Support Team";
                Mailer mail = new Mailer();
                mail.send(this.email, "Account Deletion", this.body);
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Faculty Deleted");
                request.setAttribute("body", "The faculty was deleted successfully");
                request.setAttribute("url", "homepage");
                rd.forward(request, response);
            } catch (ClassNotFoundException | SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Request Failed");
                request.setAttribute("body", "The teacher cannot be deleted because teacher-log/teacher-fingerprint is dependent on it");
                request.setAttribute("url", "homepage");
                rd.forward(request, response);
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
