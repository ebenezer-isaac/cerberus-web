
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

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

    public static void new_week(int week) {
        int weekid = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps6 = con.prepareStatement("SELECT weekID FROM WEEK where week = ?");
            ps6.setInt(1, week);
            ResultSet rs = ps6.executeQuery();
            while (rs.next()) {
                weekid = rs.getInt(1);
            }
            if (weekid == 0) {
                PreparedStatement ps2 = con.prepareStatement("insert into week(`week`) values(?)");
                ps2.setInt(1, week);
                ps2.executeUpdate();
            }
            rs = ps6.executeQuery();
            while (rs.next()) {
                weekid = rs.getInt(1);
            }
            int labcount = 0;
            PreparedStatement ps8 = con.prepareStatement("SELECT count(labID) FROM lab");
            ps6.setInt(1, week);
            rs = ps8.executeQuery();
            while (rs.next()) {
                labcount = rs.getInt(1);
            }
            for (int i = 1; i <= labcount; i++) {
                PreparedStatement ps5 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                ps5.setInt(1, weekid);
                ps5.setInt(2, i);
                rs = ps5.executeQuery();
                int flag = 0;
                while (rs.next()) {
                    flag = 1;
                    break;
                }
                if (flag == 0) {
                    PreparedStatement ps10 = con.prepareStatement("SELECT weekID FROM `week` ORDER BY `week`.`weekID` DESC");
                    rs = ps10.executeQuery();
                    while (rs.next()) {
                        PreparedStatement ps9 = con.prepareStatement("SELECT * FROM timetable where weekID = ? and labID=?");
                        ps9.setInt(1, rs.getInt(1));
                        ps9.setInt(2, i);
                        ResultSet rs1 = ps9.executeQuery();
                        flag = 0;
                        while (rs1.next() && flag == 0) {
                            flag = 1;
                        }
                        PreparedStatement ps3 = con.prepareStatement("insert into timetable (slotID, labID, subjectID, batchID, weekID, dayID) select slotID, labID, subjectID, batchID, ?, dayID from timetable where weekID = ? and labID=?");
                        ps3.setInt(1, weekid);
                        ps3.setInt(2, rs.getInt(1));
                        ps3.setInt(3, i);
                        ps3.executeUpdate();
                    }
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
        }
    }

    public static float calpercentage(String prn, String subid) {
        String perc = "";
        System.out.println(prn);
        System.out.println(subid);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
            PreparedStatement ps = con.prepareStatement("select ((count(attendance.scheduleID)*100)/count(facultytimetable.scheduleID)) "
                    + "from student "
                    + "inner join rollcall "
                    + "on student.PRN = rollcall.PRN "
                    + "inner join attendance "
                    + "on attendance.PRN = student.PRN "
                    + "inner join studentsubject "
                    + "on studentsubject.PRN = student.PRN "
                    + "inner join timetable "
                    + "on timetable.scheduleID = attendance.scheduleID "
                    + "inner join facultytimetable "
                    + "on timetable.scheduleID = facultytimetable.scheduleID "
                    + "inner join subject "
                    + "on studentsubject.subjectID = subject.subjectID "
                    + "where timetable.subjectID=studentsubject.subjectID "
                    + "and facultytimetable.scheduleID = timetable.scheduleID "
                    + "and timetable.subjectID=? "
                    + " and student.PRN = ?");
            ps.setString(1, subid);
            ps.setString(2, prn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("hello");
                perc = rs.getString(1);
            }
            System.out.println(perc);
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
