
import static cerberus.AttFunctions.appendValue;
import static cerberus.AttFunctions.calPercentage;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getCurrYear;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.getWeek;
import static cerberus.AttFunctions.no_of_class;
import static cerberus.AttFunctions.no_of_labs;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.messages;
import static cerberus.printer.tableend;
import static cerberus.printer.tablehead;
import static cerberus.printer.tablestart;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class downTimetable extends HttpServlet {

    private static final long serialVersionUID = 2480501928307475798L;
    String heading;
    int week = 0, year = 0;
    int temp = 0;
    String[] subs;
    int access;
    int classID = 0;
    HttpServletResponse response;
    HttpServletRequest request;
    int no_of_class;
    LocalDate wks, mon, tue, wed, thu, fri, sat, wke;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        access = getAccess(request);
        if (access == 1) {
            try (OutputStream out = response.getOutputStream()) {
                XSSFWorkbook wb = new XSSFWorkbook();
                Map<String, Object[]> data;
                data = new TreeMap<String, Object[]>();
                try {
                    week = Integer.parseInt(request.getParameter("week"));
                    year = Integer.parseInt(request.getParameter("year"));
                } catch (NumberFormatException e) {
                    week = getWeek(request);
                    year = getCurrYear();
                }
                access = getAccess(request);
                LocalDate date = LocalDate.now()
                        .withYear(year) // year
                        .with(WeekFields.ISO.weekOfWeekBasedYear(), week) // week of year
                        .with(WeekFields.ISO.dayOfWeek(), 7);
                wks = date.plusDays(-7);
                mon = wks.plusDays(1);
                tue = wks.plusDays(2);
                wed = wks.plusDays(3);
                thu = wks.plusDays(4);
                fri = wks.plusDays(5);
                sat = wks.plusDays(6);
                wke = wks.plusDays(7);
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                    int no_of_lab = no_of_labs();
                    String slots[][];
                    int no_of_slots = 0;
                    PreparedStatement ps7 = con.prepareStatement("SELECT * from slot");
                    ResultSet rs1 = ps7.executeQuery();

                    while (rs1.next()) {
                        no_of_slots++;
                    }
                    rs1.first();
                    rs1.previous();
                    slots = new String[no_of_slots][2];
                    no_of_slots = 0;
                    while (rs1.next()) {
                        slots[no_of_slots][0] = rs1.getString(2).substring(0, 5);
                        slots[no_of_slots][1] = rs1.getString(3).substring(0, 5);
                        no_of_slots++;
                    }
                    System.out.println(no_of_slots);
                    no_of_slots--;
                    for (int labid = 1; labid <= no_of_lab; labid++) {
                        Object[][] lines;
                        lines = new Object[no_of_slots + 1][0];
                        XSSFSheet sheet = wb.createSheet("Lab - " + labid);
                        Object[] head = new Object[]{"Start Time", "End Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                        data.put("1", head);
                        String sql = "SELECT slot.slotID,slot.startTime, slot.endTime, "
                                + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Monday, "
                                + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Tuesday, "
                                + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Wednesday, "
                                + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Thursday, "
                                + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Friday, "
                                + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Saturday "
                                + "FROM timetable "
                                + "INNER JOIN slot "
                                + "ON timetable.slotID = slot.slotID "
                                + "inner join subject "
                                + "on timetable.subjectID = subject.subjectID "
                                + "where labID=? and weekID=(select weekID from week where week = ? and year=?) "
                                + "GROUP BY slot.startTime, slot.endTime ASC "
                                + "ORDER BY slot.startTime, slot.endTime ASC;";
                        PreparedStatement ps4 = con.prepareStatement(sql);
                        ps4.setInt(1, labid);
                        ps4.setInt(2, week);
                        ps4.setInt(3, year);
                        ResultSet lab1 = ps4.executeQuery();
                        while (lab1.next()) {
                            System.out.println(lab1.getInt(1));
                            lines[lab1.getInt(1) - 1] = appendValue(lines[lab1.getInt(1) - 1], slots[lab1.getInt(1) - 1][0]);
                            lines[lab1.getInt(1) - 1] = appendValue(lines[lab1.getInt(1) - 1], slots[lab1.getInt(1) - 1][1]);
                            for (int j = 1; j <= 6; j++) {
                                if (lab1.getString(j + 3) != null) {
                                    String[] arrOfStr = lab1.getString(j + 3).split(",");
                                    lines[lab1.getInt(1) - 1] = appendValue(lines[lab1.getInt(1) - 1], lab1.getString(j + 3));
                                } else {
                                    lines[lab1.getInt(1) - 1] = appendValue(lines[lab1.getInt(1) - 1], "-");
                                }
                                temp++;
                            }
                        }
                        for (int y = 0; y <= no_of_slots; y++) {
                            System.out.println(y + " : " + lines[y].length);
                            if (lines[y].length == 0) {
                                lines[y] = appendValue(lines[y], slots[y][0]);
                                lines[y] = appendValue(lines[y], slots[y][1]);
                                for (int j = 1; j <= 6; j++) {
                                    lines[y] = appendValue(lines[y], "-");
                                    temp++;
                                }
                            }
                            data.put(y + 2 + "", lines[y]);
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
                    XSSFSheet sheet = wb.createSheet("Combined");
                    data = new TreeMap<String, Object[]>();
                    String labs[][] = new String[no_of_lab][(no_of_slots + 2)];
                    for (int l = 0; l <= no_of_lab - 1; l++) {
                        String sql = "SELECT slot.slotID,slot.startTime, slot.endTime, "
                                + "MAX(CASE WHEN dayID = 1 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Monday, "
                                + "MAX(CASE WHEN dayID = 2 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Tuesday, "
                                + "MAX(CASE WHEN dayID = 3 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Wednesday, "
                                + "MAX(CASE WHEN dayID = 4 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Thursday, "
                                + "MAX(CASE WHEN dayID = 5 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Friday, "
                                + "MAX(CASE WHEN dayID = 6 THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),'-',(select batch.name from batch where timetable.batchID=batch.batchID)) END) as Saturday "
                                + "FROM timetable "
                                + "INNER JOIN slot "
                                + "ON timetable.slotID = slot.slotID "
                                + "inner join subject "
                                + "on timetable.subjectID = subject.subjectID "
                                + "where labID=? and weekID=(select weekID from week where week = ? and year = ?)"
                                + "GROUP BY slot.startTime, slot.endTime ASC "
                                + "ORDER BY slot.startTime, slot.endTime ASC;";
                        PreparedStatement ps4 = con.prepareStatement(sql);
                        ps4.setInt(1, l + 1);
                        ps4.setInt(2, week);
                        ps4.setInt(3, year);
                        ResultSet lab1 = ps4.executeQuery();
                        for (int y = 0; y <= no_of_slots; y++) {
                            labs[l][y] = "";
                        }
                        while (lab1.next()) {
                            labs[l][lab1.getInt(1) - 1] = "";
                            for (int j = 1; j <= 6; j++) {
                                if (lab1.getString(j + 3) != null) {
                                    labs[l][lab1.getInt(1) - 1] += (lab1.getString(j + 3));
                                } else {
                                    labs[l][lab1.getInt(1) - 1] += ("-");
                                }
                                if (j != 6) {
                                    labs[l][lab1.getInt(1) - 1] += ",";
                                }
                                temp++;
                            }
                        }
                        System.out.println(no_of_slots);
                        for (int y = 0; y <= no_of_slots; y++) {
                            System.out.println(y);
                            if (labs[l][y].equals("")) {
                                labs[l][y] = "";
                                for (int j = 1; j <= 6; j++) {
                                    labs[l][y] += ("-");
                                    temp++;
                                    if (j != 6) {
                                        labs[l][y] += ",";
                                    }
                                }
                            }
                        }

                    }

                    int slot = 0;

                    Object[] head = new Object[]{"Start Time", "End Time", "Lab", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                    data.put("1", head);
                    int line = 2;
                    while (slot <= no_of_slots) {
                        for (int lab = 0; lab <= no_of_lab - 1; lab++) {
                            Object[] combined = {};
                            combined = appendValue(combined, slots[slot][0]);
                            combined = appendValue(combined, slots[slot][1]);
                            combined = appendValue(combined, "Lab " + (lab + 1));
                            String labdets = labs[lab][slot];
                            System.out.println("labdets : " + labdets);
                            String labsplit[] = labdets.split(",");
                            for (int j = 0; j <= 5; j++) {
                                combined = appendValue(combined, labsplit[j]);
                            }
                            data.put(line + "", combined);
                            line++;
                        }
                        slot++;

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

                    con.close();
                } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                    e.printStackTrace();
                }

                try (FileOutputStream fos = new FileOutputStream(new File("D:\\temp.xlsx"))) {
                    wb.write(fos);
                }
                String filename = "Timetable.xlsx";
                String filepath = "D:\\temp.xlsx";
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
                    int j;
                    while ((j = fileInputStream.read()) != -1) {
                        out.write(j);
                    }
                }
                File xls = new File("D:\\temp.xlsx");
                xls.delete();
                out.close();

            }
        } else if (access == 0) {
            messages b = new messages();
            b.kids(request, response);
        } else {
            messages c = new messages();
            c.nouser(request, response);
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
