
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class downTemplate extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (OutputStream out = response.getOutputStream()) {

            int no_of_classes = no_of_class();
            System.out.println(no_of_classes);
            //Blank workbook
            XSSFWorkbook wb = new XSSFWorkbook();

            //Create a blank sheet
            XSSFSheet sheet = wb.createSheet("BCA FY");
            //This data needs to be written (Object[])
            Map<String, Object[]> data;
            data = new TreeMap<String, Object[]>();

            Object[] head = new Object[]{"Roll", "PRN", "Name", "Email"};

            data.put("1", head);

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

            try (FileOutputStream fos = new FileOutputStream(new File("D:\\AttendanceSystem\\temp.xlsx"))) {
                wb.write(fos);
            }
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");

            String filename = "data.xlsx";
            String filepath = "D:\\AttendanceSystem\\temp.xlsx";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
                int k;
                while ((k = fileInputStream.read()) != -1) {
                    out.write(k);
                }
            }
            File xls = new File("D:\\AttendanceSystem\\temp.xlsx");
            xls.delete();
            out.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Object[] appendValue(Object[] obj, Object newObj) {

        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();

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
