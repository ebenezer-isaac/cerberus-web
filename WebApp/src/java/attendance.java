
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
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        int sem = getSem(oddeve, classID);
                        String[][] subs = semSubs(sem, classID);
                        int no_of_batch = no_of_batch();
                        int no_of_sub = subs.length - 1;
                        out.print(tablestart(cla, "hover", "studDetails", 0));
                        String header = "<th>Subject Code</th><th>Subject Abbr</th>";
                        PreparedStatement ps = con.prepareStatement("select * from batch where batchID>0");
                        ResultSet rs = ps.executeQuery();
                        String sql_subject = "select ";
                        while (rs.next()) {
                            sql_subject += "count(CASE WHEN timetable.batchID = '" + rs.getString(1) + "' THEN '' END) ";
                            header += "<th>" + rs.getString(2) + " </th>";
                            if (rs.getInt(1) != no_of_batch) {
                                sql_subject += ", ";
                            }
                        }
                        sql_subject += "from timetable \n"
                                + "inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID \n"
                                + "inner join subject on timetable.subjectID = subject.subjectID \n"
                                + "where subject.subjectID = ?";
                        out.print(tablehead(header));
                        for (int x = 0; x <= no_of_sub; x++) {
                            ps = con.prepareStatement(sql_subject);
                            ps.setString(1, subs[x][0]);
                            rs = ps.executeQuery();
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
                        out.print("<br><div class='row' style='border-radius: 4px; border: solid 1px black;'><div class='col-xl-4 col-sm-6' align='center'><br>Select Subject : </div>"
                                + "<div class='col-xl-4 col-sm-6' style='vertical-align : middle;text-align:center;' align='center'>"
                                + "<select name = 'subject' id = 'subject' style='width:200px; padding: 5px 5px 5px 5px; border-radius: 4px; border: none; background: #e6e6e6; outline: none; margin: 6px; font-size: 14.5px;' "
                                + "onchange=\"if(this.selectedIndex==0){document.getElementById('batch').disabled = true;}else{document.getElementById('batch').disabled = false;document.getElementById('batch').selectedIndex=1;document.getElementById('newFacTime-btn').disabled=false;}\"><option value= '0'>Select Subject</option>");
                        for (String sub[] : subs) {
                            out.print("<option value= '" + sub[0] + "'>" + sub[0] + " " + sub[1] + "</option>");
                        }
                        out.print("</select>");
                        out.print("<br><select style='width:200px; padding: 5px 5px 5px 5px; border-radius: 4px; border: none; background: #e6e6e6; outline: none; margin: 6px; font-size: 14.5px;' "
                                + "onchange = \"if(this.selectedIndex==0){document.getElementById('subject').selectedIndex=0; this.disabled= true;document.getElementById('newFacTime-btn').disabled=true;}\" class=\"editSelectTimeTable\" name = 'batch' id = 'batch' disabled  class='not-allowed';>"
                                + "<option name='-' value='-' selected >No Batch</option>");
                        PreparedStatement ps11 = con.prepareStatement("Select name from batch where batchid>0");
                        ResultSet rs3 = ps11.executeQuery();
                        int index = 1;
                        while (rs3.next()) {
                            out.print("<option value='" + index + "'>" + rs3.getString(1) + "</option>");
                            index++;
                        }
                        out.print("</select>");
                        out.print("</div><div class='col-xl-4 col-sm-6' style='vertical-align : middle;text-align:center;' align='center'>"
                                + "<br><button disabled onclick=\"var e = document.getElementById('subject');var b = document.getElementById('batch');setContent('/Cerberus/newFacultyTimetable?subjectid='+e.options[e.selectedIndex].value+'&batch='+b.selectedIndex);\" style='width:200px;' id='newFacTime-btn' class='btn btn-primary'>Edit Attendance</button>"
                                + "<br><div id='validations' style='color:red;font-size:14px;'>*Or to Conduct New Lab Session</div></div></div><br>");
                        index = 0;
                        String sql = "SELECT student.PRN, rollcall.rollNo,student.name,";
                        while (index <= no_of_sub) {
                            sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat(studentsubject.batchID,',',studentsubject.subjectID) END) as '" + subs[index][1].replace("-", "_") + "'";
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
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        ResultSetMetaData rsm = rs.getMetaData();
                        int cols = rsm.getColumnCount();
                        if (rs.next()) {
                            int line = 0;
                            out.print(tablestart("Student Average Attendance<br>Percentage Criteria : <input type='number' min='0' max='100'  class=\"editSelectTimeTable\" id = 'criteriaPerc' value='80' onkeyup='checkPerc(this.value)' onchange='checkPerc(this.value)'>", "hover", "studDetails", 1));
                            header = "<tr>";
                            header += "<th> Roll </th>";
                            header += "<th> Name </th>";
                            for (int i = 4; i <= cols; i++) {
                                header += "<th> " + rsm.getColumnLabel(i) + " </th>";
                            }
                            header += "<th> Average </th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            while (rs.next()) {
                                line++;
                                String prn = rs.getString(1);
                                out.print("<tr id='row" + line + "'>");
                                out.print("<td>" + rs.getString(2) + "</td>");
                                out.print("<td>" + rs.getString(3) + "</td>");
                                float total = 0;
                                float count = 0;
                                for (int i = 4; i <= cols; i++) {
                                    String result[] = rs.getString(i).split(",");
                                    ps = con.prepareStatement("select count(facultytimetable.scheduleID) from facultytimetable inner join timetable on facultytimetable.scheduleID = timetable.scheduleID where subjectID = ? and batchID = ?");
                                    ps.setString(1, result[1]);
                                    ps.setString(2, result[0]);
                                    ResultSet rs_subject = ps.executeQuery();
                                    int temp = 0;
                                    while (rs_subject.next()) {
                                        if (rs_subject.getInt(1) > 0) {
                                            temp = 1;
                                        }
                                    }
                                    if (result[0].equals("0")) {
                                        out.print("<td>N/A</td>");
                                    } else if (temp == 0) {
                                        out.print("<td>No Labs</td>");
                                    } else {
                                        count++;
                                        out.print("<td ><a href = \"javascript:setContent('/Cerberus/studSubAttendance?prn=" + prn + "&sub=" + result[1] + "');\" style='display:block;text-decoration:none;'>");
                                        float currPerc = AttFunctions.calPercentage(prn, result[1], result[0]);
                                        total = total + currPerc;
                                        out.print(String.format("%.02f", total) + "%");
                                        out.print("</a></td>");
                                    }
                                }
                                float perc = 0;
                                if (count > 0) {
                                    perc = total / count;
                                }
                                out.print("<td id='perc" + line + "'>" + String.format("%.02f", perc) + "%" + "</td></tr>");
                            }
                            out.print(tableend(null, 1));
                            out.print("<script>var line =" + line + ";"
                                    + "function checkPerc(criteria){"
                                    + "if(criteria>100){document.getElementById('criteriaPerc').value=100;checkPerc(100);}else{"
                                    + "for(var i=1;i<=line;i++){var value = document.getElementById('perc'+i).innerHTML;"
                                    + "value = value.substring(0, value.length - 1);"
                                    + "value = parseFloat(value);"
                                    + "if(value<criteria){"
                                    + "document.getElementById('row'+i).classList.add('table-danger');document.getElementById('row'+i).classList.remove('table-success');"
                                    + "}else{"
                                    + "document.getElementById('row'+i).classList.remove('table-danger');document.getElementById('row'+i).classList.add('table-success');}"
                                    + "}"
                                    + "}}checkPerc(80);"
                                    + "</script>");
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
