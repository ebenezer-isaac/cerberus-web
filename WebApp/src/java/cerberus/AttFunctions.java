package cerberus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.Calendar;

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
            default:
                return 0;
        }
    }

    /*public static String get_next_schedule(HttpServletRequest request) {
       /* int week = getWeek(request);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String time = getCurrTime();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps4 = con.prepareStatement("SELECT slot.slotID,slot.startTime, slot.endTime, "
                    + "MAX(CASE WHEN dayID = ? THEN concat((select subject.abbreviation from subject where timetable.subjectID=subject.subjectID),' </br> ',(select batch.name from batch where timetable.batchID=batch.batchID),',',(select subject.classID from subject where timetable.subjectID=subject.subjectID),',',timetable.scheduleID) END) "
                    + "FROM timetable "
                    + "INNER JOIN slot "
                    + "ON timetable.slotID = slot.slotID "
                    + "where labID=? and weekID=(select weekID from week where week = ?) "
                    + "GROUP BY slot.startTime, slot.endTime ASC "
                    + "ORDER BY slot.startTime, slot.endTime ASC;");
            ps4.setInt(1, day);
            ResultSet nextSchedule = ps4.executeQuery();
            while (nextSchedule.next()) {
                
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return nextSchedule;
    }*/

    public boolean checkInternetConnection() {
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

        String schedule[] = new String[6];
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("select (STR_TO_DATE(concat(YEAR(CURDATE()),' ',timetable.weekID,' ',timetable.dayID),'%X %V %w')) as Date, \n"
                    + "(select slot.startTime from slot where slot.slotID = timetable.slotID) as startTime,\n"
                    + "(select slot.endTime from slot where slot.slotID = timetable.slotID) as endTime, \n"
                    + "(select lab.name from lab where lab.labID = timetable.labID) as Lab, \n"
                    + "(select subject.subjectID from subject where subject.subjectID = timetable.subjectID) as SubjectID, \n"
                    + "(select subject.subject from subject where subject.subjectID = timetable.subjectID) as Subject \n"
                    + "from timetable where scheduleID = ?");
            ps.setInt(1, scheduleID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int index = 0;
                while (index < 6) {
                    schedule[index] = rs.getString(index + 1);
                    index = index + 1;
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return schedule;
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

    public static int no_of_labs() {
        int no_of_labs = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(labID) FROM `lab`");
            while (rs.next()) {
                no_of_labs = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return no_of_labs;
    }

    public static int no_of_slots() {
        int no_of_slots = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(slotID) FROM `slot`");
            while (rs.next()) {
                no_of_slots = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return no_of_slots;
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

    public static String[] prefSubs(HttpServletRequest request, String user) {
        String prefsubs[] = null;
        int access = 0;
        if (user == null) {
            HttpSession session = request.getSession(false);
            access = (int) session.getAttribute("access");
            user = session.getAttribute("user").toString();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
                        + "where prn = ? and studentsubject.batchID not '0'");
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
            e.printStackTrace();
        }
        return prefsubs;
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

    public static String[][] oddEveSubs() throws IOException {
        int oddeve = oddEve();
        String subs[][] = null;
        int no_of_subs = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
            e.printStackTrace();
        }
        return subs;
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

    public static int getAccess(HttpServletRequest request) {
        int access = 0;
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
        int week = getWeek(request);
        session.setAttribute("week", week);
        session.setAttribute("access", 2);
    }

    public static int getWeek(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        int week;
        try {
            week = (int) session.getAttribute("week");
        } catch (Exception e) {
            java.util.Date date = new java.util.Date();
            SimpleDateFormat ft = new SimpleDateFormat("w");
            week = Integer.parseInt(ft.format(date));
        }
        return week;
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

    public static int getWeekID(int week, int year) {
        int weekID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT weekID FROM WEEK where week = ? and year =?");
            ps.setInt(1, week);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                weekID = rs.getInt(1);
            }
            if (weekID == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into week(`week`,`year`) values(?,?)");
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
            e.printStackTrace();
        }
        return weekID;
    }

    public static String getCurrDate() {
        Date date = new Date();
        return (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    }

    public static int getDateID(String date) {
        int dateID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
            e.printStackTrace();
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
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
            e.printStackTrace();
        }
        return timeID;
    }

    public static String[][] getSlots() {
        String slots[][] = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
            e.printStackTrace();
        }
        return slots;
    }
}
