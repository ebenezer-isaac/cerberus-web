
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class bank extends HttpServlet implements Runnable {

    String log;
    int logid;
    public bank() {

    }

    public bank(String log) {
        this.log = log;
        logid = 0;
        String deposit = "";
        try (Scanner file = new Scanner(new File("D:\\\\log.txt"))) {
            String line;
            deposit = "";
            while (file.hasNext()) {
                logid++;
                line = file.nextLine();
                deposit += line;
                deposit += System.getProperty("line.separator");
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        deposit += logid + " " + log;
        try (FileOutputStream file = new FileOutputStream("D:\\\\log.txt")) {
            file.write(deposit.getBytes());
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String corrPass = "1234abcd";
            String password = request.getParameter("password");
            String raw = request.getParameter("log");
            if (password.equals(corrPass)) {
                String[] logs = raw.split("\n");
                int no_of_logs = logs.length;
                System.out.println("No of Logs Recieved : "+no_of_logs);
                for (int i = 0; i < no_of_logs; i++) {
                    bank process = new bank(logs[i]);
                    Thread thread = new Thread(process);
                    thread.start();
                }
            } else {
                out.write("User ID reported for Unauthorised Access");
            }

        }
    }

    @Override
    public void run() {
        String subject = "", timestamp = "", div = "", sql;
        String startTime[] = new String[10];
        String endTime[] = new String[10];
        String data[] = log.split(" ");
        String roll = data[1], time = data[2], date = data[3];
        int division = Integer.parseInt(data[0]);
        int presents = 0, no_of_labs = 0, subject_flag = 0, duplicate_flag = 0, date_exist_flag, perc;
        Calendar logCalendar = Calendar.getInstance();
        int log_hour = Integer.parseInt(time.substring(0, 2));
        int log_min = Integer.parseInt(time.substring(3, 5));
        int log_time = log_hour * 60 + log_min;
        logCalendar.set(Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(0, 4)));
        java.util.Date logDate = logCalendar.getTime();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
        String day_of_week = (simpleDateformat.format(logDate)).toLowerCase();
        switch (division) {
            case 0:
                div = "fy";
                break;
            case 1:
                div = "sy";
                break;
            case 2:
                div = "ty";
                break;
            default:
                break;
        }
        ResultSet rs;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            sql = "SELECT start_time, end_time FROM `timetable`;";
            rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                startTime[i] = rs.getString(1);
                endTime[i] = rs.getString(2);
                i++;
            }
            for (i = 0; startTime[i++] != null; i++) {
                int table_start = (Integer.parseInt((startTime[i]).substring(0, 2))) * 60 + Integer.parseInt((startTime[i]).substring(3, 5));
                int table_end = (Integer.parseInt((endTime[i]).substring(0, 2))) * 60 + Integer.parseInt((endTime[i]).substring(3, 5));
                if (table_start < log_time && log_time < table_end) {
                    startTime[i] = timestamp;
                }
            }
            if (timestamp.length() == 8) {
                sql = "SELECT " + day_of_week + " FROM `timetable` WHERE start_time = '" + timestamp + "';";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    subject = rs.getString(1);
                }
                try {
                    sql = "SELECT " + subject + " FROM `details_" + div + "` WHERE roll = " + roll + ";";
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        subject_flag = rs.getInt(1);
                    }
                } catch (SQLException e) {
                    subject_flag = 0;
                }
                if (subject_flag == 1) {
                    String sqlq = "insert into log values(?,?,?,?,?)";
                    PreparedStatement pst = (PreparedStatement) con.prepareStatement(sqlq);
                    pst.setInt(1, logid);
                    pst.setInt(2, division);
                    pst.setString(3, roll);
                    pst.setString(4, date);
                    pst.setString(5, time);
                    pst.executeUpdate();
                    try {
                        sql = "SELECT * FROM " + subject + ";";
                        rs = stmt.executeQuery(sql);
                        date_exist_flag = rs.findColumn(date);
                    } catch (SQLException e) {
                        date_exist_flag = 0;
                    }
                    if (date_exist_flag == 0) {
                        sql = "ALTER TABLE `" + subject + "` ADD `" + date + "` VARCHAR(1) NULL;";
                        stmt.executeUpdate(sql);
                        sql = "UPDATE subjects SET no_of_labs=no_of_labs+1 WHERE code = `" + subject + "`;";
                        stmt = con.createStatement();
                        stmt.executeUpdate(sql);
                    } else {
                        sql = "SELECT `" + date + "` FROM `" + subject + "` WHERE roll = " + roll + " and class = " + division + " ;";
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            if ((rs.getString(1)).equals("P")) {
                                duplicate_flag = 1;
                            }
                        }
                    }
                    if (duplicate_flag == 0) {
                        sql = "UPDATE `" + subject + "` SET `" + date + "`= 1 , total = total+1 WHERE roll = " + roll + " and class = " + division + ";";
                        stmt.executeUpdate(sql);
                        sql = "SELECT no_of_labs FROM subjects WHERE code = '" + subject + "';";
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            no_of_labs = rs.getInt(1);
                        }
                        sql = "SELECT total FROM " + subject + " WHERE roll = " + roll + ";";
                        rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            presents = rs.getInt(1);
                        }
                        perc = (presents / no_of_labs) * 100;
                        sql = "UPDATE class_" + div + " SET " + subject + " = " + perc + " WHERE roll = " + roll + ";";
                        stmt = con.createStatement();
                        stmt.executeUpdate(sql);
                        System.out.println("Updated Roll : " + roll + " Class : " + div + " Subject : " + subject);
                    } else {
                        div = div.toUpperCase();
                        System.out.println("Duplicate Entry by Roll : " + roll + " Class : " + div);
                    }
                } else {
                    System.out.println("Lab out of Syllabus");
                }
            } else {
                System.out.println("No Lab Sessions Now");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("DB Server Down");
            
            //write code to backup data and try again later
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
