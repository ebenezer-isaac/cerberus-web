
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

public class saveTimetable extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            PreparedStatement ps = con.prepareStatement("Delete from timetable where weekID=(select weekID from week where week = ?)");
                            int week = (int) session.getAttribute("week");
                            ps.setInt(1, week);
                            ps.execute();
                            String batch[] = {"Batch A", "Batch B", "Batch C"};
                            int labid = Integer.parseInt(request.getParameter("lab"));
                            String subs[][] = new String[30][2];
                            int no_of_subs = 0;
                            int selesem = 1;
                            try {
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT `abbreviation`,`subjectID` from `subject` where `sem` in(" + selesem + "," + (selesem + 2) + "," + (selesem + 4) + ");");
                                while (rs.next()) {
                                    subs[no_of_subs][0] = rs.getString(1);
                                    subs[no_of_subs][1] = rs.getString(2);
                                    no_of_subs++;
                                }
                                no_of_subs--;
                            } catch (SQLException e) {
                                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                                request.setAttribute("redirect", "false");
                                request.setAttribute("head", "Database Error");
                                request.setAttribute("body", e.getMessage());
                                request.setAttribute("url", "viewTimetable");
                                rd.forward(request, response);

                            }
                            for (int i = 1; i <= 5; i++) {
                                for (int j = 1; j <= 6; j++) {
                                    if (request.getParameter("c" + i + j).equals("-")) {
                                    } else {
                                        PreparedStatement ps1 = con.prepareStatement("Insert into timetable(`slotID`, `labID`, `subjectID`, `batchID`, `weekID`, `dayID`) values(?,?,?,?,?,?)");
                                        int batchID = 0;
                                        int weekid = 0;
                                        PreparedStatement ps6 = con.prepareStatement("SELECT weekID FROM WEEK where week = ?");
                                        ps6.setInt(1, week);
                                        ResultSet rs = ps6.executeQuery();
                                        while (rs.next()) {
                                            weekid = rs.getInt(1);
                                        }
                                        for (int k = 0; k <= batch.length - 1; k++) {
                                            if (batch[k].equals(request.getParameter("batch" + i + j))) {
                                                batchID = k + 1;
                                            }
                                        }
                                        String subjectID = "";
                                        for (int k = 0; k <= subs.length && subs[k][0] != null; k++) {
                                            if (subs[k][0].equals(request.getParameter("c" + i + j))) {
                                                subjectID = subs[k][1];
                                            }
                                        }
                                        String dayOfWeek = "";
                                        switch (j) {
                                            case 1:
                                                dayOfWeek = "mon";
                                                break;
                                            case 2:
                                                dayOfWeek = "tue";
                                                break;
                                            case 3:
                                                dayOfWeek = "wed";
                                                break;
                                            case 4:
                                                dayOfWeek = "thu";
                                                break;
                                            case 5:
                                                dayOfWeek = "fri";
                                                break;
                                            case 6:
                                                dayOfWeek = "sat";
                                                break;
                                        }
                                        ps1.setInt(1, i);
                                        ps1.setInt(2, labid);
                                        ps1.setString(3, subjectID);
                                        ps1.setInt(4, batchID);
                                        ps1.setInt(5, weekid);
                                        ps1.setString(6, dayOfWeek);
                                        ps1.execute();
                                    }
                                }
                            }

                        } catch (IOException | ClassNotFoundException | NumberFormatException | SQLException | ServletException e) {
                            RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                            request.setAttribute("redirect", "false");
                            request.setAttribute("head", "Database Error");
                            request.setAttribute("body", e.getMessage());
                            request.setAttribute("url", "viewTimetable");
                            rd.forward(request, response);
                        }
                        RequestDispatcher rd = request.getRequestDispatcher("viewTimetable");
                        rd.forward(request, response);
                        break;
                    default:
                        RequestDispatcher rd1 = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Hey 'Kid'!");
                        request.setAttribute("body", "You are not authorized to view this page");
                        request.setAttribute("url", "homepage");
                        request.setAttribute("sec", "2");
                        rd1.forward(request, response);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
