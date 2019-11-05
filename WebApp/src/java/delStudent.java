
import cerberus.Mailer;
import cerberus.messages;
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

public class delStudent extends HttpServlet {

    String name = "";
    String email = "";
    String body = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String prn = request.getParameter("prn");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                PreparedStatement ps = con.prepareStatement("Select * from `attendance` where `prn` = ?");
                ps.setString(1, prn);
                ResultSet rs = ps.executeQuery();
                int flag = 0;
                while (rs.next()) {
                    flag = 1;
                    break;
                }
                if (flag == 0) {
                    try {
                        ps = con.prepareStatement("Delete from `studentfingerprint` where `prn` = ?;");
                        ps.setString(1, prn);
                        ps.executeUpdate();
                        ps = con.prepareStatement("Delete from `rollcall` where `prn` = ?;");
                        ps.setString(1, prn);
                        ps.executeUpdate();
                        ps = con.prepareStatement("Delete from `studentsubject` where `prn` = ?;");
                        ps.setString(1, prn);
                        ps.executeUpdate();
                        ps = con.prepareStatement("Delete from `student` where `prn` = ?;");
                        ps.setString(1, prn);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        con.rollback();
                    }
                    con.close();
                    this.body = "Hey kiddo,\n    This mail is in response to a request to remove you as a Student at MSU-CA Department.\n\n"
                            + "Email/Username : " + this.email + "\n\n"
                            + "Note: You cannot login into the portal anymore 😎 \n\n"
                            + "This is an auto-generated e-mail, please do not reply.\n"
                            + "Regards\nCerberus Support Team";
                    Mailer mail = new Mailer();
                    mail.send(this.email, "Student Deletion", this.body);
                    messages a = new messages();
                    a.success(request, response, "The student was deleted successfully", "homepage");
                }
            } catch (ClassNotFoundException | SQLException e) {
                messages a = new messages();
                a.failed(request, response, "The Student cannot be deleted because student-log/student-fingerprint is dependent on it", "homepage");
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
