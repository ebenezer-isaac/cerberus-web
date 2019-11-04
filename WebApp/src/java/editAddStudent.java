
import cerberus.AttFunctions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editAddStudent extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:

                        out.print("<style>"
                                + "input[type=number]{"
                                + "width: 40px;"
                                + "} "
                                + "</style>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            out.print("<br><form action='addStudent' method='post'><table cellspacing='10'>"
                                    + "<tr><td class=\"editSubjectStyle\">Student Class</td><td> : </td><td>");
                            Statement stmt = con.createStatement();
                            out.print("<select name = 'clas' id = 'clas' class=\"editSelect\" onchange='sendInfo(2);dissub();'>");
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
                                    + "<tr><td class=\"editSubjectStyle\">Roll No</td><td> : </td><td><input type='number' name='roll' id='roll' class=\"editSubjectForm\" style= 'width: 216px' onchange='this.value = zeroPad(this.value);sendInfo(2);' value = '01' placeholder='xx' min='1' max='150'/><td><div id='disp3' ><i class=\"fa fa-times\" aria-hidden=\"true\" onk eyup='sendInfo(2);'></i></div></td></td></tr> "
                                    + "<tr><td class=\"editSubjectStyle\">PRN</td><td> : </td><td><input type='TEXT' name='prn' id='prn' onkeyup='sendInfo(1)' class=\"editSubjectForm\" placeholder='20xx03380010xxxx'/><td><div id='disp2' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></td></tr> "
                                    + "<tr><td class=\"editSubjectStyle\">Student Email</td><td> : </td><td><input type='email' id='email' name='email' onkeyup='sendInfo(0)' class=\"editSubjectForm\" placeholder='zuck@gmail.com' /></td><td><div id='disp1' ><i class=\"fa fa-times\" aria-hidden=\"true\"></i></div></td></tr> "
                                    + "</table><div id='subs'></div><button disabled type='submit' id='studbtn1' class='btn btn-info'>Add Student</button></form>");
                            out.print("<script>");
                            rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                            PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
                            st.setInt(1, Integer.parseInt(session.getAttribute("week").toString()));
                            ResultSet rs2 = st.executeQuery();
                            int oddeve = 0;
                            while (rs2.next()) {
                                oddeve = (rs2.getInt(1) % 2);
                            }
                            int classcount = 1;
                            out.print("function getbatch(name,sub){batch=\"");
                            out.print("<select class='editSelectTimeTable not-allowed' onchange = 'subsdisable(this.id)' name = 'batch\"+sub+\"' id = 'batch\"+name+\"' disabled>"
                                    + "<option name='-' value='-' selected >No Batch</option>");
                            PreparedStatement ps11 = con.prepareStatement("Select batchID, name from batch");
                            ResultSet rs4 = ps11.executeQuery();
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
                            out.print("}</script><script src=\"js/studaddvalidations.js\">");
                            con.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Hey 'Kid'!");
                        request.setAttribute("body", "You are not authorized to view this page");
                        request.setAttribute("url", "homepage");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);
                        break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Please login to continue");
                request.setAttribute("url", "index.jsp");
                request.setAttribute("sec", "2");
                rd.forward(request, response);
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
}
