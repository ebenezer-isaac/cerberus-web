
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
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;

public class viewSubject extends HttpServlet {

    private static final long serialVersionUID = 3984071378259487483L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    out.print("<style>tr:hover {"
                            + "background: #a8a3a3;"
                            + "}</style>");
                    out.print(tablestart("Subjects", "hover", "studDetails", 0));
                    String header = "<tr>";
                    header += "<th>Subject Code</th>";
                    header += "<th>Semester</th>";
                    header += "<th>Subject Name</th>";
                    header += "<th>Abbreviation</th>";
                    header += "<th>Class</th>";
                    header += "</tr>";
                    out.print(tablehead(header));
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("Select subject.subjectID, subject.sem, subject.subject, subject.Abbreviation, (select class.class from class where subject.classID = class.classID) from `subject` ORDER BY `sem` ASC");
                        while (rs.next()) {
                            out.print("<tr onclick=\"javascript:setContent('/Cerberus/viewSubDetails?subcode=" + rs.getString(1) + "');\">");
                            for (int i = 1; i <= 5; i++) {
                                out.print("<td>" + rs.getString(i) + "</td>");
                            }
                            out.print("</tr>");
                        }
                        out.print(tableend(null,0));
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
