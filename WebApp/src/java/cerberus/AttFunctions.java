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

    public static float calpercentage(String prn, String subid) {
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

}
