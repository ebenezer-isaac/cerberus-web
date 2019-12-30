
import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.oddEveSubs;
import static cerberus.AttFunctions.semSubs;
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
import java.sql.SQLException;
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
                    out.print("<div class='row' align='center'><div class='col-xl-4 col-sm-6 mb-3' align='center'><br><br>");
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
                            out.print("<img style='border-radius: 10%;height:130px; width:130px;' src='data:image/png;base64," + imgString + "'/><br><br><font size=4>" + name + "</font>");
                        } else {
                            out.print("<img style='width:150px;height:150px;'src='images/student.png'/><br><br><font size=4>" + name + "</font>");
                        }
                        ps = con.prepareStatement("SELECT \n"
                                + "MAX(CASE \n"
                                + "WHEN facultyfingerprint.templateID = 1 and facultyfingerprint.template is not null THEN ' 1 '\n"
                                + "WHEN facultyfingerprint.templateID = 1 and facultyfingerprint.template is null then '0'\n"
                                + "END) as Template1, \n"
                                + "MAX(CASE \n"
                                + "WHEN facultyfingerprint.templateID = 2 and facultyfingerprint.template is not null THEN ' 1 '\n"
                                + "WHEN facultyfingerprint.templateID = 2 and facultyfingerprint.template is null then '0'\n"
                                + "END) as Template2 \n"
                                + "FROM facultyfingerprint \n"
                                + "where facultyfingerprint.facultyID = ?");
                        ps.setString(1, facultyID);
                        out.print("<br>Faculty ID : " + facultyID + "<br><br>");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            for (int i = 1; i < 3; i++) {
                                int flag;
                                if (Integer.parseInt(rs.getString(i).trim()) == 0) {
                                    flag = 0;
                                } else {
                                    flag = 1;
                                }
                                out.print("<div class='col-xl-10 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-");
                                if (flag == 0) {
                                    out.print("danger ");
                                } else {
                                    out.print("success ");
                                }
                                out.print("small o-hidden h-60'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Fingerprint " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class=\"fas fa-fingerprint\"></i>"
                                        + "</div>"
                                        + "<div class='mr-1' align='center'><br>");
                                if (flag == 0) {
                                    out.print("Unavailable<br><br>");

                                } else {
                                    out.print("Available");
                                }
                                out.print("<br><br></div>"
                                        + "</div>"
                                        + "");
                                if (flag == 1) {
                                    out.print("<a class='card-footer text-white clearfix small z-1' "
                                            + "href=\"javascript:setContent('/Cerberus/delFacFingerprint?id=" + i + "');\">"
                                            + "<span class='float-left'>Delete Fingerprint</span>"
                                            + "<span class='float-right'>"
                                            + "<i class='fas fa-angle-right'></i>"
                                            + "</span>"
                                            + "</a>");
                                }
                                out.print(""
                                        + ""
                                        + "</div>"
                                        + "</div>");
                            }
                        }
                        out.print("<div class='col-xl-9 col-sm-6 mb-3' style='border: solid black 1px;border-radius:4px'align='center'>");
                        out.print("<form action='editProfile' method='post' enctype='multipart/form-data'>\n"
                                + "<br><div style='border: solid #6D6A65 1px;border-radius:4px'><input type='file'  name='avatar-file' required></div>\n"
                                + "<br><div id='validations' style='color:red;font-size:14px;'>Picture should be less than 25 KB<br>and have a ratio of 1:1</div>"
                                + "<br><button class='btn btn-primary form-btn' style='width:200px;' type='submit'>Upload</button>\n"
                                + "</form><br>");
                        out.print("</div>"
                                + "</div><div class='col-xl-8 col-sm-6 mb-3' align='center'>");
                        out.print("<form action='savePref' method='post'>");
                        out.print(tablestart("<b>Subject Preferences</b>", "hover", "preferences", 0));
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
                                    + "<td style='vertical-align : middle;text-align:center;'><center><input type='checkbox' id='" + index + "' value='" + sub[0] + "' name='subjects'");
                            if (isfav(sub[0])) {
                                out.print(" checked ");
                            }
                            out.print("><label for='" + index + "'></label></center></td><tr>");
                            index++;
                        }

                        out.print(tableend("<br><button class='btn btn-primary' style='width:200px' type='submit'>Save Preferences</button><br><br>"
                                + "</form><style type='text/css'>\n"
                                + "@import url('css/checkbox.css');\n"
                                + "</style>", 0));
                        con.close();
                    } catch (IOException | ClassNotFoundException | SQLException e) {
                        errorLogger(e.getMessage());
                    }
                    out.print("</div></div>To change password, Click 'Create a new Password' in Login Page.");
                    break;
                case 0:
                    out.print("<div class='row' align='center'><div class='col-xl-4 col-sm-6 mb-3' align='center'><br><br>");
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("SELECT studentphoto.photo FROM studentphoto WHERE studentphoto.prn = ?");
                        HttpSession session = request.getSession(false);
                        String prn = session.getAttribute("user").toString();
                        ps.setString(1, prn);
                        byte[] blob = null;
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            blob = rs.getBytes("photo");
                        }
                        ps = con.prepareStatement("SELECT student.name FROM student WHERE prn = ?");
                        ps.setString(1, prn);
                        String name = "";
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            name = rs.getString("name");
                        }
                        ps = con.prepareStatement("select rollcall.rollNo, rollcall.classID from rollcall where rollcall.prn = ?");
                        ps.setString(1, prn);
                        int classID = 0;
                        int rollNo = 0;
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            rollNo = rs.getInt(1);
                            classID = rs.getInt(2);
                        }
                        if (blob != null) {
                            String imgString = DatatypeConverter.printBase64Binary(blob);
                            out.print("<img style='border-radius: 10%;width:80px;height:110px;' src='data:image/png;base64," + imgString + "'/><br><br><font size=4>" + name + "</font>");
                        } else {
                            out.print("<img style='width:150px;height:150px;' src='images/student.png'/ alt='We couldn't find your Photo'><br><br><font size=4>" + name + "</font>");
                        }
                        ps = con.prepareStatement("SELECT \n"
                                + "MAX(CASE \n"
                                + "WHEN studentfingerprint.templateID = 1 and studentfingerprint.template is not null THEN ' 1 '\n"
                                + "WHEN studentfingerprint.templateID = 1 and studentfingerprint.template is null then '0'\n"
                                + "END) as Template1, \n"
                                + "MAX(CASE \n"
                                + "WHEN studentfingerprint.templateID = 2 and studentfingerprint.template is not null THEN ' 1 '\n"
                                + "WHEN studentfingerprint.templateID = 2 and studentfingerprint.template is null then '0'\n"
                                + "END) as Template2 \n"
                                + "FROM studentfingerprint \n"
                                + "where studentfingerprint.prn = ?");
                        ps.setString(1, prn);
                        out.print("<br>PRN : " + prn + "<br>Roll No : "+rollNo+"<br><br>");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            for (int i = 1; i < 3; i++) {
                                int flag;
                                if (Integer.parseInt(rs.getString(i).trim()) == 0) {
                                    flag = 0;
                                } else {
                                    flag = 1;
                                }
                                out.print("<div class='col-xl-10 col-sm-6 mb-3' align='center'>"
                                        + "<div class='card text-white bg-");
                                if (flag == 0) {
                                    out.print("danger ");
                                } else {
                                    out.print("success ");
                                }
                                out.print("small o-hidden h-60'>"
                                        + "<a class='card-header text-white clearfix'>"
                                        + "<span class='float-middle'>Fingerprint " + i + "</span>"
                                        + "</a>"
                                        + "<div class='card-body'>"
                                        + "<div class='card-body-icon'>"
                                        + "<i class=\"fas fa-fingerprint\"></i>"
                                        + "</div>"
                                        + "<div class='mr-1' align='center'><br>");
                                if (flag == 0) {
                                    out.print("Unavailable");

                                } else {
                                    out.print("Available");
                                }
                                out.print("<br><br></div>"
                                        + "</div></div>"
                                        + "</div>");
                            }
                        }
                        out.print("</div><div class='col-xl-8 col-sm-6 mb-3' align='center'><br><br><br><br>");
                        out.print(tablestart("<b>Subject Preferences</b>", "hover", "preferences", 0));
                        String header = "<tr>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Subject Code</th>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Abbreviation</th>";
                        header += "<th style='vertical-align : middle;text-align:center;'>Status</th>";
                        header += "</tr>";
                        out.print(tablehead(header));
                        int sem = getSem(oddEve(), classID);
                        subs = semSubs(sem, classID);
                        ps = con.prepareStatement("select subject.subjectID, subject.Abbreviation from studentsubject "
                                + "inner join subject "
                                + "on subject.subjectID=studentsubject.subjectID "
                                + "where PRN = ? and studentsubject.batchID!=0");
                        ps.setString(1, prn);
                        rs = ps.executeQuery();
                        int no_of_pref = 0;
                        while (rs.next()) {
                            no_of_pref++;
                        }
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
                                    + "<td style='vertical-align : middle;text-align:center;'>");
                            if (isfav(sub[0])) {
                                ps = con.prepareStatement("select (select batch.name from batch where batch.batchID = studentsubject.batchID) from studentsubject where prn = ? and subjectID = ?");
                                ps.setString(1, prn);
                                ps.setString(2, sub[0]);
                                rs = ps.executeQuery();
                                while (rs.next()) {
                                    out.print("<a href=\"javascript:setContent('/Cerberus/studSubAttendance?sub=" + sub[0] + "');\">"
                                            + rs.getString(1)
                                            + "</a>");
                                }
                            } else {
                                out.print("Not Opted");
                            }
                            out.print("</td><tr>");
                            index++;
                        }
                        if (index == 1) {
                            out.print("<h5>No Subjects Available</h5>");
                        }
                        out.print(tableend(null, 0) + "To change password, Click 'Create a new Password' in Login Page.");
                        con.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        errorLogger(e.getMessage());
                    }
                    break;
                default:
                    out.print(nouser());
            }
        }
    }

    public boolean isfav(String subject) {
        for (String sub : prefSub) {
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
