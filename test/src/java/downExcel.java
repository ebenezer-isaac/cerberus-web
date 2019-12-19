
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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

public class downExcel extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (OutputStream out = response.getOutputStream()) {

            XSSFWorkbook wb = new XSSFWorkbook();
            int no_of_classes = no_of_class();
            for (int classID = 1; classID <= no_of_classes; classID++) {
                int oddeve = oddEve();
                int sem = getSem(oddeve, classID);
                if (sem != 0) {
                    String subjects[][] = semSubs(sem, classID);
                    //Blank workbook
                    //Create a blank sheet
                    XSSFSheet sheet = wb.createSheet(getClassName(classID));
                    //This data needs to be written (Object[])
                    Map<String, Object[]> data;
                    data = new TreeMap<String, Object[]>();
                    String cla = getClassName(classID);
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        String[][] subs = semSubs(sem, classID);
                        for (int k = 0; k < subs.length; k++) {
                            System.out.println(subs[k][0]);
                            System.out.println(subs[k][1]);
                        }
                        int no_of_batch = no_of_batch();
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
                        System.out.println(sql);
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
                                    System.out.println("temp ::::::::::::::" + temp);
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
                                data.put("" + line, body);
                            }

                        } else {

                        }
                        con.close();
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        e.printStackTrace();
                    }

                    //Iterate over data and write to sheet
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
            try (FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\iamvr\\Desktop\\howtodoinjava_demo.xlsx"))) {
                wb.write(fos);
            }
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");

            String filename = "data.xlsx";
            String filepath = "C:\\Users\\iamvr\\Desktop\\howtodoinjava_demo.xlsx";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
                int j;
                while ((j = fileInputStream.read()) != -1) {
                    out.write(j);
                }
            }
            out.close();
        }
    }

    private Object[] appendValue(Object[] obj, Object newObj) {

        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();

    }

    public static int oddEve() throws FileNotFoundException, IOException {
        int result = 0;

        String fileName = "D:\\oddEve.txt";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String text;
        while ((text = br.readLine()) != null) {
            try {
                result = Integer.parseInt(text.trim());
                break;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        System.out.println("asdfasdf : " + result + ".");
        return result;
    }

    public static int no_of_batch() {
        int no_of_batch = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(batchID) FROM `batch` where batchID>0");
            while (rs.next()) {
                no_of_batch = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        return no_of_batch;
    }

    public static int no_of_class() {
        int no_of_class = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(classID) FROM `class`");
            while (rs.next()) {
                no_of_class = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return no_of_class;
    }

    public static float calPercentage(String prn, String subid, String batch) {
        prn = prn.trim();
        subid = subid.trim();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("select count(timetable.scheduleID) from timetable inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID where timetable.subjectID=? and batchid = ?");
            ps.setString(1, subid);
            ps.setString(2, batch);
            int labs = 0;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                labs = rs.getInt(1);
            }
            if (labs > 0) {
                ps = con.prepareStatement("select count(attendance.attendanceID) from timetable inner join attendance on attendance.scheduleID = timetable.scheduleID inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID where timetable.subjectID=? and batchid = ? and attendance.PRN = ?");
                ps.setString(1, subid);
                ps.setString(2, batch);
                ps.setString(3, prn);
                rs = ps.executeQuery();
                int presents = 0;
                while (rs.next()) {
                    presents = rs.getInt(1);
                }
                con.close();
                System.out.println("prn : " + prn);
                System.out.println("presents : " + presents);
                System.out.println("labs : " + labs);
                float perc = ((float) (presents) / (float) labs) * 100;
                System.out.println("perc : " + perc);
                return (perc);
            } else {
                return 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getClassName(int classID) {
        String cl = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps1 = con.prepareStatement("Select class from class where classID=?");
            ps1.setInt(1, classID);
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                cl = rs.getString(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return cl;
    }

    public static String[][] semSubs(int sem, int classID) {
        String semsubs[][] = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ? and classID = ?");
            ps.setInt(1, sem);
            ps.setInt(2, classID);
            ResultSet rs = ps.executeQuery();
            int no_of_sub = 0;
            while (rs.next()) {
                no_of_sub++;
            }
            rs.first();
            rs.previous();
            semsubs = new String[no_of_sub][2];
            no_of_sub--;
            int index = 0;
            while (rs.next()) {
                semsubs[index][0] = rs.getString(1);
                semsubs[index][1] = rs.getString(2);
                index++;
            }
            con.close();
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
        return semsubs;
    }

    public static int getSem(int oddeve, int classID) {
        switch (classID) {
            //FYBCA
            case 1:
                switch (oddeve) {
                    case 1:
                        return 1;
                    case 0:
                        return 2;
                    default:
                        return 0;
                }
            //SYBCA
            case 2:
                switch (oddeve) {
                    case 1:
                        return 3;
                    case 0:
                        return 4;
                    default:
                        return 0;
                }
            //TYBCA
            case 3:
                switch (oddeve) {
                    case 1:
                        return 5;
                    default:
                        return 0;

                }
            default:
                return 0;
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

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}