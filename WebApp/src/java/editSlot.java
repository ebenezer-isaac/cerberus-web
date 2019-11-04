
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cerberus.*;

public class editSlot extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String flow = "";
            try {
                flow = request.getParameter("flow");
            } catch (Exception e) {
                flow = "edit";
            }
            if (flow.equals("edit")) {

            } else if (flow.equals("add")) {
            } else if (flow.equals("del")) {
                out.print("<script>"
                        + "function myFunction()"
                        + "{if (document.getElementById('warn').checked==true) "
                        + "{document.getElementById('butt').style.display = 'block';}"
                        + "else"
                        + "{document.getElementById('butt').style.display = 'none';}}"
                        + "</script>");
                out.print("<form action='delSlot' method='post'>");
                out.print("<div align='center'><br> <font style=\"font-size: 17px; color: red;\"> Select the Slot you want to delete : </font> <br><br>");
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                    Statement stmt = con.createStatement();
                    String sql = "SELECT `slotID`,`startTime`,`endTime` from `slot`;";
                    ResultSet rs = stmt.executeQuery(sql);
                    String select = "<select name = 'slotID' class=\"editSelect\">";
                    while (rs.next()) {
                        select += "<option name='slot' value='" + rs.getString(1) + "'> " + rs.getString(2) + "-" + rs.getString(3) + "</option>";
                    }
                    select += "</select>";
                    out.print(select);
                    out.print("<br><fieldset>"
                            + "<legend><br> <font style=\"font-size: 20px;\"> Warning - The following changes will be made: </font> <br></legend>"
                            + "<p> <font style=\"font-size: 15.5px;\"> 1. A mail will be sent to the respective faculty </font> </p>"
                            + "<p> <font style=\"font-size: 15.5px;\"> 2. Fingerprint data will be deleted permanently </font> </p>"
                            + "<p> <font style=\"font-size: 15.5px;\"> 3. Data of the No of Labs conducted will be deleted. </font> </p>"
                            + "<br><input type='checkbox' id='warn'onclick='myFunction()'/> <font style=\"font-size: 15px; color: green;\"> I have read all the Warnings! </font>"
                            + "<br></fieldset>");
                    out.print("<br><div id = 'butt' style='display:none;'><button type='submit' class='btn btn-info'>Submit</button></div>");
                    out.print("</form></div>");
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();

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
