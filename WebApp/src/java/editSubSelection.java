
import cerberus.AttFunctions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editSubSelection extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:

                        int classID;
                        try {
                            classID = Integer.parseInt(request.getParameter("class"));
                        } catch (NumberFormatException e) {
                            classID = 3;
                        }
                        int week = (int) session.getAttribute("week");
                        out.print("<script>"
                                + "function zeroPad(num) {"
                                + "var s = num+'';"
                                + "while (s.length < 2) s = '0' + s;"
                                + "return(s);"
                                + "}"
                                + "</script>");
                        out.print("<div>");
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            String cla = "";
                            PreparedStatement ps1 = con.prepareStatement("Select class from class where classID=?");
                            ps1.setInt(1, classID);
                            ResultSet rs = ps1.executeQuery();
                            while (rs.next()) {
                                cla = rs.getString(1);
                            }
                            int index;
                            int oddeve = 1;
                            PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
                            st.setInt(1, week);
                            ResultSet rs2 = st.executeQuery();
                            while (rs2.next()) {
                                oddeve = (rs2.getInt(1) % 2);
                            }
                            int sem = AttFunctions.getSem(oddeve, classID);
                            PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                            ps3.setInt(1, sem);
                            rs = ps3.executeQuery();
                            int no_of_sub = 0;
                            while (rs.next()) {
                                no_of_sub++;
                            }
                            rs = ps3.executeQuery();
                            String[][] subs = new String[no_of_sub][2];
                            no_of_sub--;
                            index = 0;
                            while (rs.next()) {
                                subs[index][0] = rs.getString(1);

                                subs[index][1] = rs.getString(2);
                                index++;
                            }

                            out.print("<h3 align='center'>" + cla.toUpperCase() + " Subject Selection </h3>");
                            index = 0;
                            String sql = "SELECT rollcall.rollNo,student.PRN,student.name,";
                            while (index <= no_of_sub) {
                                sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat('1',',',(select name from batch where batch.batchID = studentsubject.batchID )) END) as " + subs[index][1].replace('-', '_');
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
                                    + "ORDER by rollcall.rollNo";
                            System.out.println(sql);
                            PreparedStatement ps4 = con.prepareStatement(sql);
                            rs = ps4.executeQuery();
                            ResultSetMetaData rsm = rs.getMetaData();
                            int cols = rsm.getColumnCount();
                            int line = 0;
                            if (rs.next()) {
                                out.print("<style>.not-allowed {cursor: not-allowed;}</style>"
                                        + "<form action='editStudDetail' method='post'>");
                                out.print("<table class=\"table table-striped table-bordered\"><thead>");
                                out.print("<tr>");
                                out.print("<th> Roll </th>");
                                out.print("<th> Name </th>");
                                for (int i = 4; i <= cols; i++) {
                                    out.print("<th> " + rsm.getColumnLabel(i) + " </th>");
                                }
                                out.print("</tr>");
                                rs.previous();
                                PreparedStatement ps11 = con.prepareStatement("Select batchID, name from batch");
                                ResultSet rs4 = ps11.executeQuery();
                                while (rs.next()) {
                                    line++;
                                    out.print("<tr>");
                                    out.print("<td>" + String.format("%02d", Integer.parseInt(rs.getString(1))) + "<input type='text' name='prn" + line + "' value='" + rs.getString(2) + "' hidden></td>");
                                    out.print("<td>" + rs.getString(3) + "</td>");
                                    for (int i = 4; i <= cols; i++) {
                                        System.out.println(rs.getString(i));
                                        int flag = 0;
                                        String arr[] = null;
                                        try {
                                            arr = rs.getString(i).split(",");
                                            flag = 1;
                                        } catch (Exception e) {
                                            flag = 0;
                                        }
                                        out.print("<td><input type='checkbox' id='sub" + rsm.getColumnLabel(i) + "" + line + "' name='sub" + rsm.getColumnLabel(i) + "" + line + "' onchange='batchdisable(this.id)'");

                                        if (flag == 1) {
                                            out.print(" checked");
                                        }
                                        out.print("><select class='editSelectTimeTable not-allowed' onchange = 'subsdisable(this.id)' name = 'batch" + rsm.getColumnLabel(i) + "" + line + "' id = 'batch" + rsm.getColumnLabel(i) + "" + line + "'");
                                        if (flag == 0) {
                                            out.print("disabled");
                                        }
                                        out.print(">"
                                                + "<option name='-' value='-'");
                                        if (flag == 0) {
                                            out.print("selected");
                                        }
                                        out.print("> No Batch</option >");
                                        rs4.first();
                                        rs4.previous();
                                        while (rs4.next()) {
                                            out.print("<option name='batch" + rs4.getString(1) + "' value='" + rs4.getString(1) + "'");
                                            if (flag == 1 && arr[1].equals(rs4.getString(2))) {
                                                out.print("selected");
                                            }
                                            out.print(">" + rs4.getString(2) + "</option>");
                                        }
                                        out.print("</select>");
                                        out.print("</td>");
                                    }
                                    out.print("</tr>");
                                }
                                out.print("</table><br><br>");
                                out.print("<input type='submit' value='Submit' align='center'>"
                                        + "<input type='text' name='division' value='" + classID + "' hidden>"
                                        + "<input type='text' name='cols' value='" + cols + "' hidden>"
                                        + "<input type='text' name='rows' value='" + line + "' hidden>");
                                out.print("</form><br>");
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
                        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        }

                        break;

                    case 0:
                        request.getRequestDispatcher("side-student.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Security Firewall");
                        request.setAttribute("body", "Please login to continue");
                        request.setAttribute("url", "index.jsp");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);
                }
            } catch (IOException | ServletException e) {
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
