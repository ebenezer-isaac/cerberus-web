import cerberus.messages;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static cerberus.AttFunctions.getAccess;
import static cerberus.AttFunctions.getWeekID;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class saveTimetable extends HttpServlet {

    private static final long serialVersionUID = 584397678136844754L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int access = getAccess(request);
            switch (access) {
                case 1:
                    int week = Integer.parseInt(request.getParameter("week"));
                    int labid = Integer.parseInt(request.getParameter("lab"));
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps7 = con.prepareStatement("SELECT * from slot");
                        ResultSet rs1 = ps7.executeQuery();
                        int no_of_slots = 0;
                        while (rs1.next()) {
                            no_of_slots++;
                        }
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        int weekID = getWeekID(week, year);
                        PreparedStatement ps4 = con.prepareStatement("SELECT timetable.slotID,"
                                + "MAX(CASE WHEN dayID = 1 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Monday, "
                                + "MAX(CASE WHEN dayID = 2 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Tuesday, "
                                + "MAX(CASE WHEN dayID = 3 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Wednesday, "
                                + "MAX(CASE WHEN dayID = 4 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Thursday, "
                                + "MAX(CASE WHEN dayID = 5 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Friday, "
                                + "MAX(CASE WHEN dayID = 6 THEN concat(timetable.subjectID,' , ',timetable.batchID) END) as Saturday "
                                + "FROM timetable "
                                + "INNER JOIN slot "
                                + "ON timetable.slotID = slot.slotID "
                                + "where labID=? and weekID=(select weekID from week where week = ?) "
                                + "GROUP BY slot.startTime, slot.endTime;");
                        ps4.setInt(1, labid);
                        ps4.setInt(2, week);
                        ResultSet rs = ps4.executeQuery();
                        int done[] = new int[no_of_slots];
                        for (int y = 0; y < no_of_slots; y++) {
                            done[y] = 0;
                        }
                        while (rs.next()) {
                            int slot = (rs.getInt(1)) - 1;
                            done[slot] = 1;
                            for (int j = 0; j <= 5; j++) {
                                String exist = rs.getString(j + 2);
                                String table = request.getParameter("c" + slot + (j + 1)) + " , " + request.getParameter("batch" + slot + (j + 1));
                                if (exist == null) {
                                    if (table.equals("null , null") || table.equals("- , -") || table.equals("- , null") || table.equals("null , -")) {
                                    } else {
                                        String[] arrOfStr = table.split(" , ");
                                        PreparedStatement pps = con.prepareStatement("INSERT INTO `timetable`(`slotID`, `labID`, `subjectID`, `batchID`, `weekID`, `dayID`) VALUES (?,?,?,?,?,?)");
                                        pps.setInt(1, (slot + 1));
                                        pps.setInt(2, labid);
                                        pps.setString(3, arrOfStr[0]);
                                        pps.setInt(4, Integer.parseInt(arrOfStr[1]));
                                        pps.setInt(5, weekID);
                                        pps.setInt(6, (j + 1));
                                        pps.executeUpdate();
                                    }
                                } else if (table.equals("null , null") || table.equals("- , -") || table.equals("- , null") || table.equals("null , -")) {
                                    String[] arrOfStr = exist.split(" , ");
                                    PreparedStatement pps = con.prepareStatement("DELETE from `timetable` where `slotID` = ? and `labID` = ? and `weekID` = ? and `dayID` = ?");
                                    pps.setInt(1, (slot + 1));
                                    pps.setInt(2, labid);
                                    pps.setInt(3, weekID);
                                    pps.setInt(4, (j + 1));
                                    pps.executeUpdate();
                                } else if (exist.equals(table)) {
                                } else {
                                    String[] arrOfStr = table.split(" , ");
                                    PreparedStatement pps = con.prepareStatement("UPDATE `timetable` SET `subjectID`=?,`batchID`=? WHERE slotID=? and labID=? and weekID=? and dayID=?");
                                    pps.setString(1, arrOfStr[0]);
                                    pps.setInt(2, Integer.parseInt(arrOfStr[1]));
                                    pps.setInt(3, (slot + 1));
                                    pps.setInt(4, labid);
                                    pps.setInt(5, weekID);
                                    pps.setInt(6, (j + 1));
                                    pps.executeUpdate();
                                }
                            }
                        }
                        for (int y = 0; y < no_of_slots; y++) {
                            if (done[y] == 0) {
                                int slot = y;
                                for (int j = 0; j <= 5; j++) {
                                    String table = request.getParameter("c" + slot + (j + 1)) + " , " + request.getParameter("batch" + slot + (j + 1));
                                    if (table.equals("null , null") || table.equals("- , -") || table.equals("- , null") || table.equals("null , -")) {
                                    } else {
                                        String[] arrOfStr = table.split(" , ");
                                        PreparedStatement pps = con.prepareStatement("INSERT INTO `timetable`(`slotID`, `labID`, `subjectID`, `batchID`, `weekID`, `dayID`) VALUES (?,?,?,?,?,?)");
                                        pps.setInt(1, (slot + 1));
                                        pps.setInt(2, labid);
                                        pps.setString(3, arrOfStr[0]);
                                        pps.setInt(4, Integer.parseInt(arrOfStr[1]));
                                        pps.setInt(5, weekID);
                                        pps.setInt(6, (j + 1));
                                        pps.executeUpdate();
                                    }
                                }
                            }

                        }
                        con.close();
                        messages a = new messages();
                        a.success(request, response, "Your changes to the timetable have been saved", "viewTimetable");
                    } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "/Cerberus/homepage");
                    }
                    break;
                case 0:
                    messages b = new messages();
                    b.kids(request, response);
                    break;
                default:
                    messages c = new messages();
                    c.nouser(request, response);
            }
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
