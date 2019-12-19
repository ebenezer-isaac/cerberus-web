
import static cerberus.AttFunctions.getAccess;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
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

public class editSlot extends HttpServlet {

    private static final long serialVersionUID = 1298846673263982579L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    String flow;
                    try {
                        flow = request.getParameter("flow");
                    } catch (Exception e) {
                        flow = "edit";
                    }
                    switch (flow) {
                        case "edit":
                            out.print("<form action ='saveSlot' method='post'>");
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                                Statement stmt = con.createStatement();
                                String sql = "SELECT `slotID`,`startTime`,`endTime` from `slot` order by startTime,endTime ASC;";
                                ResultSet rs = stmt.executeQuery(sql);
                                out.print("<table cellpadding=5><tr align='center'><th >Start Time</th><th>&nbsp&nbsp&nbsp&nbsp</th><th>End Time</th></tr>");
                                while (rs.next()) {
                                    out.print("<tr><td ><input required type = 'time' style='background: #e6e6e6; font-size: 14.5px; padding: 3px 6px 3px 4px; border: none; border-radius: 4px;' name = 'stime" + rs.getString(1) + "' id = 'stime" + rs.getString(1) + "'  value='" + rs.getString(2).substring(0, 5) + "' step='60'></td> ");
                                    out.print("<td>&nbsp&nbsp&nbsp&nbsp</td><td><input required type = 'time' style='background: #e6e6e6;  font-size: 14.5px; padding: 3px 6px 3px 4px; border: none; border-radius: 4px;' name = 'etime" + rs.getString(1) + "' id = 'stime" + rs.getString(1) + "'  value='" + rs.getString(3).substring(0, 5) + "' step='60'></td></tr>");
                                }
                                out.print("</table><br><button type='submit' style='width: 200px;' class='btn btn-primary'>Save Timings</button>");
                                out.print("</form>");
                                con.close();
                            } catch (ClassNotFoundException | SQLException e) {
                                error(e.getMessage());
                            }
                            break;
                        case "add":
                            out.print("<br><br><form action = 'addSlot' method='post'>"
                                    + "Start Time : &nbsp<input required type = 'time' style='background: #e6e6e6; font-size: 14.5px; padding: 3px 6px 3px 4px; border: none; margin: 6px; border-radius: 4px;' name = 'stime' id = 'stime'  value='08:00' step='60'><br>"
                                    + "End Time : &nbsp<input required type = 'time' style='background: #e6e6e6; font-size: 14.5px; padding: 3px 6px 3px 4px; border: none; margin: 6px 0 12px 6px; border-radius: 4px;' name = 'etime' id = 'etime'  value='13:00' step='60'><br>"
                                    + "<button type = 'submit' style='width: 200px;' class='btn btn-primary'>Add Timings</button>"
                                    + "</form");
                            break;
                        case "del":
                            out.print("<script>"
                                    + "function myFunction()"
                                    + "{if (document.getElementById('warn').checked==true) "
                                    + "{document.getElementById('butt').style.display = 'block';}"
                                    + "else"
                                    + "{document.getElementById('butt').style.display = 'none';}}"
                                    + "</script>");
                            out.print("<form action='delSlot' method='post'>");
                            out.print("<br> <font style=\"font-size: 17px; color: red;\"> Select the Slot you want to delete : </font> <br><br>");
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                                Statement stmt = con.createStatement();
                                String sql = "SELECT `slotID`,`startTime`,`endTime` from `slot` order by startTime,endTime ASC;";
                                ResultSet rs = stmt.executeQuery(sql);
                                String select = "<select name = 'slotID' class=\"editSelect\">";
                                while (rs.next()) {
                                    select += "<option name='slot' value='" + rs.getString(1) + "'> " + rs.getString(2) + "-" + rs.getString(3) + "</option>";
                                }
                                select += "</select>";
                                out.print(select);
                                out.print("<br><fieldset>"
                                        + "<legend><br> <font style=\"font-size: 20px;\"> Warning : </font> <br></legend>"
                                        + "<p> <font style=\"font-size: 15.5px;\"> 1. The slot cannot be deleted if a lab session has been alloted for the same. </font> </p>"
                                        + "<br><table><tr><td><input type='checkbox' id='warn'onclick='myFunction()'/> <label for='warn'></label></td><td>&nbsp;&nbsp; <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font></td></tr></table>"
                                        + "<br></fieldset>");
                                out.print("<br><div id = 'butt' style='display:none;'><button type='submit' style='width: 200px;' class='btn btn-primary'>Delete Timings</button></div>");
                                out.print("</form><style type='text/css'>\n"
                                        + "@import url('css/checkbox.css');\n"
                                        + "</style>");
                            } catch (SQLException | ClassNotFoundException e) {
                                error(e.getMessage());
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case 0:
                    out.print(kids());
                    break;
                default:
                    out.print(nouser());
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
