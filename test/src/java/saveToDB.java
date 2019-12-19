
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
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
                stmt10.executeUpdate();

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
                    int roll = 0, prn = 0;
                    String name = null;
                    String email = null;
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
                            String url = "/Cerberus/addStudent?roll=" + value[0] + "&prn=" + value[1] + "&name=" + value[2] + "&email=" + value[3] + "&class=1";
                            System.out.println(url);
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
