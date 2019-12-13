
import cerberus.AttFunctions;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class saveSubSelection extends HttpServlet {

    private static final long serialVersionUID = -2692354481675679164L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);

            switch (access) {
                case 1:
                    int classID = Integer.parseInt(request.getParameter("division"));
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        String cla = getClassName(classID);
                        int index = 0;
                        int oddeve = oddEve();
                        int sem = AttFunctions.getSem(oddeve, classID);
                        String[][] subs = semSubs(sem, classID);
                        int no_of_sub = subs.length - 1;
                        String sql = "SELECT student.PRN, ";
                        while (index <= no_of_sub) {
                            sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat('" + subs[index][0] + "',',',(studentsubject.batchID)) END) as " + subs[index][1].replace('-', '_');
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
                        index = 1;
                        while (rs.next()) {
                            String prn = request.getParameter("prn" + index);
                            int subsele[][] = new int[subs.length][2];
                            for (int i = 0; i < subs.length; i++) {
                                String arr[] = null;
                                String estring = rs.getString(2 + i);
                                try {
                                    arr = estring.split(",");
                                } catch (Exception e) {
                                }

                                try {
                                    subsele[i][0] = Integer.parseInt(request.getParameter("sub" + subs[i][0] + "" + index));
                                } catch (NumberFormatException e) {
                                    subsele[i][0] = 0;
                                }
                                try {
                                    subsele[i][1] = Integer.parseInt(request.getParameter("batch" + subs[i][0] + "" + index));
                                } catch (NumberFormatException e) {
                                    subsele[i][1] = 0;
                                }
                                if (subsele[i][1] == Integer.parseInt(arr[1])) {
                                } else {
                                    PreparedStatement pps = con.prepareStatement("update studentsubject set batchID=? where prn = ? and subjectID = ?");
                                    pps.setInt(1, subsele[i][1]);
                                    pps.setString(2, prn);
                                    pps.setString(3, subs[i][0]);
                                    pps.executeUpdate();
                                }

                            }
                            index++;
                        }
                        messages a = new messages();
                        a.success(request, response, "Subject Selection has been saved", "editSubSelection?class=" + classID);
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages a = new messages();
                        a.dberror(request, response, e.getMessage(), "editSubSelection?class=" + classID);
                    }
                    break;
                case 0:
                    messages b = new messages();
                    b.kids(request, response);
                    break;
                default:
                    messages c = new messages();
                    c.nouser(request, response);
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
