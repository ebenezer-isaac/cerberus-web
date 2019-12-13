
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
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

    private static final long serialVersionUID = 8429145304207385844L;

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
                            header += "<th style='vertical-align : middle;text-align:center;'> Roll No </th>";
                            header += "<th style='vertical-align : middle;text-align:center;'> PRN </th>";
                            header += "<th style='vertical-align : middle;text-align:center;'> Name </th>";
                            header += "<th style='vertical-align : middle;text-align:center;'> Email </th>";
                            header += "<th style='vertical-align : middle;text-align:center;' align='center'> Fingerprint <br>1 </th>";
                            header += "<th style='vertical-align : middle;text-align:center;' align='center'> Fingerprint <br>2 </th>";
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            while (rs.next()) {
                                line++;
                                out.print("<tr id='row" + line + "'>");
                                out.print("<td style='vertical-align : middle;text-align:center;'><input type='number' style='padding: 3px 0 3px 20px; border-radius: 4px; border: 2px solid green; background: #e6e6e6;' id='roll" + line + "' name='roll" + line + "' min='1' max='120' onkeyup='checkRoll(this.id)' onchange='checkRoll(this.id)' value = '" + String.format("%03d", Integer.parseInt(rs.getString(1))) + "'></td>");
                                out.print("<td style='vertical-align : middle;text-align:center;'>" + rs.getString(2) + "<input type='text' id='prn" + line + "' name='prn" + line + "' value='" + rs.getString(2) + "' hidden></td>");
                                out.print("<td style='vertical-align : middle;text-align:center;'><input type='text' class='editSubjectForm' name='name" + line + "' value='" + rs.getString(3) + "'></td>");
                                out.print("<td style='vertical-align : middle;text-align:center;'><input type='email' style='border: 2px solid green;'class='editSubjectForm' id='email" + line + "' name='email" + line + "' onkeyup='checkdupEmail(" + line + ")' value='" + rs.getString(4) + "'><td style='vertical-align : middle;text-align:center;'>");
                                if (rs.getString(5) != null) {
                                    out.print("<input type='checkbox' value='1' name='t1" + line + "' checked >");
                                } else {
                                    out.print("N/A");
                                }
                                out.print("</td>");
                                out.print("<td style='vertical-align : middle;text-align:center;'>");
                                if (rs.getString(6) != null) {
                                    out.print("<input type='checkbox' value='1' name='t2" + line + "' checked >");
                                } else {
                                    out.print("N/A");
                                }
                                out.print("</td>");

                                out.print("</tr>");
                            }
                            out.print(tableend("No of students : " + line + "<br><br><div id='validations' style='color:red;font-size:14px;'><br></div>"
                                    + "<input type='submit' value='Submit' class='btn btn-primary' style='width: 200px;' align='center' id='subBtn'> <br><br>"
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
                                    + "document.getElementById('email'+line).style.borderColor='green';"
                                    + "if(document.getElementById('email'+line).style.borderColor=='green'&&document.getElementById('roll'+line).style.borderColor=='green'){"
                                    + "var element = document.getElementById('row'+line);"
                                    + "element.classList.remove('table-danger');}"
                                    + "btnstatus5 = 1;"
                                    + "} else {"
                                    + "document.getElementById('email'+line).style.borderColor='red';"
                                    + "var element = document.getElementById('row'+line);"
                                    + "element.classList.add('table-danger');"
                                    + "btnstatus5 = 0;"
                                    + "show()"
                                    + "}"
                                    + "if (btnstatus5 == 1) {"
                                    + "document.getElementById('subBtn').disabled = false;"
                                    + "document.getElementById('validations').innerHTML = '<br>';"
                                    + "} else {"
                                    + "document.getElementById('subBtn').disabled = true;"
                                    + "document.getElementById('validations').innerHTML = 'Validations Error';"
                                    + "}"
                                    + "}"
                                    + "}"
                                    + "function checkRoll() {"
                                    + "for (var j=1; j<=studs; j++) {var rollNum='roll'+j; var rowNum='row'+j;"
                                    + "document.getElementById('subBtn').disabled=false;"
                                    + "document.getElementById('validations').innerHTML = '<br>';"
                                    + "var valueRoll = parseInt(document.getElementById(rollNum).value,10);"
                                    + "if (valueRoll<1 || valueRoll>120) {"
                                    + "console.log(valueRoll);"
                                    + "document.getElementById(rollNum).style.borderColor='red';"
                                    + "var element = document.getElementById(rowNum);\n"
                                    + "element.classList.add('table-danger');"
                                    + "}"
                                    + "else {"
                                    + "var flag=0;"
                                    + "for (var i=1; i<=studs; i++) { "
                                    + "var value = parseInt(document.getElementById('roll'+i).value,10);"
                                    + "if (value==valueRoll && rollNum!='roll'+i) {flag=1;"
                                    + "document.getElementById('roll'+i).style.borderColor='red';"
                                    + "document.getElementById(rollNum).style.borderColor='red';"
                                    + "var element = document.getElementById('row'+i);\n"
                                    + "element.classList.add('table-danger');"
                                    + "var element = document.getElementById(rowNum);\n"
                                    + "element.classList.add('table-danger');"
                                    + "document.getElementById('subBtn').disabled=true;"
                                    + "document.getElementById('validations').innerHTML = 'Validations Error';"
                                    + "}"
                                    + "}if(flag==0){"
                                    + "document.getElementById(rollNum).style.borderColor='green';"
                                    + "if(document.getElementById('email'+j).style.borderColor=='green'&&document.getElementById('roll'+j).style.borderColor=='green'){"
                                    + "var element = document.getElementById(rowNum);\n"
                                    + "element.className = '';}"
                                    + "}else{document.getElementById(rollNum).style.borderColor='red';"
                                    + "var element = document.getElementById(rowNum);\n"
                                    + "element.classList.add('table-danger');"
                                    + "}"
                                    + "}"
                                    + "document.getElementById(rollNum).value = zeroPad(valueRoll);"
                                    + "}"
                                    + "}"
                                    + "$(document).ready(function() {\n"
                                    + "$(\"#hoverText\").hide();"
                                    + "});"
                                    + "function show() {"
                                    + "$.myjQuery();"
                                    + "}"
                                    + "$.myjQuery = function() {\n"
                                    + "$(\"#hoverText\").show();"
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
