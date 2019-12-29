import static cerberus.AttFunctions.errorLogger;
import static cerberus.AttFunctions.appendValue;
import static cerberus.AttFunctions.calPercentage;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.no_of_class;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class downPercAtt extends HttpServlet {

    private static final long serialVersionUID = -6221650315249888167L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int access = getAccess(request);
        switch (access) {
            case 1:
                try (OutputStream out = response.getOutputStream()) {
                    int criteria = Integer.parseInt(request.getParameter("criteria"));
                    XSSFWorkbook wb = new XSSFWorkbook();
                    int no_of_classes = no_of_class();
                    for (int classID = 1; classID <= no_of_classes; classID++) {
                        int oddeve = oddEve();
                        int sem = getSem(oddeve, classID);
                        if (sem != 0) {
                            XSSFSheet sheet = wb.createSheet(getClassName(classID));
                            Map<String, Object[]> data;
                            data = new TreeMap<String, Object[]>();
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                                String[][] subs = semSubs(sem, classID);
                                int no_of_sub = subs.length - 1;
                                int index = 0;
                                String sql = "SELECT student.PRN, rollcall.rollNo,student.name,";
                                while (index <= no_of_sub) {
                                    sql += "MAX(CASE WHEN studentsubject.subjectID = '" + subs[index][0] + "' THEN concat(studentsubject.batchID,',',studentsubject.subjectID) END) as '" + subs[index][1].replace("-", "_") + "'";
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
                                PreparedStatement ps = con.prepareStatement(sql);
                                ResultSet rs = ps.executeQuery();
                                ResultSetMetaData rsm = rs.getMetaData();
                                int cols = rsm.getColumnCount();
                                if (rs.next()) {
                                    int line = 1;
                                    Object[] head = new Object[]{"Roll", "PRN", "Name"};
                                    for (int i = 4; i <= cols; i++) {
                                        head = appendValue(head, rsm.getColumnLabel(i));
                                    }
                                    head = appendValue(head, "Average");
                                    data.put("1", head);
                                    rs.previous();
                                    while (rs.next()) {
                                        line++;
                                        Object[] body = new Object[]{rs.getString(2), rs.getString(1), rs.getString(3)};
                                        String prn = rs.getString(1);
                                        float total = 0;
                                        float count = 0;
                                        for (int i = 4; i <= cols; i++) {
                                            String result[] = rs.getString(i).split(",");
                                            ps = con.prepareStatement("select count(facultytimetable.scheduleID) from facultytimetable inner join timetable on facultytimetable.scheduleID = timetable.scheduleID where subjectID = ? and batchID = ?");
                                            ps.setString(1, result[1]);
                                            ps.setString(2, result[0]);
                                            ResultSet rs_subject = ps.executeQuery();
                                            int temp = 0;
                                            while (rs_subject.next()) {
                                                if (rs_subject.getInt(1) > 0) {
                                                    temp = 1;
                                                }
                                            }
                                            if (result[0].equals("0")) {
                                                body = appendValue(body, "N/A");

                                            } else if (temp == 0) {
                                                body = appendValue(body, "No Labs");
                                            } else {
                                                count++;
                                                float currPerc = calPercentage(prn, result[1], result[0]);
                                                total = total + currPerc;
                                                body = appendValue(body, String.format("%.02f", total) + "%");
                                            }
                                        }
                                        float perc = 0;
                                        if (count > 0) {
                                            perc = total / count;
                                        }
                                        if (perc < criteria) {
                                            body = appendValue(body, String.format("%.02f", perc) + "%");
                                            data.put("" + line, body);
                                        }
                                    }
                                } else {

                                }
                                con.close();
                            } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                                errorLogger(e.getMessage());
                            }
                            Set<String> keyset = data.keySet();
                            int rownum = 0;
                            for (String key : keyset) {
                                Row row = sheet.createRow(rownum++);
                                Object[] objArr = data.get(key);
                                int cellnum = 0;
                                for (Object obj : objArr) {
                                    Cell cell = row.createCell(cellnum++);
                                    if (obj instanceof String) {
                                        cell.setCellValue((String) obj);
                                    } else if (obj instanceof Integer) {
                                        cell.setCellValue((Integer) obj);
                                    }
                                }
                            }
                        }
                    }
                    try (FileOutputStream fos = new FileOutputStream(new File("D:\\AttendanceSystem\\temp.xlsx"))) {
                        wb.write(fos);
                    }
                    String filename = "Attendance.xlsx";
                    String filepath = "D:\\AttendanceSystem\\temp.xlsx";
                    response.setContentType("APPLICATION/OCTET-STREAM");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                    try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
                        int j;
                        while ((j = fileInputStream.read()) != -1) {
                            out.write(j);
                        }
                    }
                    File xls = new File("D:\\AttendanceSystem\\temp.xlsx");
                    xls.delete();
                    out.close();
                }
                break;
            case 0:
                messages b = new messages();
                b.kids(request, response);
                break;
            default:
                messages c = new messages();
                c.nouser(request, response);
                break;
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
