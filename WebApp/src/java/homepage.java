
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import numbertowords.EnglishNumberToWords;

public class homepage extends HttpServlet {

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
                    request.getRequestDispatcher("side-faculty.jsp").include(request, response);
                    out.println("hello");
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                case 0:
                    request.getRequestDispatcher("side-student.jsp").include(request, response);
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
                        }
                        if (index == 1) {
                            out.print("No Subjects Available");
                        }
                    } catch (Exception e) {
                    }
                    request.getRequestDispatcher("end.html").include(request, response);
                    break;
                default:
                    messages m = new messages();
                    m.nouser(request, response);
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
