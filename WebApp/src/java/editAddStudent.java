
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

    private static final long serialVersionUID = 6491755108919078233L;

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
                        out.print("<br><form action='addStudent' method='post'><table cellspacing='10' cellpadding=3>"
                                + "<tr><td class=\"editSubjectStyle\">Student Class</td><td> : </td><td>");
                        Statement stmt = con.createStatement();
                        out.print("<select name = 'clas' id = 'clas' style='padding: 5px 109px 5px 8px; border-radius: 4px; border: none; background: #e6e6e6; outline: none; margin: 6px; font-size: 14.5px;' onchange='checkValidations(2);'>");
                        out.print("<option name='clas' value= '0'>Select Class</option>");
                        ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                        int no_of_class = 0;
                        while (rs.next()) {
                            no_of_class++;
                            out.print("<option name='clas' value= '" + no_of_class + "'>" + rs.getString(1) + "</option>");
                        }
                        Date d = new Date();
                        out.print("</select>");
                        out.print("</td></tr><tr><td class=\"editSubjectStyle\">Student Name</td><td> : </td><td><input required type='text' name='name' id='name' minlength='5' maxlength='120' class=\"editSubjectForm\" placeholder='Mark Zuckerberg'/></td></tr>"
                                + "<tr><td class=\"editSubjectStyle\">Roll No</td><td> : </td><td><input type='number' name='roll' id='roll' class=\"editSubjectForm\" style= 'width: 216px' onchange=\"var index = document.getElementById('clas').selectedIndex;if (index != 0){checkValidations(2);}this.value = zeroPad(this.value);\" value = '001' placeholder='xx' min='1' max='150'/><td><div id='disp3'class=\"tooltipp\" style='width:100px'><i class=\"fa fa-times\" aria-hidden=\"true\" onk eyup='checkValidations(2);'></i></div></td></td></tr> "
                                + "<tr><td class=\"editSubjectStyle\">PRN</td><td> : </td><td><input type='TEXT' name='prn' id='prn' onchange='checkValidations(1);' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/><td><div id='disp2' class=\"tooltipp\" style='width:100px'><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></td></tr> "
                                + "<tr><td class=\"editSubjectStyle\">Student Email</td><td> : </td><td><input type='email' id='email' name='email' onchange='checkValidations(0);' class=\"editSubjectForm\" placeholder='zuck@gmail.com' /></td><td><div id='disp1' class=\"tooltipp\" style='width:100px'><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr> "
                                + "<tr><td></td><td></td><td class='text-center'><div id='validations' style='color:red;font-size:14px;'><br></div></td></tr>"
                                + "</table><div id='subs'></div><br><button disabled type='submit' style='width:200px;' id='studbtn1' class='btn btn-primary'>Add Student</button></form>");

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
