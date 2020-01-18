package cerberus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import org.threeten.extra.YearWeek;

public class AttFunctions {

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
            //MSc IT FY
            case 4:
                switch (oddeve) {
                    case 1:
                        return 1;
                    default:
                        return 2;

                }
            //MSc IT SY
            case 5:
                switch (oddeve) {
                    case 1:
                        return 3;
                    default:
                        return 0;

                }
            default:
                return 0;
        }
    }

    public static String[] get_next_schedule(HttpServletRequest request, int labid) {
        String nextSchedule[] = new String[2];
        String weekYear[] = getCurrWeekYear().split(",");
        int week = Integer.parseInt(weekYear[1]);
        int year = Integer.parseInt(weekYear[0]);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String time = getCurrTime();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = ? THEN timetable.scheduleID END) "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "INNER JOIN subject "
                    + "ON timetable.subjectID = subject.subjectID "
                    + "where labID=? and weekID=(select weekID from week where week = ? and year = ?) "
                    + "GROUP BY slot.startTime, slot.endTime ASC "
                    + "ORDER BY slot.startTime, slot.endTime ASC;");
            ps.setInt(1, day);
            ps.setInt(2, labid);
            ps.setInt(3, week);
            ps.setInt(4, year);
            ResultSet rs = ps.executeQuery();
            int slots = no_of_slots();
            String schedule[][] = new String[slots][4];
            int count = 0;
            int nullcount = 0;
            while (rs.next()) {
                if (rs.getString(4) == null) {
                    nullcount++;
                }
                schedule[count][0] = rs.getString(1);
                schedule[count][1] = rs.getString(2);
                schedule[count][2] = rs.getString(3);
                schedule[count][3] = rs.getString(4);
                count++;
            }
            if (count > 0 && count > nullcount) {
                long startmill = 0;
                long timemill = 0;
                long endmill = 0;
                for (int x = 0; x < slots; x++) {
                    try {
                        String startDate = "2014/10/29 " + schedule[x][1];
                        String timeDate = "2014/10/29 " + time;
                        String endDate = "2014/10/29 " + schedule[x][2];
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date startD = sdf.parse(startDate);
                        Date timeD = sdf.parse(timeDate);
                        Date endD = sdf.parse(endDate);
                        startmill = startD.getTime();
                        timemill = timeD.getTime();
                        endmill = endD.getTime();
                    } catch (Exception e) {
                    }
                    if (startmill < timemill && timemill < endmill) {
                        nextSchedule[0] = "2";
                        if (schedule[x][3] != null) {
                            String details[] = get_schedule_det(Integer.parseInt(schedule[x][3]));
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(schedule[x][1]);
                                schedule[x][1] = (new SimpleDateFormat("K:mm a").format(dateObj));
                            } catch (final ParseException e) {
                                errorLogger(e.getMessage());
                            }
                            int studs = 0;
                            ps = con.prepareStatement("SELECT count(attendance.attendanceID) from attendance where scheduleID = ?");
                            ps.setString(1, schedule[x][3]);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                studs = rs.getInt(1);
                            }
                            nextSchedule[1] = "Lab Started at " + schedule[x][1] + "<br>"
                                    + "Class : " + getClassName(Integer.parseInt(details[7])) + "<br>"
                                    + "Subject : " + details[5] + "<br>"
                                    + "Batch : " + details[6] + "," + schedule[x][3] + "," + studs;
                            con.close();
                            con.close();
                            return nextSchedule;
                        }
                    }
                }
                for (int x = 0; x < slots; x++) {
                    if (schedule[x][3] != null) {
                        try {
                            String startDate = "2014/10/29 " + schedule[x][1];
                            String timeDate = "2014/10/29 " + time;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date startD = sdf.parse(startDate);
                            Date timeD = sdf.parse(timeDate);
                            startmill = startD.getTime();
                            timemill = timeD.getTime();
                        } catch (ParseException e) {
                        }
                        if (timemill < startmill) {
                            nextSchedule[0] = "3";
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(schedule[x][1]);
                                schedule[x][1] = (new SimpleDateFormat("K:mm a").format(dateObj));
                            } catch (final ParseException e) {
                                errorLogger(e.getMessage());
                            }
                            String details[] = get_schedule_det(Integer.parseInt(schedule[x][3]));
                            nextSchedule[1] = "Next Lab starts at " + schedule[x][1] + "<br>"
                                    + "Class : " + getClassName(Integer.parseInt(details[7])) + "<br>"
                                    + "Subject : " + details[5] + "<br>"
                                    + "Batch : " + details[6];
                            con.close();
                            return nextSchedule;
                        }
                    }
                }
                nextSchedule[0] = "1";
                nextSchedule[1] = "All Labs Over";
                con.close();
                return nextSchedule;
            } else {
                nextSchedule[0] = "0";
                nextSchedule[1] = "No Labs Today";
                con.close();
                return nextSchedule;
            }

        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return nextSchedule;
    }

    public static String[] get_next_stud_schedule(HttpServletRequest request, int labid, String prn) {
        String nextSchedule[] = new String[2];
        String weekYear[] = getCurrWeekYear().split(",");
        int week = Integer.parseInt(weekYear[1]);
        int year = Integer.parseInt(weekYear[0]);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String time = getCurrTime();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = ? THEN timetable.scheduleID END) "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "INNER JOIN subject "
                    + "ON timetable.subjectID = subject.subjectID "
                    + "inner join studentsubject on "
                    + "timetable.subjectID = studentsubject.subjectID "
                    + "where labID=? and weekID=(select weekID from week where week = ? and year = ?) and timetable.subjectID in "
                    + "(select subjectID from studentsubject "
                    + "where prn = ? and batchID != 0) "
                    + "and studentsubject.batchID = timetable.batchID "
                    + "and studentsubject.prn = ? "
                    + "and studentsubject.batchID !=0 "
                    + "GROUP BY slot.startTime, slot.endTime ASC "
                    + "ORDER BY slot.startTime, slot.endTime ASC;");
            ps.setInt(1, day);
            ps.setInt(2, labid);
            ps.setInt(3, week);
            ps.setInt(4, year);
            ps.setString(5, prn);
            ps.setString(6, prn);
            ResultSet rs = ps.executeQuery();
            int slots = no_of_slots();
            String schedule[][] = new String[slots][4];
            int count = 0;
            int nullcount = 0;
            while (rs.next()) {
                if (rs.getString(4) == null) {
                    nullcount++;
                }
                schedule[count][0] = rs.getString(1);
                schedule[count][1] = rs.getString(2);
                schedule[count][2] = rs.getString(3);
                schedule[count][3] = rs.getString(4);
                count++;
            }
            if (count > 0 && count > nullcount) {
                long startmill = 0;
                long timemill = 0;
                long endmill = 0;
                for (int x = 0; x < slots; x++) {
                    try {
                        String startDate = "2014/10/29 " + schedule[x][1];
                        String timeDate = "2014/10/29 " + time;
                        String endDate = "2014/10/29 " + schedule[x][2];
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date startD = sdf.parse(startDate);
                        Date timeD = sdf.parse(timeDate);
                        Date endD = sdf.parse(endDate);
                        startmill = startD.getTime();
                        timemill = timeD.getTime();
                        endmill = endD.getTime();
                    } catch (Exception e) {
                    }
                    if (startmill < timemill && timemill < endmill) {
                        nextSchedule[0] = "2";
                        if (schedule[x][3] != null) {
                            String details[] = get_schedule_det(Integer.parseInt(schedule[x][3]));
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(schedule[x][1]);
                                schedule[x][1] = (new SimpleDateFormat("K:mm a").format(dateObj));
                            } catch (final ParseException e) {
                                errorLogger(e.getMessage());
                            }
                            int mark = 0;
                            String marked = "Not Marked as Present";
                            ps = con.prepareStatement("SELECT attendance.attendanceID from attendance where scheduleID = ? and prn = ?");
                            ps.setString(1, schedule[x][3]);
                            ps.setString(2, prn);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                mark = 1;
                                marked = "Marked as Present";
                            }
                            nextSchedule[1] = "Lab Started at " + schedule[x][1] + "<br>"
                                    + "Class : " + getClassName(Integer.parseInt(details[7])) + "<br>"
                                    + "Subject : " + details[5] + "<br>"
                                    + "Batch : " + details[6] + "<br>"
                                    + "Status : " + marked + "," + mark;
                            con.close();
                            return nextSchedule;
                        }
                    }
                }
                for (int x = 0; x < slots; x++) {
                    if (schedule[x][3] != null) {
                        try {
                            String startDate = "2014/10/29 " + schedule[x][1];
                            String timeDate = "2014/10/29 " + time;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date startD = sdf.parse(startDate);
                            Date timeD = sdf.parse(timeDate);
                            startmill = startD.getTime();
                            timemill = timeD.getTime();
                        } catch (ParseException e) {
                        }
                        if (timemill < startmill) {
                            nextSchedule[0] = "3";
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(schedule[x][1]);
                                schedule[x][1] = (new SimpleDateFormat("K:mm a").format(dateObj));
                            } catch (final ParseException e) {
                                errorLogger(e.getMessage());
                            }
                            String details[] = get_schedule_det(Integer.parseInt(schedule[x][3]));
                            nextSchedule[1] = "Next Lab starts at " + schedule[x][1] + "<br>"
                                    + "Class : " + getClassName(Integer.parseInt(details[7])) + "<br>"
                                    + "Subject : " + details[5] + "<br>"
                                    + "Batch : " + details[6];
                            con.close();
                            return nextSchedule;
                        }
                    }
                }
                nextSchedule[0] = "1";
                nextSchedule[1] = "All Labs Over";
                con.close();
                return nextSchedule;
            } else {
                nextSchedule[0] = "0";
                nextSchedule[1] = "No Labs Today";
                con.close();
                return nextSchedule;
            }
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return nextSchedule;
    }

    public static boolean checkInternetConnection() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static void errorLogger(String errorMessage) {
        String text = "\n" + getCurrDate() + " - " + getCurrTime() + "\n" + errorMessage + "\n";
        try {
            Files.write(Paths.get("D:\\AttendanceSystem\\ErrorLog.txt"), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }

    public static void dbLog(String comment) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("insert into log values(null,?,?,?)");
            int dateID = getDateID(getCurrDate());
            int timeID = getTimeID(getCurrTime());
            ps.setInt(1, dateID);
            ps.setInt(2, timeID);
            ps.setString(3, comment);
            ps.executeUpdate();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
            errorLogger(e.getMessage());
        }

    }

    public static String currUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        try {
            return session.getAttribute("name").toString();
        } catch (Exception e) {
            return "False";
        }
    }

    public static String nameProcessor(String str) {
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            } else if (ch[i] >= 'A' && ch[i] <= 'Z') {
                ch[i] = (char) (ch[i] + 'a' - 'A');
            }
        }
        String st = new String(ch);
        return st;
    }

    public static String[] get_schedule_det(int scheduleID) {

        String schedule[] = new String[9];
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("select (select week.year from week where timetable.weekID = week.weekID),(select week.week from week where timetable.weekID = week.weekID),timetable.dayID,     \n"
                    + "    (select slot.startTime from slot where slot.slotID = timetable.slotID) ,    \n"
                    + "    (select slot.endTime from slot where slot.slotID = timetable.slotID),     \n"
                    + "    (select lab.name from lab where lab.labID = timetable.labID),     \n"
                    + "    (select subject.subjectID from subject where subject.subjectID = timetable.subjectID),     \n"
                    + "    (select subject.subject from subject where subject.subjectID = timetable.subjectID),     \n"
                    + "    (select batch.name from batch where batch.batchID = timetable.batchID),     \n"
                    + "    (select subject.classID from subject where subject.subjectID = timetable.subjectID),      \n"
                    + "    (select subject.abbreviation from subject where subject.subjectID = timetable.subjectID)      \n"
                    + "    from timetable where scheduleID = ?");
            ps.setInt(1, scheduleID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int index = 1;
                LocalDate date = LocalDate.now()
                        .with(WeekFields.ISO.weekBasedYear(), rs.getInt(1)) // year
                        .with(WeekFields.ISO.weekOfWeekBasedYear(), rs.getInt(2)) // week of year
                        .with(WeekFields.ISO.dayOfWeek(), rs.getInt(3));
                schedule[0] = date + "";
                while (index < 9) {
                    schedule[index] = rs.getString(index + 3);
                    index = index + 1;
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return schedule;
    }

    public static Object[] appendValue(Object[] obj, Object newObj) {

        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();

    }

    public static String hashIt(String raw) throws NoSuchAlgorithmException {
        raw = raw + "msubca";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger number = new BigInteger(1, md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String trimSQLInjection(String str) {
        String temp = str;
        temp = temp.replaceAll("\\s+", "");
        temp = temp.replaceAll("[A-Za-z0-9]+", "");
        temp = temp.replaceAll("\"", "'");
        return (temp);
    }

    public static String generateOTP() throws NoSuchAlgorithmException {
        String otpchars = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) {
            int index = (int) (rnd.nextFloat() * otpchars.length());
            salt.append(otpchars.charAt(index));
        }
        return salt.toString();
    }

    public static String generatePassword() throws NoSuchAlgorithmException {
        String otpchars = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * otpchars.length());
            salt.append(otpchars.charAt(index));
        }
        return salt.toString();
    }

    public static float calPercentage(String prn, String subid, String batch) {
        prn = prn.trim();
        subid = subid.trim();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
                float perc = ((float) (presents) / (float) labs) * 100;
                return (perc);
            } else {
                return 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return 0;
    }

    public static int no_of_labs() {
        int no_of_labs = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(labID) FROM `lab`");
            while (rs.next()) {
                no_of_labs = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return no_of_labs;
    }

    public static int no_of_slots() {
        int no_of_slots = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(slotID) FROM `slot`");
            while (rs.next()) {
                no_of_slots = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return no_of_slots;
    }

    public static int no_of_class() {
        int no_of_class = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(classID) FROM `class`");
            while (rs.next()) {
                no_of_class = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return no_of_class;
    }

    public static int no_of_batch() {
        int no_of_batch = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(batchID) FROM `batch` where batchID>0");
            while (rs.next()) {
                no_of_batch = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
            errorLogger(e.getMessage());
        }
        return no_of_batch;
    }

    public static int get_class_from_sub(String subjectID) {
        int classID = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement stmt = con.prepareStatement("select classId from subject where subjectID = ?");
            stmt.setString(1, subjectID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classID = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return classID;
    }

    public static String[][] prefStudSubs(String prn) {
        String prefsubs[][] = null;
        int access = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("select subject.subjectID, studentsubject.batchID, subject.abbreviation from studentsubject "
                    + "inner join subject "
                    + "on subject.subjectID=studentsubject.subjectID "
                    + "where prn = ? and studentsubject.batchID != 0");
            ps.setString(1, prn);
            ResultSet rs = ps.executeQuery();
            int no_of_pref = 0;
            while (rs.next()) {
                no_of_pref++;
            }
            prefsubs = new String[no_of_pref][3];
            rs.first();
            rs.previous();
            no_of_pref = 0;
            while (rs.next()) {
                prefsubs[no_of_pref][0] = rs.getString(1);
                prefsubs[no_of_pref][1] = rs.getString(2);
                prefsubs[no_of_pref][2] = rs.getString(3);
                no_of_pref++;
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return prefsubs;
    }

    public static String[] prefSubs(HttpServletRequest request, String user) {
        String prefsubs[] = null;
        int access = 0;
        if (user == null) {
            HttpSession session = request.getSession(false);
            access = (int) session.getAttribute("access");
            user = session.getAttribute("user").toString();
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps;
            if (access == 1) {
                ps = con.prepareStatement("select subject.Abbreviation from facultysubject "
                        + "inner join subject "
                        + "on subject.subjectID=facultysubject.subjectID "
                        + "where facultyID = ?");
            } else {
                ps = con.prepareStatement("select subject.Abbreviation from studentsubject "
                        + "inner join subject "
                        + "on subject.subjectID=studentsubject.subjectID "
                        + "where prn = ? and studentsubject.batchID != 0");
            }
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            int no_of_pref = 0;
            while (rs.next()) {
                no_of_pref++;
            }
            prefsubs = new String[no_of_pref];
            rs.first();
            rs.previous();
            no_of_pref = 0;
            while (rs.next()) {
                prefsubs[no_of_pref] = rs.getString(1);
                no_of_pref++;
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return prefsubs;
    }

    public static int oddEve() throws FileNotFoundException, IOException {
        int result = 0;

        String fileName = "D:\\AttendanceSystem\\oddEve.txt";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String text;
        while ((text = br.readLine()) != null) {
            try {
                result = Integer.parseInt(text.trim());
                break;
            } catch (NumberFormatException e) {
                errorLogger(e.getMessage());
            }
        }
        return result;
    }

    public static String[][] oddEveSubs() throws IOException {
        int oddeve = oddEve();
        String subs[][] = null;
        int no_of_subs = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT subjectID,`Abbreviation` FROM `subject` where `sem` in(?,?,?) ORDER BY `subject`.`Abbreviation` ASC;");
            ps.setInt(1, oddeve);
            ps.setInt(2, oddeve + 2);
            ps.setInt(3, oddeve + 4);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                no_of_subs++;
            }
            rs.first();
            rs.previous();
            subs = new String[no_of_subs][2];
            no_of_subs = 0;
            while (rs.next()) {
                subs[no_of_subs][0] = rs.getString(1);
                subs[no_of_subs][1] = rs.getString(2);
                no_of_subs++;
            }
            con.close();
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return subs;
    }

    public static String[][] semSubs(int sem, int classID) {
        String semsubs[][] = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
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
            errorLogger(e.getMessage());
        }
        return semsubs;
    }

    public static int getAccess(HttpServletRequest request) {
        int access;
        HttpSession session = request.getSession(false);
        try {
            access = (int) session.getAttribute("access");
        } catch (Exception e) {
            HttpSession sess = request.getSession(true);
            java.util.Date date = new java.util.Date();
            SimpleDateFormat ft = new SimpleDateFormat("w");
            int week = Integer.parseInt(ft.format(date));
            sess.setAttribute("week", week);
            sess.setAttribute("access", 2);
            access = 2;
        }
        return access;
    }

    public static void createSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("access", 2);
    }

    public static String getCurrWeekYear() {
        ZoneId z = ZoneId.of("Asia/Kolkata");
        YearWeek currentWeek = YearWeek.now(z);
        String result[] = (currentWeek + "").split("-W");
        return result[0] + "," + result[1];
    }

    public static String getPrevWeekYear(int week, int year) {
        ZoneId z = ZoneId.of("Asia/Kolkata");
        YearWeek currentWeek = YearWeek.of(year, week);
        currentWeek = currentWeek.minusWeeks(1);
        String result[] = (currentWeek + "").split("-W");
        return result[0] + "," + result[1];
    }

    public static String getNextWeekYear(int week, int year) {
        ZoneId z = ZoneId.of("Asia/Kolkata");
        YearWeek currentWeek = YearWeek.of(year, week);
        currentWeek = currentWeek.plusWeeks(1);
        String result[] = (currentWeek + "").split("-W");
        return result[0] + "," + result[1];
    }

    public static String getClassName(int classID) {
        String cl = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps1 = con.prepareStatement("Select class from class where classID=?");
            ps1.setInt(1, classID);
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                cl = rs.getString(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return cl;
    }

    public static int getWeekID(int week, int year) {
        int weekID = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT weekID FROM WEEK where week = ? and year =?");
            ps.setInt(1, week);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                weekID = rs.getInt(1);
            }
            if (weekID >= 0) {
            } else {
                PreparedStatement ps2 = con.prepareStatement("insert into week values(null,?,?)");
                ps2.setInt(1, week);
                ps2.setInt(2, year);
                ps2.executeUpdate();
                rs = ps.executeQuery();
                while (rs.next()) {
                    weekID = rs.getInt(1);
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return weekID;
    }

    public static String getCurrDate() {
        Date date = new Date();
        return (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    }

    public static int getCurrYear() {
        Date date = new Date();
        return (date.getYear() + 1900);
    }

    public static int getDateID(String date) {
        int dateID = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT dateID FROM datedata where date = ?");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dateID = rs.getInt(1);
            }
            if (dateID == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into datedata(`date`) values(?)");
                ps2.setString(1, date);
                ps2.executeUpdate();
                rs = ps.executeQuery();
                while (rs.next()) {
                    dateID = rs.getInt(1);
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return dateID;
    }

    public static String getCurrTime() {
        Date date = new Date();
        return String.format("%02d", date.getHours()) + ":" + String.format("%02d", date.getMinutes()) + ":" + String.format("%02d", date.getSeconds());
    }

    public static int getTimeID(String time) {
        int timeID = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps = con.prepareStatement("SELECT timeID FROM timedata where time = ?");
            ps.setString(1, time);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                timeID = rs.getInt(1);
            }
            if (timeID == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into timedata(`time`) values(?)");
                ps2.setString(1, time);
                ps2.executeUpdate();
                rs = ps.executeQuery();
                while (rs.next()) {
                    timeID = rs.getInt(1);
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return timeID;
    }

    public static String[][] getSlots() {
        String slots[][] = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
            PreparedStatement ps7 = con.prepareStatement("SELECT * from slot");
            ResultSet rs1 = ps7.executeQuery();
            int no_of_slots = 0;
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
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            errorLogger(e.getMessage());
        }
        return slots;
    }
}
