
import cerberus.AttFunctions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class attendance extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            int access = (int) session.getAttribute("access");
            switch (access) {
                case 1:
                    int classID = Integer.parseInt(request.getParameter("class"));                 
                    try {
                        int oddeve = 0;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
                        st.setInt(1, Integer.parseInt(session.getAttribute("week").toString()));
                        ResultSet rs2 = st.executeQuery();
                        while (rs2.next()) {
                            oddeve = (rs2.getInt(1) % 2);
                        }
                        String cla = "";
                        PreparedStatement ps1 = con.prepareStatement("Select class from class where classID=?");
                        ps1.setInt(1, classID);
                        ResultSet rs5 = ps1.executeQuery();
                        while (rs5.next()) {
                            cla = rs5.getString(1);
                        }
                        out.print("<div align='center'>" + cla + "</div><br>");
                        int sem = AttFunctions.getSem(oddeve, classID);
                        System.out.println("sem:"+sem);
                        PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                        ps3.setInt(1, sem);
                        ResultSet rs3 = ps3.executeQuery();
                        int no_of_sub = 0;
                        while (rs3.next()) {
                            no_of_sub++;
                        }
                        rs3 = ps3.executeQuery();
                        String[][] subs = new String[no_of_sub][2];
                        no_of_sub--;
                        int index = 0;
                        while (rs3.next()) {

                            subs[index][0] = rs3.getString(1);
                            subs[index][1] = rs3.getString(2);
                            index++;
                        }
                        int no_of_batch = 0;
                        PreparedStatement ps4 = con.prepareStatement("select count(batch.batchID) from batch");
                        ResultSet rs4 = ps4.executeQuery();
                        while (rs4.next()) {
                            no_of_batch = rs4.getInt(1);
                        }
                        out.println("<table class='table table-striped table-bordered'><thead><th>Subject Code</th><th>Subject Abbr</th>");
                        String sql = "select ";
                        for (int x = 1; x <= no_of_batch; x++) {
                            sql += "count(CASE WHEN timetable.batchID = '" + x + "' THEN '' END) ";
                            out.println("<th>Batch " + x + " </th>");
                            if (x != no_of_batch) {
                                sql += ", ";
                            }
                        }
                        sql += "from timetable \n"
                                + "inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID \n"
                                + "inner join subject on timetable.subjectID = subject.subjectID \n"
                                + "where subject.subjectID = ?";
                        out.println("</thead><tbody>");
                        for (int x = 0; x <= no_of_sub; x++) {
                            ps4 = con.prepareStatement(sql);
                            ps4.setString(1, subs[x][0]);
                            rs4 = ps4.executeQuery();
                            out.println("<tr><td>" + subs[x][0] + "</td><td>" + subs[x][1] + "</td>");
                            while (rs4.next()) {
                                for (int y = 1; y <= no_of_batch; y++) {
                                    out.print("<td>" + rs4.getInt(y) + "</td>");
                                }
                            }
                            out.print("</tr>");
                        }
                        out.print("</tbody></table>");

                        index = 0;
                        sql = "SELECT student.PRN, rollcall.rollNo,student.name,";
                        while (index <= no_of_sub) {
                            sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat(' " + subs[index][0] + "',' ') END) as '" + subs[index][1].replace("-", "_")+"'";
                            if (index <= (no_of_sub - 1)) {
                                sql += ", ";
                            }
                            index++;
                        }
                        sql += " FROM student "
                                + "INNER JOIN studentsubject "
                                + "ON student.PRN = studentsubject.PRN "
                                + "INNER JOIN rollcall "
                                + " on  rollcall.PRN = student.PRN "
                                + "where student.PRN in (select rollcall.PRN from rollcall where rollcall.classID = " + classID + ") "
                                + "GROUP BY studentsubject.PRN "
                                + "ORDER by rollcall.rollNo";
                        System.out.println(sql);
                        ps4 = con.prepareStatement(sql);
                        ResultSet rs = ps4.executeQuery();
                        ResultSetMetaData rsm = rs.getMetaData();
                        int cols = rsm.getColumnCount();
                        int line = 0;
                        if (rs.next()) {
                            out.print("<table class='table table-striped table-bordered'><thead>");
                            out.print("<tr>");
                            out.print("<th> Roll </th>");
                            out.print("<th> Name </th>");
                            for (int i = 4; i <= cols; i++) {
                                out.print("<th> " + rsm.getColumnLabel(i) + " </th>");
                            }
                            out.print("</tr>");
                            rs.previous();

                            while (rs.next()) {
                                String prn = rs.getString(1);
                                line++;
                                out.print("<tr>");
                                out.print("<td>" + rs.getString(2) + "</td>");
                                out.print("<td>" + rs.getString(3) + "</td>");
                                for (int i = 4; i <= cols; i++) {
                                    if (rs.getString(i) != null) {
                                        out.print("<td><a href = '/Cerberus/studSubAttendance?prn=" + prn + "&sub=" + rs.getString(i) + "' style='display:block;text-decoration:none;'>");
                                        out.print(String.format("%.02f", AttFunctions.calpercentage(prn, rs.getString(i))) + "%");
                                        out.print("</a></td>");
                                    } else {
                                        out.print("<td>NA</td>");
                                    }
                                }
                                out.print("</tr>");
                            }
                            out.print("</table><br><br>");
                            rs = ps4.executeQuery();

                        } else {
                            out.print("<div align='center'>Student Data Unavailable</div>");
                        }
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        e.printStackTrace();
                    }
                    break;
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
