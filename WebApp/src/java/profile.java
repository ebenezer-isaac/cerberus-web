
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.oddEveSubs;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

public class profile extends HttpServlet {

    String subs[][];
    String prefSub[];
    private static final long serialVersionUID = -2007307218613281629L;

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
                        PreparedStatement ps = con.prepareStatement("SELECT name, photo FROM faculty WHERE facultyID = ?");
                        HttpSession session = request.getSession(false);
                        String facultyID = session.getAttribute("user").toString();
                        ps.setString(1, facultyID);
                        byte[] blob = null;
                        String name = "";
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            blob = rs.getBytes("photo");
                            name = rs.getString("name");
                        }
                        if (blob != null) {
                            String imgString = DatatypeConverter.printBase64Binary(blob);
                            out.print("<img style='border-radius: 10%;width:80px;height:110px;' src='data:image/png;base64," + imgString + "'/><br><br>" + name);
                        }
                        out.print("<br><br>");
                        out.print("<form action='savePref' method='post'>");
                        out.print(tablestart("My Preferences", "hover", "preferences", 0));
                        String header = "<tr>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Subject Code</th>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Abbreviation</th>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Preference</th>";
                        header += "</tr>";
                        out.print(tablehead(header));
                        subs = oddEveSubs();
                        ps = con.prepareStatement("SELECT subjectID FROM facultysubject WHERE facultyID = ?");
                        ps.setString(1, facultyID);
                        rs = ps.executeQuery();
                        int no_of_pref = 0;
                        while (rs.next()) {
                            no_of_pref++;
                        }
                        System.out.println(no_of_pref);
                        prefSub = new String[no_of_pref];
                        rs.first();
                        rs.previous();
                        int index = 0;
                        while (rs.next()) {
                            prefSub[index] = rs.getString(1);
                            index++;
                        }
                        for (String[] sub : subs) {
                            out.print("<tr><td style='vertical-align : middle;text-align:center;'>" + sub[0] + "</td>"
                                    + "<td style='vertical-align : middle;text-align:center;'>" + sub[1] + "</td>"
                                    + "<td style='vertical-align : middle;text-align:center;'><input type='checkbox' value='" + sub[0] + "' name='subjects'");
                            if (isfav(sub[0])) {
                                out.print(" checked ");
                            }
                            out.print("></td><tr>");
                        }

                        out.print(tableend("<br><button class='btn btn-primary' style='width:200px' type='submit'>Submit</button><br><br>"
                                + "</form>", 0));
                        con.close();
                    } catch (Exception e) {
                        e.printStackTrace();
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

    public boolean isfav(String subject) {
        System.out.println("finding "+ subject);
        for (String sub : prefSub) {
            System.out.println("checking "+sub);
            int index = subject.indexOf(sub);
            if (index != -1) {
                return true;
            }
        }
        return false;
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
