
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
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

public class editSubSelection extends HttpServlet {

    private static final long serialVersionUID = -4900973394776460938L;

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
                            + "</script>");
                    out.print("<style>td{"
                            + "align: center;"
                            + "vertical-align: middle;"
                            + "}</style>");
                    out.print("<div>");
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        String cla = getClassName(classID);
                        int index = 0;
                        int oddeve = oddEve();
                        int sem = AttFunctions.getSem(oddeve, classID);
                        String[][] subs = semSubs(sem, classID);
                        int no_of_sub = subs.length - 1;
                        String sql = "SELECT rollcall.rollNo,student.PRN,student.name,";
                        while (index <= no_of_sub) {
                            sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN (select name from batch where batch.batchID = studentsubject.batchID ) END) as " + subs[index][1].replace('-', '_');
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
                                + "ORDER by LENGTH(rollcall.rollNo),rollcall.rollNo";
                        PreparedStatement ps4 = con.prepareStatement(sql);
                        ResultSet rs = ps4.executeQuery();
                        ResultSetMetaData rsm = rs.getMetaData();
                        int cols = rsm.getColumnCount();
                        int line = 0;
                        if (rs.next()) {
                            out.print("<style>.not-allowed {cursor: not-allowed;}</style>"
                                    + "<form action='saveSubSelection' method='post'>");
                            out.print(tablestart(cla.toUpperCase(), "hover", "studDetails", 0) + "");
                            String header = "<tr>";
                            header += "<th style='vertical-align : middle;text-align:center;'> Roll </th>";
                            header += "<th style='vertical-align : middle;text-align:center;'> Name </th>";
                            for (int i = 4; i <= cols; i++) {
                                header += "<th style='vertical-align : middle;text-align:center;'> " + rsm.getColumnLabel(i) + " </th>";
                            }
                            header += "</tr>";
                            out.print(tablehead(header));
                            rs.previous();
                            PreparedStatement ps11 = con.prepareStatement("Select batchID, name from batch where batchID>0");
                            ResultSet rs4 = ps11.executeQuery();
                            while (rs.next()) {
                                line++;
                                out.print("<tr>");
                                out.print("<td style='vertical-align : middle;text-align:center;'>" + String.format("%02d", Integer.parseInt(rs.getString(1))) + "<input type='text' name='prn" + line + "' value='" + rs.getString(2) + "' hidden></td>");
                                out.print("<td style='vertical-align : middle;text-align:center;'>" + rs.getString(3) + "</td>");
                                for (int i = 4; i <= cols; i++) {
                                    index = 0;
                                    int flag = 0;
                                    if (rs.getString(i).equals("Batch 0")) {
                                        flag = 0;
                                    } else {
                                        flag = 1;
                                    }
                                    out.print("<td style='vertical-align : middle;text-align:center;'><input type='checkbox' value='1' id='sub" + subs[i - 4][0] + "" + line + "' name='sub" + subs[i - 4][0] + "" + line + "' onchange='batchdisable(this.id)'");

                                    if (flag == 1) {
                                        out.print(" checked");
                                    }
                                    out.print("><select onchange = 'subsdisable(this.id)' name = 'batch" + subs[i - 4][0] + "" + line + "' id = 'batch" + subs[i - 4][0] + "" + line + "' class='editSelectTimeTable");
                                    if (flag == 0) {
                                        out.print(" not-allowed' disabled");
                                    } else {
                                        out.print("'");
                                    }
                                    out.print("><option name='-' value='0'");
                                    if (flag == 0) {
                                        out.print("selected");
                                    }
                                    out.print("> No Batch</option >");
                                    rs4.first();
                                    rs4.previous();
                                    while (rs4.next()) {
                                        out.print("<option name='batch" + rs4.getString(1) + "' value='" + rs4.getString(1) + "'");
                                        if (flag == 1 && rs.getString(i).equals(rs4.getString(2))) {
                                            out.print("selected");
                                        }
                                        out.print(">" + rs4.getString(2) + "</option>");
                                    }
                                    out.print("</select>");
                                    out.print("</td>");
                                }
                                out.print("</tr>");
                            }
                            out.print(tableend("No of students : " + line + "<br><br><div id='validations' style='color:red;font-size:14px;'>Changing the batch of a student mid - semester is not advised.<br>All attendance pertaining to the previously set batch will not be calculated when displaying attendance of that student.</div>"
                                    + "<br><input class='btn-primary btn' type='Save' style='width: 200px;' value='Save Details' align='center'>"
                                    + "<input type='text' name='division' value='" + classID + "' hidden>"
                                    + "</form><br><br>", 0));
                            out.print("<script>function batchdisable(id) {"
                                    + "if(document.getElementById(id).checked)"
                                    + "{id = id.substr(3);"
                                    + "document.getElementById('batch' + id).selectedIndex=1;"
                                    + "document.getElementById('batch' + id).disabled=false;"
                                    + "document.getElementById('batch' + id).classList.remove('not-allowed');}"
                                    + "else{id = id.substr(3);"
                                    + "document.getElementById('batch' + id).selectedIndex=0;"
                                    + "document.getElementById('batch' + id).disabled=true;"
                                    + "document.getElementById('batch' + id).classList.add('not-allowed');}}");
                            out.print("function subsdisable(id) {"
                                    + "var index = document.getElementById(id).selectedIndex;"
                                    + "if(index == 0)"
                                    + "{id = id.substr(5);"
                                    + "document.getElementById('sub' + id).checked=false;"
                                    + "document.getElementById('batch' + id).disabled=true;"
                                    + "document.getElementById('batch' + id).classList.add('not-allowed');}}"
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
