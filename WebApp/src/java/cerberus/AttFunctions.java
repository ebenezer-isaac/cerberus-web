package cerberus;

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
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    public static float calPercentage(String prn, String subid) {
        prn = prn.trim();
        subid = subid.trim();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("select ((count(attendance.scheduleID)*100)/count(facultytimetable.scheduleID)) from timetable inner join attendance on attendance.scheduleID = timetable.scheduleID inner join facultytimetable on timetable.scheduleID = facultytimetable.scheduleID where timetable.subjectID=? and attendance.PRN = ?");
            ps.setString(1, subid);
            ps.setString(2, prn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getFloat(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
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
        }
        return no_of_class;
    }

    public static int no_of_batch() {
        int no_of_batch = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(batchID) FROM `batch`");
            while (rs.next()) {
                no_of_batch = rs.getInt(1);
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
        }
        return no_of_batch;
    }

    public static String[] prefSubs(HttpServletRequest request) {
        String prefsubs[] = null;
        HttpSession session = request.getSession();
        int access = (int) session.getAttribute("access");
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
                ps = con.prepareStatement("select subject.Abbreviation from facultysubject "
                        + "inner join subject "
                        + "on subject.subjectID=facultysubject.subjectID "
                        + "where facultyID = ?");
            }
            ps.setString(1, session.getAttribute("user").toString());
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
        }
        return prefsubs;
    }

    public static int oddEve(HttpServletRequest request) {
        int oddeve = 0;
        HttpSession session = request.getSession();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement st = con.prepareStatement("SELECT `sem` FROM `subject` where subjectID=(select max(subjectID) from timetable where weekID=(select weekID from week where week = ?)) ");
            st.setInt(1, Integer.parseInt(session.getAttribute("week").toString()));
            ResultSet rs2 = st.executeQuery();
            while (rs2.next()) {
                oddeve = (rs2.getInt(1) % 2);
            }
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
        }
        return oddeve;
    }

    public static String[][] oddEveSubs(int oddeve) {
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
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
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
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
        }
        return semsubs;
    }

    public static int getAccess(HttpServletRequest request) {
        int access = 0;
        HttpSession session = request.getSession(false);
        try {
            access = (int) session.getAttribute("access");
        } catch (Exception e) {
            createSession(request);
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
        int week = 0;
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
        } catch (ClassNotFoundException | SQLException e) {
        }
        return cl;
    }

    public static int getWeekID(int week) {
        int weekID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT weekID FROM WEEK where week = ?");
            ps.setInt(1, week);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                weekID = rs.getInt(1);
            }
            if (weekID == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into week(`week`) values(?)");
                ps2.setInt(1, week);
                ps2.executeUpdate();
                rs = ps.executeQuery();
                while (rs.next()) {
                    weekID = rs.getInt(1);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
        return weekID;
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
        } catch (ClassNotFoundException | SQLException e) {
        }
        return slots;
    }
}
