
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class viewLogs extends HttpServlet {

    private static final long serialVersionUID = -7374037765907987223L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    out.print(tablestart("Logs<div id='validations' style='color:red;font-size:14px;'>All Logs will be automatically be cleared at the end of each semester.</div>", "hover", "logs", 1));
                    String header = "<tr>";
                    header += "<th>Log ID</th>";
                    header += "<th>Date</th>";
                    header += "<th>Time</th>";
                    header += "<th>Comment</th>";
                    header += "</tr>";
                    out.print(tablehead(header));
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("Select logID, (select datedata.date from datedata where datedata.dateID = log.dateID) as Date,(select timedata.time from timedata where timedata.timeID = log.timeID)as Time, comments from `log` ORDER BY `date` and `time` DESC");
                        if (rs.next()) {
                            rs.previous();
                            while (rs.next()) {
                                out.print("<tr>");
                                for (int i = 1; i <= 4; i++) {
                                    out.print("<td>" + rs.getString(i) + "</td>");
                                }
                                out.print("</tr>");
                            }
                        } else {
                            out.print("<tr><td colspan=4>No Logs Found</td></tr>");
                        }
                        out.print(tableend(null, 1));
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        error(e.getMessage());
                    }
                    break;
                case 0:
                    out.print(kids());
                    break;
                default:
                    out.print(nouser());
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
