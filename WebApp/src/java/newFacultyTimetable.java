
import static cerberus.AttFunctions.get_schedule_det;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class newFacultyTimetable extends HttpServlet {

    private static final long serialVersionUID = -3963568346101198223L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("inside facultytimetable");
        try (PrintWriter out = response.getWriter()) {

            try {
                HttpSession session = request.getSession(false);
                System.out.println(session.getAttribute("user"));
                int currentFaculty = Integer.parseInt(session.getAttribute("user").toString().trim());
                int facultyID = 0;
                String facultyName = "";
                int scheduleID = Integer.parseInt(request.getParameter("scheduleid"));
                System.out.println(scheduleID);
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    PreparedStatement ps = con.prepareStatement("select facultyID, (select faculty.name from faculty where faculty.facultyID = facultytimetable.facultyID) from facultyTimetable where scheduleID = ?");
                    ps.setInt(1, scheduleID);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        facultyID = rs.getInt(1);
                        facultyName = rs.getString(2);
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("currentFaculty : " + currentFaculty);
                System.out.println("facultyID : " + facultyID);
                if (facultyID != 0) {
                    if (currentFaculty == facultyID) {
                        out.print("<font style=\"font-size: 20px;\">You have already marked this Lab Session as conducted."
                                + "<br>Redirecting you the the Edit Attendance page for this Lab Session</font>"
                                + "<form action='saveNewAttendance' method='post'>"
                                + "<input name='scheduleid' type='text' value='" + scheduleID + "' hidden>"
                                + "<button type='submit' style='width:200px;' class='btn btn-primary'>Redirect</button></form>"
                        );

                    } else {
                        out.print("<font style='font-size: 20px;'>" + facultyName + " already marked this Lab Session as conducted.<br>Redirecting you the the Edit Attendance page for this Lab Session</font>"
                                + "<form action='saveNewAttendance' method='post'>"
                                + "<input name='scheduleid' type='text' value='" + scheduleID + "' hidden>"
                                + "<button type='submit' style='width:200px;' class='btn btn-primary'>Redirect</button></form>");
                    }
                } else {
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
                            + "<p> <font style=\"font-size: 15.5px;\"> 2. Any attendance marked via the fingerprint system meanwhile, will be void. </font> </p>"
                            + "<p> <font style=\"font-size: 15.5px;\"> 3. Attendance marked by you will be overwritten with express authority. </font> </p>"
                            + "<br><table><tr><td><input type='checkbox' id='warn' onclick='myFunction()'/> <label for='warn'></label></td><td>&nbsp;&nbsp; <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font></td></tr></table>"
                            + "<br></fieldset><style type='text/css'>\n"
                            + "@import url('css/checkbox.css');\n"
                            + "</style>");
                    out.print("<br><div id = 'butt' style='display:none;'><form action='saveNewAttendance' method='post'>"
                            + "<input name='scheduleid' type='text' value='" + scheduleID + "' hidden>"
                            + "<button type='submit' style='width:200px;' class='btn btn-primary'>Submit</button></form></div>");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
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
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    Statement stmt = con.createStatement();
                    String sql = "SELECT `subjectID`,`abbreviation` from `subject`;";
                    ResultSet rs = stmt.executeQuery(sql);
                    out.print("<select id = 'subject'  onclick='checkVal()' style='width:200px;padding: 5px 5px 5px 5px;border-radius: 4px;border: none;background-color: #e6e6e6;outline: none;margin: 6px;font-size: 14.5px;'>");
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
                    out.print("</select><br><select id = 'batch' onclick='checkVal()' style='width:200px;padding: 5px 5px 5px 5px;border-radius: 4px;border: none;background-color: #e6e6e6;outline: none;margin: 6px;font-size: 14.5px;'>");
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
