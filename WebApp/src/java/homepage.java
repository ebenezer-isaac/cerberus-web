
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
import javax.servlet.http.HttpSession;

public class homepage extends HttpServlet {

    int week;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            new_week();
            HttpSession session = request.getSession();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        request.getRequestDispatcher("end.html").include(request, response);
                        break;
                    case 0:
                        request.getRequestDispatcher("side-student.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Security Firewall");
                        request.setAttribute("body", "Please login to continue");
                        request.setAttribute("url", "index.html");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);

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

    public void new_week() {
        int weekid = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {
                PreparedStatement ps6 = con.prepareStatement("SELECT weekID FROM WEEK where week = ?");
                ps6.setInt(1, week);
                ResultSet rs = ps6.executeQuery();
                while (rs.next()) {
                    weekid = rs.getInt(1);
                }
                if (weekid == 0) {
                    PreparedStatement ps2 = con.prepareStatement("insert into week(`week`) values(?)");
                    ps2.setInt(1, week);
                    ps2.executeUpdate();
                }
                rs = ps6.executeQuery();
                while (rs.next()) {
                    weekid = rs.getInt(1);
                }
                int labcount = 0;
                PreparedStatement ps8 = con.prepareStatement("SELECT count(labID) FROM lab");
                ps6.setInt(1, week);
                rs = ps8.executeQuery();
                while (rs.next()) {
                    labcount = rs.getInt(1);
                }
                for (int i = 1; i <= labcount; i++) {
                    PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                    ps5.setInt(1, weekid);
                    ps5.setInt(2, i);
                    rs = ps5.executeQuery();
                    int flag = 0;
                    while (rs.next()) {
                        flag = 1;
                        break;
                    }
                    if (flag == 0) {
                        PreparedStatement ps10 = con.prepareStatement("SELECT weekID FROM `week` ORDER BY `week`.`weekID` DESC");
                        rs = ps10.executeQuery();
                        while (rs.next()) {
                            PreparedStatement ps9 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                            ps9.setInt(1, rs.getInt(1));
                            ps9.setInt(2, i);
                            ResultSet rs1 = ps9.executeQuery();
                            flag = 0;
                            while (rs1.next() && flag == 0) {
                                flag = 1;
                            }
                            PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ? and labID=?");
                            ps3.setInt(1, weekid);
                            ps3.setInt(2, rs.getInt(1));
                            ps3.setInt(3, i);
                            ps3.executeUpdate();
                        }
                    }
                }
                con.close();
            }

        } catch (ClassNotFoundException | SQLException e) {
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
