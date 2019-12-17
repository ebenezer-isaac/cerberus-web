
import static cerberus.AttFunctions.get_next_schedule;
import static cerberus.AttFunctions.no_of_labs;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static cerberus.printer.nouser;
import java.util.Date;

public class homepage extends HttpServlet {

    private static final long serialVersionUID = -6020013234525993016L;

    int week;
    String[] subs;
    int access;
    HttpServletResponse response;
    HttpServletRequest request;

    protected void processRequest(HttpServletRequest reques, HttpServletResponse respons)
            throws ServletException, IOException {
        response = respons;
        request = reques;
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    out.print("<div class='row''>");
                    int labs = no_of_labs();
                    for (int i = 1; i <= labs; i++) {
                        String testing[] = get_next_schedule(request, i);
                        switch (testing[0]) {
                            case "0":
                                out.print("<div class='col mb-3' align='center'>"
                                        + "<div class='card text-white bg-danger o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class=\"fas fa-times\"></i>"
                                        + "</div>"
                                        + "<div class='mr-5'><br>No Labs Today<br><br></div>"
                                        + "</div>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "1":
                                out.print("<div class='col mb-3' align='center'>"
                                        + "<div class='card text-white bg-primary o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-desktop'></i>"
                                        + "</div>"
                                        + "<div class='mr-5'><br>All Labs are Over<br><br></div>"
                                        + "</div>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "2":
                                out.print("<div class='col mb-3' align='center'>"
                                        + "<div class='card text-white bg-success o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-check'></i>"
                                        + "</div>"
                                        + "<div class='mr-5'>" + testing[1].split(",")[0] + "</div>"
                                        + "</div>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/rapidAttendance?scheduleid=" + testing[1].split(",")[1] + "');\">"
                                        + "<span class='float-left'>Edit Attendance</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;
                            case "3":
                                out.print("<div class='col mb-3' align='center'>"
                                        + "<div class='card text-white bg-warning o-hidden h-100'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Lab " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class='fas fa-forward'></i>"
                                        + "</div>"
                                        + "<div class='mr-5'>" + testing[1] + "</div>"
                                        + "</div>"
                                        + "<a class='card-footer text-white clearfix small z-1' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + i + "');\">"
                                        + "<span class='float-left'>Edit Timetable</span>"
                                        + "<span class='float-right'>"
                                        + "<i class='fas fa-angle-right'></i>"
                                        + "</span>"
                                        + "</a>"
                                        + "</div>"
                                        + "</div>");
                                break;

                        }
                    }
                    out.print("</div>");
                    break;
                case 0:
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps1 = con.prepareStatement("select subject.subjectID, subject.Abbreviation, (select name from batch where batchID = studentsubject.batchID) from studentsubject "
                                + "inner join subject "
                                + "on subject.subjectID=studentsubject.subjectID "
                                + "where PRN = ?");
                        ps1.setString(1, session.getAttribute("user").toString());
                        ResultSet rs = ps1.executeQuery();
                        int index = 1;
                        while (rs.next()) {
                            out.print(rs.getString(1) + " " + rs.getString(2) + "<br>");
                            index++;
                        }
                        if (index == 1) {
                            out.print("No Subjects Selected");
                        }
                    } catch (Exception e) {
                    }
                    break;
                default:
                    nouser();
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
