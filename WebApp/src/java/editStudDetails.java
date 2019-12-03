
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getWeek;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
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

public class editStudDetails extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    int classID;
                    try {
                        classID = Integer.parseInt(request.getParameter("class"));
                    } catch (NumberFormatException e) {
                        classID = 3;
                    }
                    out.print("<script>"
                            + "function zeroPad(num) {"
                            + "var s = num+'';"
                            + "while (s.length < 2) s = '0' + s;"
                            + "return(s);"
                            + "}"
                            + "</script>"
                            + "<style>"
                            + ".valid{"
                            + "border:solid 1px green;"
                            + "}"
                            + ".invalid{"
                            + "border:solid 1px red;"
                            + "}"
                            + "</style>");
                    out.print("<div>");
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        String cla = getClassName(classID);
                        PreparedStatement ps4 = con.prepareStatement("SELECT rollcall.rollNo, student.PRN, student.name, student.email,"
                                + "MAX(CASE WHEN studentfingerprint.templateID = 1 and studentfingerprint.template is not null THEN concat(' 1 ',' ') END) as Template1, "
                                + "MAX(CASE WHEN studentfingerprint.templateID = 2 and studentfingerprint.template is not null THEN concat(' 1 ',' ') END) as Template2 "
                                + "FROM student "
                                + "INNER JOIN rollcall  on  rollcall.PRN = student.PRN "
                                + "INNER JOIN studentfingerprint  on  student.PRN = studentfingerprint.PRN "
                                + "where student.PRN in (select rollcall.PRN "
                                + "from rollcall "
                                + "where rollcall.classID = ?) "
                                + "GROUP BY student.PRN "
                                + "ORDER by LENGTH(rollcall.rollNo),rollcall.rollNo;");
                        ps4.setInt(1, classID);
                        System.out.println("hello");
                        ResultSet rs = ps4.executeQuery();
                        ResultSetMetaData rsm = rs.getMetaData();
                        int cols = rsm.getColumnCount();
                        int line = 0;
                        if (rs.next()) {
                            System.out.println("hello");
                            out.print("<form action='saveStudDetails' method='post'>");
                            out.print(tablestart(cla.toUpperCase(), "hover", "studDetails", 0) + "");
                            String header = "<tr>";
                            header += "<th> Roll No </th>";
                            header += "<th> PRN </th>";
                            header += "<th> Name </th>";
                            header += "<th> Email </th>";
                            header += "<th align='center'> Fingerprint <br>1 </th>";
                            header += "<th align='center'> Fingerprint <br>2 </th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            while (rs.next()) {
                                line++;
                                out.print("<tr>");
                                out.print("<td><input type='number' id='roll" + line + "' name='roll" + line + "' min='1' max='120' class='valid' onkeyup='checkRoll(this.id)' onchange='checkRoll(this.id)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1))) + "'></td>");
                                out.print("<td><div>" + rs.getString(2) + "</div><input type='text' id='prn" + line + "' name='prn" + line + "' value='" + rs.getString(2) + "' hidden></td>");
                                out.print("<td><input type='text' name='name" + line + "' value='" + rs.getString(3) + "'></td>");
                                out.print("<td><input type='email' class='valid' id='email" + line + "' name='email" + line + "' onkeyup='checkdupEmail(" + line + ")' value='" + rs.getString(4) + "'><td>");
                                if (rs.getString(5) != null) {
                                    out.print("<input type='checkbox' value='1' name='t1" + line + "' checked >");
                                } else {
                                    out.print("N/A");
                                }
                                out.print("</td>");
                                out.print("<td>");
                                if (rs.getString(6) != null) {
                                    out.print("<input type='checkbox' value='1' name='t2" + line + "' checked >");
                                } else {
                                    out.print("N/A");
                                }
                                out.print("</td>");

                                out.print("</tr>");
                            }
                            out.print(tableend("No of students : " + line + "<br><br>"
                                    + "<input type='submit' value='Submit' align='center'>"
                                    + "<input type='text' name='division' value='" + classID + "' hidden>"
                                    + "</form>", 0));
                            out.print("<script>"
                                    + "var studs = " + line + ";"
                                    + "var btnstatus5 = 0;var line=0;"
                                    + "function checkdupEmail(id) {line=id;"
                                    + "prn = document.getElementById('prn'+id).value;"
                                    + "email = document.getElementById('email'+id).value;"
                                    + "var url = 'ajaxDupEmail?prn=' + prn+'&email='+email;"
                                    + "if (window.XMLHttpRequest) {"
                                    + "request = new XMLHttpRequest()"
                                    + "} else if (window.ActiveXObject) {"
                                    + "request = new ActiveXObject('Microsoft.XMLHTTP');"
                                    + "}"
                                    + "try {"
                                    + "request.onreadystatechange = setInf;"
                                    + "request.open('GET', url, true);"
                                    + "request.send();"
                                    + "} catch (e) {"
                                    + "alert('Unable to connect to server');"
                                    + "}"
                                    + "}"
                                    + ""
                                    + "function setInf() {"
                                    + "if (request.readyState == 4) {"
                                    + "var val = request.responseText;"
                                    + "if (val == 1) {"
                                    + "document.getElementById('dispemail'+line).classList.add('invalid');"
                                    + "btnstatus5 = 1;"
                                    + "} else {"
                                    + "document.getElementById('dispemail'+line).classList.add('valid');"
                                    + "btnstatus5 = 0;"
                                    + "}"
                                    + "if (btnstatus5 == 1) {"
                                    + "document.getElementById('studdbtn').disabled = false;"
                                    + "} else {"
                                    + "document.getElementById('studdbtn').disabled = true;"
                                    + "}"
                                    + "}"
                                    + "}"
                                    + "function checkRoll(rollNum) {"
                                    + "var valueRoll = parseInt(document.getElementById(rollNum).value,10);"
                                    + "if (valueRoll<1 || valueRoll>120) {"
                                    + "console.log(valueRoll);"
                                    + "document.getElementById(rollNum).style.borderColor='red';"
                                    + "}"
                                    + "else {"
                                    + "document.getElementById(rollNum).style.borderColor='green';"
                                    + "for (var i=1; i<=studs; i++) { "
                                    + "var value = parseInt(document.getElementById('roll'+i).value,10);"
                                    + "if (value==valueRoll && rollNum!='roll'+i) {"
                                    + "document.getElementById('roll'+i).style.borderColor='red';"
                                    + "document.getElementById(rollNum).style.borderColor='red';"
                                    + "}"
                                    + "else {"
                                    + "document.getElementById('roll'+i).style.borderColor='green';"
                                    + "}"
                                    + "}"
                                    + "}"
                                    + "document.getElementById(rollNum).value = zeroPad(valueRoll);"
                                    + "}"
                                    + "</script>");
                        } else {
                            out.print("No Data to display");
                        }
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        error(e.getMessage());
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
