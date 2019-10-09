
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class dispSubject extends HttpServlet {

    int week;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        week = (int) session.getAttribute("week");
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        String table = "";
                        table += ("<table class=\"table table-striped table-bordered\" style='text-align:center'><thead>");
                        table += ("<tr>");
                        table += ("<th>Subject Code</th>");
                        table += ("<th>Semester</th>");
                        table += ("<th>Subject Name</th>");
                        table += ("<th>Abbreviation</th><th>Class</th></tr></thead><tbody>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("Select * from `subject` ORDER BY `sem` ASC");
                            while (rs.next()) {
                                table += ("</tr>");
                                for (int i = 1; i <= 5; i++) {
                                    if (i != 5) {
                                        table += ("<td>" + rs.getString(i) + "</td>");
                                    } else {
                                        int sem = rs.getInt(i);
                                        String div = "";
                                        switch (sem) {
                                            case 1:
                                                div = "FY";
                                                break;
                                            case 2:
                                                div = "SY";
                                                break;
                                            case 3:
                                                div = "TY";
                                                break;
                                        }
                                        table += ("<td>" + div + "</td>");
                                    }

                                }
                                table += ("</tr>");
                            }
                            table += ("</tbody></table><br>");
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            table = e.getMessage();

                        }
                        out.println(table);
                        out.print("<form action='editSubject' method='post' align='center'>");
                        out.print("<input id='349' type='radio' name='flow' value='add'>"
                                + "<label for=\"349\">Add Subject &nbsp </label>");
                        out.print("<input id='350' type='radio' name='flow' value='delete'>"
                                + "<label for=\"350\">  Delete  Subject </label><br><br>");
                        out.print("<input type='submit' value='Submit' onClick='/'>");
                        out.print("</form>");
                        request.getRequestDispatcher("end.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Hey 'Kid'!");
                        request.setAttribute("body", "You are not authorized to view this page");
                        request.setAttribute("url", "homepage");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);
                        break;
                }
            } catch (IOException | ServletException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Please login to continue");
                request.setAttribute("url", "index.html");
                request.setAttribute("sec", "2");
                rd.forward(request, response);

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
