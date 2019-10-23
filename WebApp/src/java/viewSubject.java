
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class viewSubject extends HttpServlet {

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
                        request.getRequestDispatcher("side-faculty.jsp").include(request, response);
                        out.print("<style>tr:hover {"
                                + "background: #a8a3a3;"
                                + "}</style>");
                        out.print("<table class=\"table table-striped table-bordered\" style='text-align:center'><thead>");
                        out.print("<tr>");
                        out.print("<th>Subject Code</th>");
                        out.print("<th>Semester</th>");
                        out.print("<th>Subject Name</th>");
                        out.print("<th>Abbreviation</th>");
                        out.print("<th>Class</th>");
                        out.print("<th>Labs Conducted</th>");
                        out.print("</tr></thead><tbody>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("Select * from `subject` ORDER BY `sem` ASC");
                            while (rs.next()) {
                                out.print("<tr onclick=\"window.location='viewSubDetails?subcode=" + rs.getString(1) + "';\">");
                                for (int i = 1; i <= 5; i++) {
                                    if (i != 5) {
                                        out.print("<td>" + rs.getString(i) + "</td>");
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
                                        out.print("<td>" + div + "</td>");
                                    }

                                }
                                PreparedStatement ps = con.prepareStatement("SELECT count(facultytimetable.scheduleID)\n"
                                        + "from facultytimetable \n"
                                        + "INNER JOIN timetable\n"
                                        + "on timetable.scheduleID=facultytimetable.scheduleID\n"
                                        + "where timetable.subjectID = ?");
                                ps.setString(1, rs.getString(1));
                                ResultSet rs1 = ps.executeQuery();
                                while (rs1.next()) {
                                    out.print("<td>" + rs1.getString(1) + "</td>");
                                }
                                out.print("</tr>");
                            }
                            out.print("</tbody></table><br>");
                            con.close();
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();

                        }
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
