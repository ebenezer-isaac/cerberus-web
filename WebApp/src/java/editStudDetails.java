
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class editStudDetails extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            try {
                int access = (int) session.getAttribute("access");
                switch (access) {
                    case 1:
                        request.getRequestDispatcher("side-faculty.html").include(request, response);
                        int classID;
                        try {
                            classID = Integer.parseInt(request.getParameter("class"));
                        } catch (NumberFormatException e) {
                            classID = 1;
                        }
                        int week = (int) session.getAttribute("week");
                        out.print("<script>"
                                + "function zeroPad(num) {"
                                + "var s = num+'';"
                                + "while (s.length < 2) s = '0' + s;"
                                + "return(s);"
                                + "}"
                                + "</script>");
                        out.println("<div>");
                        try {
                            System.out.println("hello");
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                            String cla = "";
                            PreparedStatement ps1 = con.prepareStatement("Select class from class where classID=?");
                            ps1.setInt(1, classID);
                            ResultSet rs = ps1.executeQuery();
                            while (rs.next()) {
                                cla = rs.getString(1);
                            }

                            PreparedStatement ps2 = con.prepareStatement("Select rollNo, PRN from rollcall where classID=?");
                            ps2.setInt(1, classID);
                            rs = ps2.executeQuery();
                            int no_of_stud = 0;
                            while (rs.next()) {
                                no_of_stud++;
                            }
                            rs = ps2.executeQuery();
                            int[][] prn = new int[no_of_stud][2];
                            no_of_stud--;
                            int index = 0;
                            while (rs.next()) {
                                prn[index][0] = rs.getInt(1);
                                prn[index][1] = rs.getInt(2);
                                index++;
                            }
                            int sem = 1;
                            PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
                            st.setInt(1, week);
                            ResultSet rs2 = st.executeQuery();
                            while (rs2.next()) {
                                sem = (rs2.getInt(1) % 2);
                                if (sem == 0) {
                                    sem += 2;
                                }
                            }
                            PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                            ps3.setInt(1, 5);
                            rs = ps3.executeQuery();
                            int no_of_sub = 0;
                            while (rs.next()) {
                                no_of_sub++;
                            }
                            rs = ps3.executeQuery();
                            String[][] subs = new String[no_of_sub][];
                            no_of_sub--;
                            index = 0;
                            while (rs.next()) {
                                subs[index][0] = rs.getString(1);
                                subs[index][1] = rs.getString(2);
                                index++;
                            }
                            out.print("<form action='editStudDetail' method='post'>");
                            out.println("<h1>" + cla.toUpperCase() + "</p> Details </h1>");
                            out.println("<table class=\"table table-striped table-bordered\"><thead>");
                            out.println("<tr>");
                            out.println("<th> PRN </th>");
                            out.println("<th> Name </th>");
                            Statement stmt = con.createStatement();
                            String sql = "SELECT student.PRN, student.name, student.email,";
                            index = 0;
                            while (index <= no_of_sub) {
                                sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat(' 1 ',' ') END) as " + subs[index][1] + " FROM student";
                                if (index <= (no_of_sub - 1)) {
                                    sql += ", ";
                                }
                            }
                            sql += "INNER JOIN studentsubject "
                                    + "ON student.PRN = studentsubject.PRN "
                                    + "where student.PRN in (";
                            index = 0;
                            while (index <= no_of_stud) {
                                sql += "" + prn[no_of_stud][1];
                                if (index <= (no_of_stud - 1)) {
                                    sql += ", ";
                                }
                            }
                            sql += "GROUP BY studentsubject.PRN";
                            System.out.println(sql);
                            /*
                            rs = stmt.executeQuery("select * from details_" + division + " ORDER BY `roll` ASC");
                            ResultSetMetaData rsm = rs.getMetaData();
                            int cols = rsm.getColumnCount();
                            for (int i = 3; i <= cols; i++) {
                                ResultSet subname = stmt.executeQuery("select `name` from subjects where `code`= '" + rsm.getColumnLabel(i).toUpperCase() + "'");
                                while (subname.next()) {
                                    out.println("<th> " + subname.getString(1) + " </th>");
                                }
                                //out.println("<th> " + rsm.getColumnLabel(i).toUpperCase() + " </th>");
                            }
                            rs = stmt.executeQuery("select * from details_" + division);
                            out.println("</tr>");
                            int line = 0;
                            while (rs.next()) {
                                line++;
                                out.println("<tr>");
                                out.println("<td><input type='number' name='roll" + line + "' min='1' max='120' onchange='this.value = zeroPad(this.value)' value = '" + String.format("%02d", Integer.parseInt(rs.getString(1))) + "'></td>");
                                out.println("<td><input type='text' name='name" + line + "' value='" + rs.getString(2) + "'></td>");
                                for (int i = 3; i <= cols; i++) {
                                    out.println("<td><input type='checkbox' name='sub" + line + "" + i + "'");
                                    if (Integer.parseInt(rs.getString(i)) == 1) {
                                        out.println(" checked ");
                                    }
                                    out.println("></td>");
                                }
                                out.println("</tr>");
                            }*/
                        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        }/*
                        out.println("</table><br><br>");
                        out.println("<input type='submit' value='Submit' align='center'>"
                                + "<input type='text' name='division' value='" + division + "' hidden>"
                                + "<input type='text' name='cols' value='" + cols + "' hidden>"
                                + "<input type='text' name='rows' value='" + line + "' hidden>");
                        out.println("</form><br>");
                        out.println("<form action='menu' method='post'>");
                        out.println("<input type='submit' value='Back'>");
                        out.println("</form>");*/
                        request.getRequestDispatcher("end.html").include(request, response);
                        break;

                    case 0:
                        request.getRequestDispatcher("side-student.html").include(request, response);
                        break;
                    default:
                        RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                        request.setAttribute("redirect", "true");
                        request.setAttribute("head", "Security Firewall");
                        request.setAttribute("body", "Please login to continue");
                        request.setAttribute("url", "index.html");
                        request.setAttribute("sec", "2");
                        rd.forward(request, response);
                }
            } catch (IOException | ServletException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "true");
                request.setAttribute("head", "Security Firewall");
                request.setAttribute("body", "Please login to continue");
                request.setAttribute("url", "index.html");
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
