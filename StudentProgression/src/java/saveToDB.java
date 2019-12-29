
import cerberus.AttFunctions;
import static cerberus.AttFunctions.generatePassword;
import static cerberus.AttFunctions.getClassName;
import static cerberus.AttFunctions.getCurrDate;
import static cerberus.AttFunctions.getCurrTime;
import static cerberus.AttFunctions.getDateID;
import static cerberus.AttFunctions.getSem;
import static cerberus.AttFunctions.getTimeID;
import static cerberus.AttFunctions.oddEve;
import static cerberus.AttFunctions.semSubs;
import cerberus.Mailer;
import cerberus.messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class saveToDB extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // FileInputStream fis=new FileInputStream(new File("C:\\demo\\student.xls"));  
            out.println(request.getAttribute("path"));
            System.out.println("saved to DB ");
            String inputFile = (String) request.getAttribute("path");

            try {
                //Delete Tables from database

                /*Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                PreparedStatement stmt = con.prepareStatement("Delete from otp");
                stmt.executeUpdate();
                PreparedStatement stmt1 = con.prepareStatement("Delete from log");
                stmt1.executeUpdate();
                PreparedStatement stmt4 = con.prepareStatement("Delete from facultytimetable");
                stmt4.executeUpdate();
                PreparedStatement stmt6 = con.prepareStatement("Delete from attendance");
                stmt6.executeUpdate();
                PreparedStatement stmt7 = con.prepareStatement("Delete from timetable");
                stmt7.executeUpdate();
                PreparedStatement stmt2 = con.prepareStatement("Delete from week");
                stmt2.executeUpdate();
                PreparedStatement stmt3 = con.prepareStatement("Delete from studentsubject");
                stmt3.executeUpdate();
                PreparedStatement stmt5 = con.prepareStatement("Delete from datedate");
                stmt.executeUpdate();

                //Full Term Progression
                //Delete from student where classID = 3
                PreparedStatement stmt8 = con.prepareStatement("Delete from rollcall where classID = 3");
                stmt8.executeUpdate();
                //update classID from 2 to 3
                PreparedStatement stmt9 = con.prepareStatement("update rollcall set classID=3 where classID=2");
                stmt9.executeUpdate();
                //update classID from 1 to 2
                PreparedStatement stmt10 = con.prepareStatement("update rollcall set classID=2 where classID=1");
                stmt10.executeUpdate();*/
                //Add student data for FY from excel
                try {
                    //obtaining input bytes from a file
                    FileInputStream fis = new FileInputStream(new File(inputFile));
                    //creating workbook instance that refers to .xls file  
                    XSSFWorkbook wb = new XSSFWorkbook(fis);
                    //creating a Sheet object to retrieve the object  
                    XSSFSheet sheet = wb.getSheetAt(0);
                    //evaluating cell type   
                    FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
                    int roll;
                    String prn;
                    String name;
                    String email;
                    int rowno = 1;

                    for (Row row : sheet) //iteration over row using for each loop  
                    {
                        if (rowno != 1) {
                            String value[] = new String[4];
                            int i = 0;
                            for (Cell cell : row) //iteration over cell using for each loop  
                            {

                                switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:   //field that represents numeric cell type  
                                        value[i] = cell.getNumericCellValue() + "";

                                        break;
                                    case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
                                        //getting the value of the cell as a string  
                                        value[i] = cell.getStringCellValue();
                                        break;
                                }

                                i++;
                            }
                            float rol = Float.parseFloat(value[0]);
                            int iroll = (int) rol;
                            InputStream inputStream = null;
                            String url = "http://172.21.170.14:8080/Cerberus/addStudent?roll=" + iroll + "&prn=" + value[1] + "&name=" + value[2] + "&email=" + value[3] + "&clas=1";
                            email = value[3];
                            name = value[2];
                            int classID = 2;
                            roll = iroll;
                            prn = value[1] + "";
                            String rawpass = null;
                            String pass = null;
                            try {
                                rawpass = generatePassword();
                                pass = AttFunctions.hashIt(rawpass);
                            } catch (NoSuchAlgorithmException ex) {

                            }
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                                PreparedStatement ps = con.prepareStatement("INSERT INTO `student`(`PRN` ,`name`, `email`, `password`) VALUES (?,?,?,?)");
                                ps.setString(1, prn);
                                ps.setString(2, name);
                                ps.setString(3, email);
                                ps.setString(4, pass);
                                ps.executeUpdate();
                                ps = con.prepareStatement("INSERT INTO `rollcall`(`classID`, `rollNo`, `PRN`) VALUES (?,?,?)");
                                ps.setInt(1, classID);
                                ps.setInt(2, roll);
                                ps.setString(3, prn);
                                ps.executeUpdate();
                                int dateID = getDateID(getCurrDate());
                                int timeID = getTimeID(getCurrTime());
                                ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`, `dateID`, `timeID`) VALUES (?, '1', NULL , ?, ?);");
                                ps.setString(1, prn);
                                ps.setInt(2, dateID);
                                ps.setInt(3, timeID);
                                ps.executeUpdate();
                                ps = con.prepareStatement("INSERT INTO `studentfingerprint` (`PRN`, `templateID`, `template`, `dateID`, `timeID`) VALUES (?, '2', NULL , ?, ?);");
                                ps.setString(1, prn);
                                ps.setInt(2, dateID);
                                ps.setInt(3, timeID);
                                ps.executeUpdate();
                                String clas = getClassName(classID);
                                String subs[][] = semSubs(getSem(oddEve(), classID), classID);
                                for (String[] sub : subs) {
                                    ps = con.prepareStatement("INSERT INTO `studentsubject`(`PRN`, `subjectID`, `batchID`) VALUES (?,?,?)");
                                    ps.setString(1, prn);
                                    ps.setString(2, sub[0]);
                                    ps.setInt(3, 0);
                                    ps.executeUpdate();
                                }
                                String body = "Hello " + name + ",\n    This mail is in response to a request to add you as a student at MSU-CA Department.\n\n"
                                        + "Email/Username : " + email + "\n"
                                        + "Password : " + rawpass + "\n\n"
                                        + "Class    : " + clas + "\n"
                                        + "PRN    : " + prn + "\n\n"
                                        + "Note: You can change your password by clicking 'Create a New Password' in the Login Page.\n"
                                        + "You need to be connected to the BCA Intranet for the below link to work:\n"
                                        + "http://172.21.170.14:8080/Cerberus/\n\n"
                                        + "You can now login with given username and password at CA Department's Intranet Website\n"
                                        + "and view timetable attendance through this portal. You will be asked to provide your MSU Username and select your subjects on first login.\n\n"
                                        + "This is an auto-generated e-mail, please do not reply.\n"
                                        + "Regards\nCerberus Support Team";

                                Mailer mail = new Mailer();
                                mail.send(email, "Account Registration", body);
                                con.close();
                            } catch (ClassNotFoundException | SQLException e) {
                                e.printStackTrace();
                            }

                        }
                        rowno++;
                        System.out.println();
                    }

//                Workbook w = Workbook.getWorkbook(new File(inputFile));
//                System.out.println("got workbook" + inputFile);
//                Sheet sheet = w.getSheet(0);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            File xls = new File(inputFile);
            xls.delete();
            out.print("</br>file uploaded successfully goto previous tab and click finish");

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
