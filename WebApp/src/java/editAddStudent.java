
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.oddEve;
import static cerberus.printer.error;
import static cerberus.printer.kids;
import static cerberus.printer.nouser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class editAddStudent extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        out.print("<br><form action='addStudent' method='post'><table cellspacing='10'>"
                                + "<tr><td class=\"editSubjectStyle\">Student Class</td><td> : </td><td>");
                        Statement stmt = con.createStatement();
                        out.print("<select name = 'clas' id = 'clas' style='padding: 5px 109px 5px 8px; border-radius: 4px; border: none; background: #e6e6e6; outline: none; margin: 6px; font-size: 14.5px;' onchange='checkValidations(2);dissub();'>");
                        out.print("<option name='clas' value= '0'>Select Class</option>");
                        ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        int no_of_class = 0;
                        while (rs.next()) {
                            no_of_class++;
                            out.print("<option name='clas' value= '" + no_of_class + "'>" + rs.getString(1) + "</option>");
                        }
                        Date d = new Date();
                        int year = d.getYear() + 1900;
                        out.print("</select>");
                        out.print("</td></tr><tr><td class=\"editSubjectStyle\">Student Name</td><td> : </td><td><input type='text' name='name' class=\"editSubjectForm\" placeholder='Mark Zuckerberg'/></td></tr>"
                                + "<tr><td class=\"editSubjectStyle\">MSU ID</td><td> : </td><td><input type='TEXT' name='photo_id' id='photo_id' class=\"editSubjectForm\" placeholder='D" + String.valueOf(year).substring(2) + "CJxxxxxxx'/></td></tr> "
                                + "<tr><td class=\"editSubjectStyle\">Roll No</td><td> : </td><td><input type='number' name='roll' id='roll' class=\"editSubjectForm\" style= 'width: 216px' onchange='this.value = zeroPad(this.value);checkValidations(2);' value = '01' placeholder='xx' min='1' max='150'/><td><div id='disp3' ><i class=\"fa fa-times\" aria-hidden=\"true\" onk eyup='checkValidations(2);'></i></div></td></td></tr> "
                                + "<tr><td class=\"editSubjectStyle\">PRN</td><td> : </td><td><input type='TEXT' name='prn' id='prn' onkeyup='checkValidations(1)' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/><td><div id='disp2' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></td></tr> "
                                + "<tr><td class=\"editSubjectStyle\">Student Email</td><td> : </td><td><input type='email' id='email' name='email' onkeyup='checkValidations(0)' class=\"editSubjectForm\" placeholder='zuck@gmail.com' /></td><td><div id='disp1' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr> "
                                + "</table><div id='subs'></div><button disabled type='submit' id='studbtn1' class='btn btn-primary'>Add Student</button></form>");
                        out.print("<script>");
                        rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        int oddeve = oddEve(request);
                        int classcount = 1;
                        out.print("function getbatch(name,sub){batch=\"");
                        out.print("<select class='editSelectTimeTable not-allowed' onchange = 'subsdisable(this.id)' name = 'batch\"+sub+\"' id = 'batch\"+name+\"' disabled>"
                                + "<option name='-' value='-' selected >No Batch</option>");
                        PreparedStatement ps = con.prepareStatement("Select batchID, name from batch");
                        ResultSet rs4 = ps.executeQuery();
                        while (rs4.next()) {
                            out.print("<option name='batch" + rs4.getString(1) + "' value='" + rs4.getString(1) + "'>" + rs4.getString(2) + "</option>");
                        }
                        out.print("</select>\";return batch;}");
                        while (rs.next()) {
                            out.print("var class" + classcount + ";");
                            int sem = AttFunctions.getSem(oddeve, classcount);
                            PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                            ps3.setInt(1, sem);
                            ResultSet rs3 = ps3.executeQuery();
                            out.print("class" + classcount + "=\"<table align='center'>");
                            int no_of_sub = 0;
                            while (rs3.next()) {
                                no_of_sub += 1;
                                out.print("<tr><td><input type='checkbox' name='subjects' id='subject" + no_of_sub + "' value='" + rs3.getString(1) + "' onchange='batchdisable(" + no_of_sub + ")'></option></td><td>" + rs3.getString(2) + "</td>"
                                        + "<td>");
                                out.print("\"+getbatch(" + no_of_sub + ",'" + rs3.getString(1) + "')+\"");
                                out.print("<td></tr>");
                            }
                            out.print("</table>\";");
                            classcount++;
                        }
                        classcount--;
                        out.print("function dissub()"
                                + "{var index = document.getElementById('clas').selectedIndex;"
                                + "if(index==0)"
                                + "{document.getElementById('subs').innerHTML = ' ';}");
                        for (int i = 1; i <= classcount; i++) {
                            out.print("else if (index==" + i + ")"
                                    + "{document.getElementById('subs').innerHTML = class" + i + "}");
                        }
                        out.print("}</script>");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private int getWeek() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
