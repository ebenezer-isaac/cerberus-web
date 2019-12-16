
import static cerberus.AttFunctions.get_schedule_det;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class newFacultyTimetable extends HttpServlet {

    private static final long serialVersionUID = -3963568346101198223L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                int scheduleID = Integer.parseInt(request.getParameter("scheduleid"));
                String schedule[] = get_schedule_det(scheduleID);
                out.print("<fieldset>");
                out.print("<table align='center' width = 30%><tr><td align='center' width = 14%><b>Date</b></td><td align='center' width = 2%> : </td><td align='center' width = 14%>" + schedule[0] + "</td></tr>");
                out.print("<tr><td align='center'><b>Start Time</b></td><td align='center'> : </td><td align='center'>" + schedule[1] + "</td></tr>");
                out.print("<tr><td align='center'><b>End Time</b></td><td align='center'> : </td><td align='center'>" + schedule[2] + "</td></tr>");
                out.print("<tr><td align='center'><b>Lab</b></td><td align='center'> : </td><td align='center'>" + schedule[3] + "</td></tr>");
                out.print("<tr><td align='center'><b>Subject ID</b></td><td align='center'> : </td><td align='center'>" + schedule[4] + "</td></tr>");
                out.print("<tr><td align='center'><b>Subject</b></td><td align='center'> : </td><td align='center'>" + schedule[5] + "</td></tr></table>"
                        + "<br><font style=\"font-size: 20px;\"><b> Warning - The following changes will be made:</b></font><br><br>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 1. The selected Lab will be marked as conducted. </font> </p>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 2. Any attendance marked via the fingerprint system will be void. </font> </p>"
                        + "<p> <font style=\"font-size: 15.5px;\"> 3. Attendance marked by will be overwritten with express authority. </font> </p>"
                        + "<br><input type='checkbox' id='warn'onclick='myFunction()'/> <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font>"
                        + "<br></fieldset>");
                out.print("<br><div id = 'butt' style='display:none;'><form action='saveNewAttendance' method='post'>"
                        + "<input name='scheduleid' type='text' value='" + scheduleID + "' hidden>"
                        + "<button type='submit' style='width:200px;' class='btn btn-primary'>Submit</button></form></div>");
            } catch (NumberFormatException e) {
                int subjectflag = 0;
                int batchflag = 0;
                String subjectid = request.getParameter("subjectid");
                int batch = 0;
                try {
                    batch = Integer.parseInt(request.getParameter("batch"));
                } catch (NumberFormatException d) {
                    batchflag = 1;
                }
                if (subjectid == null) {
                    subjectflag = 1;
                }
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    Statement stmt = con.createStatement();
                    String sql = "SELECT `subjectID`,`abbreviation` from `subject`;";
                    ResultSet rs = stmt.executeQuery(sql);
                    out.print("<select id = 'subject'  onclick='checkVal()' class=\"editSelect\">");
                    out.print("<option");
                    if (subjectflag == 1) {
                        out.print(" selected");
                    }
                    out.print(">Select Subject</option>");
                    while (rs.next()) {
                        out.print("<option name='Sub' value='" + rs.getString(1) + "'");
                        if (rs.getString(1).equals(subjectid)) {
                            out.print(" selected ");
                        }
                        out.print("> " + rs.getString(1) + " - " + rs.getString(2) + " </option>");
                    }
                    out.print("</select><br><select id = 'batch' onclick='checkVal()' class=\"editSelect\">");
                    sql = "SELECT * from `batch` where batchId>0;";
                    out.print("<option");
                    if (batchflag == 1) {
                        out.print(" selected");
                    }
                    out.print(">Select Batch</option>");
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        out.print("<option name='Sub' value='" + rs.getString(1) + "'");
                        if (rs.getInt(1) == batch) {
                            out.print(" selected ");
                        }
                        out.print("> " + rs.getString(2) + " </option>");
                    }
                    out.print("</select><br><br>");
                    out.print("<input type='button' id='poper' onclick=\"getSchedules();\" style='width:200px;' class='btn btn-primary'");
                    if (subjectflag == 1 || batchflag == 1) {
                        out.print(" disabled ");
                    }
                    out.print("value = 'Populate Labs'>");

                    out.print("<br><br><div id='tableSchedule'></div>");
                    out.print("<script>function checkVal(){"
                            + "var sub = document.getElementById('subject').selectedIndex;"
                            + "var bat = document.getElementById('batch').selectedIndex;"
                            + "if(sub==0||bat==0){"
                            + "document.getElementById('poper').disabled=true;}else{document.getElementById('poper').disabled=false;}"
                            + "}");
                    if (subjectflag == 0 && batchflag == 0) {
                        out.print("getSchedules();");
                    }
                    out.print("function getSchedules(){console.log('function call');"
                            + "var subobj = document.getElementById('subject'); var sub = subobj.options[subobj.selectedIndex].value;"
                            + "var bat = document.getElementById('batch').selectedIndex;"
                            + "    if (window.XMLHttpRequest) {\n"
                            + "        request = new XMLHttpRequest();\n"
                            + "    } else if (window.ActiveXObject) {\n"
                            + "        request = new ActiveXObject(\"Microsoft.XMLHTTP\");\n"
                            + "    }\n"
                            + "    try {\n"
                            + "        request.onreadystatechange = getInfoSchedules;\n"
                            + "        request.open(\"GET\", \"/Cerberus/ajaxSchedules?sub=\"+sub+\"&bat=\"+bat, true);\n"
                            + "        request.send();\n"
                            + "    } catch (e) {\n"
                            + "        alert(\"Unable to connect to server\");\n"
                            + "    }\n"
                            + "\n"
                            + "}\n"
                            + "\n"
                            + "function getInfoSchedules() {\nconsole.log('got the value');"
                            + "    if (request.readyState == 4) {\n"
                            + "        var val = request.responseText;\n"
                            + "        document.getElementById('tableSchedule').innerHTML=val;"
                            + "    }\n"
                            + "}"
                            + "");
                    out.print("</script>");

                } catch (ClassNotFoundException | SQLException x) {
                }
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
