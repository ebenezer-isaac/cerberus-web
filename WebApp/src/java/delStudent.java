
import static cerberus.AttFunctions.currUserName;
import static cerberus.AttFunctions.dbLog;
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import cerberus.Mailer;
import cerberus.messages;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class delStudent extends HttpServlet {

    private static final long serialVersionUID = 3530925199821402303L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                String prn = request.getParameter("prn");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("Select * from `attendance` where `prn` = ?");
                    ps.setString(1, prn);
                    ResultSet rs = ps.executeQuery();
                    int flag = 0;
                    while (rs.next()) {
                        flag = 1;
                        break;
                    }
                    if (flag == 0) {
                        String email = "";
                        try {
                            ps = con.prepareStatement("Select email from `student` where `prn` = ?");
                            ps.setString(1, prn);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                email = rs.getString(1);
                            }
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
                        String body = "Hey Kid,\n    This mail is in response to a request to remove your login access at MSU-CA Department Attendance portal\n\n"
                                + "Email/Username : " + email + "\n\n"
                                + "Note: You cannot login into the portal anymore\n\n"
                                + "This is an auto-generated e-mail, please do not reply.\n"
                                + "Regards\nCerberus Support Team";
                        Mailer mail = new Mailer();
                        mail.send(email, "Student Deletion", body);
                        dbLog(currUserName(request) + " deleted student with prn : " + prn);
                        messages a = new messages();
                        a.success(request, response, "The student was deleted successfully", "homepage");
                    } else {
                        messages a = new messages();
                        a.failed(request, response, "The student cannot be deleted because attendance is associated with him/her", "homepage");
                    }
                } catch (InterruptedException | ClassNotFoundException | SQLException e) {
                    errorLogger(e.getMessage());
                    messages a = new messages();
                    a.dberror(request, response, e.getMessage(), "homepage");
                }
                break;
            case 0:
                messages a = new messages();
                a.kids(request, response);
                break;
            default:
                messages b = new messages();
                b.nouser(request, response);
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
