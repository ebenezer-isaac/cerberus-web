
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
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        out.println("<style>tr:hover {"
                                + "background: #a8a3a3;"
                                + "}</style>");
                        out.println("<table class=\"table table-striped table-bordered\" style='text-align:center'><thead>");
                        out.println("<tr>");
                        out.println("<th>Subject Code</th>");
                        out.println("<th>Semester</th>");
                        out.println("<th>Subject Name</th>");
                        out.println("<th>Abbreviation</th>");
                        out.println("<th>Class</th>");
                        out.println("<th>Labs Conducted</th>");
                        out.println("</tr></thead><tbody>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery("Select * from `subject` ORDER BY `sem` ASC");
                            while (rs.next()) {
                                out.println("<tr onclick=\"window.location='viewSubDetails?subcode=" + rs.getString(1) + "';\">");
                                for (int i = 1; i <= 5; i++) {
                                    if (i != 5) {
                                        out.println("<td>" + rs.getString(i) + "</td>");
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
                                        out.println("<td>" + div + "</td>");
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
                                    out.println("<td>" + rs1.getString(1) + "</td>");
                                }
                                out.println("</tr>");
                            }
                            out.println("</tbody></table><br>");
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
