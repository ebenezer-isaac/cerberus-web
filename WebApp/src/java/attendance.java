
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.no_of_batch;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.messages;
import static cerberus.printer.error;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
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

public class attendance extends HttpServlet {

    private static final long serialVersionUID = 7444273581035144960L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    int classID = Integer.parseInt(request.getParameter("class"));
                    int oddeve = oddEve();
                    String cla = getClassName(classID);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        int sem = getSem(oddeve, classID);
                        String[][] subs = semSubs(sem, classID);
                        for (int i = 0; i < subs.length; i++) {
                            System.out.println(subs[i][0]);
                            System.out.println(subs[i][1]);
                        }
                        int no_of_batch = no_of_batch();
                        int no_of_sub = subs.length - 1;
                        out.print(tablestart(cla, "hover", "studDetails", 0));
                        String header = "<th>Subject Code</th><th>Subject Abbr</th>";
                        String sql = "select ";
                        for (int x = 1; x <= no_of_batch; x++) {
                            sql += "count(CASE WHEN timetable.batchID = '" + x + "' THEN '' END) ";
                            header += "<th>Batch " + x + " </th>";
                            if (x != no_of_batch) {
                                sql += ", ";
                            }
                        }
                        sql += "from timetable \n"
                                + "inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID \n"
                                + "inner join subject on timetable.subjectID = subject.subjectID \n"
                                + "where subject.subjectID = ?";
                        out.print(tablehead(header));
                        for (int x = 0; x <= no_of_sub; x++) {
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setString(1, subs[x][0]);
                            ResultSet rs = ps.executeQuery();
                            out.println("<tr><td>" + subs[x][0] + "</td><td>" + subs[x][1] + "</td>");
                            while (rs.next()) {
                                for (int y = 1; y <= no_of_batch; y++) {
                                    int temp = rs.getInt(y);
                                    if (temp > 0) {
                                        out.print("<td><a href = \"javascript:setContent('/Cerberus/batSubAttendance?batchID=" + y + "&subjectID=" + subs[x][0] + "');\">" + temp + "</td>");
                                    } else {
                                        out.print("<td>" + temp + "</td>");
                                    }
                                }
                            }
                            out.print("</tr>");
                        }
                        out.print(tableend(null, 0));
                        out.print("<br><fieldset style='border-radius: 4px; border: solid 1px black;'><br><table width = 100%><tr><td style='vertical-align : middle;text-align:center;' width = 33% align='center'>Select Subject : </td>"
                                + "<td style='vertical-align : middle;text-align:center;' width = 33% align='center'>"
                                + "<select name = 'subject' id = 'subject' style='padding: 5px 109px 5px 8px; border-radius: 4px; border: none; background: #e6e6e6; outline: none; margin: 6px; font-size: 14.5px;' onchange=\"if(this.selectedIndex==0){document.getElementById('batch').disabled = true;}else{document.getElementById('batch').disabled = false;document.getElementById('batch').selectedIndex=1;document.getElementById('newFacTime-btn').disabled=false;}\"><option value= '0'>Select Subject</option>");
                        for (String sub[] : subs) {
                            out.print("<option value= '" + sub[0] + "'>" + sub[0] + " " + sub[1] + "</option>");
                        }
                        
                        out.print("</select>");
                        out.print("<select onchange = \"if(this.selectedIndex==0){document.getElementById('subject').selectedIndex=0; this.disabled= true;document.getElementById('newFacTime-btn').disabled=true;}\" class=\"editSelectTimeTable\" name = 'batch' id = 'batch' disabled  class='not-allowed';>"
                                + "<option name='-' value='-' selected >No Batch</option>");
                        PreparedStatement ps11 = con.prepareStatement("Select name from batch where batchid>0");
                        ResultSet rs3 = ps11.executeQuery();
                        int index = 1;
                        while (rs3.next()) {
                            out.print("<option value='" + index + "'>" + rs3.getString(1) + "</option>");
                            index++;
                        }
                        out.print("</select>");
                        out.print("</td><td style='vertical-align : middle;text-align:center;' width = 33% align='center'><button disabled onclick=\"var e = document.getElementById('subject');var b = document.getElementById('batch');setContent('/Cerberus/newFacultyTimetable?subjectid='+e.options[e.selectedIndex].value+'&batch='+e.selectedIndex);\" style='width:200px;' id='newFacTime-btn' class='btn btn-primary'>Edit Attendance</button></td></tr><tr><td></td><td></td><td><div id='validations' style='color:red;font-size:14px;'>*Or to Conduct New Lab Session</div></td></tr></table></fieldset><br>");
                        index = 0;
                        sql = "SELECT student.PRN, rollcall.rollNo,student.name,";
                        while (index <= no_of_sub) {
                            sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN studentsubject.batchID END) as '" + subs[index][1].replace("-", "_") + "'";
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
                        PreparedStatement ps = con.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery();
                        ResultSetMetaData rsm = rs.getMetaData();
                        int cols = rsm.getColumnCount();
                        if (rs.next()) {
                            out.print(tablestart("Student Average Attendance", "hover", "studDetails", 1));
                            header = "<tr>";
                            header += "<th> Roll </th>";
                            header += "<th> Name </th>";
                            for (int i = 4; i <= cols; i++) {
                                header += "<th> " + rsm.getColumnLabel(i) + " </th>";
                            }
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            while (rs.next()) {
                                String prn = rs.getString(1);
                                out.print("<tr>");
                                out.print("<td>" + rs.getString(2) + "</td>");
                                out.print("<td>" + rs.getString(3) + "</td>");
                                for (int i = 4; i <= cols; i++) {
                                    if (rs.getString(i).equals("0")) {
                                        out.print("<td>NA</td>");
                                    } else {
                                        out.print("<td><a href = \"javascript:setContent('/Cerberus/studSubAttendance?prn=" + prn + "&sub=" + rs.getString(i) + "');\" style='display:block;text-decoration:none;'>");
                                        out.print(String.format("%.02f", AttFunctions.calPercentage(prn, rs.getString(i))) + "%");
                                        out.print("</a></td>");
                                    }
                                }
                                out.print("</tr>");
                            }
                            out.print(tableend(null, 1));
                        } else {
                            out.print("<div align='center'>Student Data Unavailable</div>");
                        }
                        con.close();
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        error(e.getMessage());
                    }
                    break;
                case 0:
                    messages a = new messages();
                    a.kids(request, response);
                    break;
                default:
                    messages b = new messages();
                    b.nouser(request, response);
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
