
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class readExcel {

    String inputFile;
    String  division;
    int divi;
    public readExcel(String inputFile,  int divi) {
        this.inputFile = inputFile;
                switch (divi) {
            case 0:
                division = "fy";
                break;
            case 1:
                division = "sy";
                break;
            case 2:
                division = "ty";
                break;
            default:
                break;
        }
        this.divi = divi;
    }

    public int read() throws IOException, BiffException {
        Workbook w = Workbook.getWorkbook(new File(inputFile));
        Sheet sheet = w.getSheet(0);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            String sql = "DELETE FROM `details_"+division+"`";
            stmt.executeUpdate(sql);
            String subs[] = new String[sheet.getColumns() - 2];
            sql = "INSERT INTO `details_"+division+"` VALUES (?,";
            for (int col = 2; col < sheet.getColumns(); col++) {
                Cell cell = sheet.getCell(col, 0);
                subs[col - 2] = cell.getContents();
                sql += "?,";
            }
            sql += "?)";
            for (int row = 1; row < sheet.getRows(); row++) {
                PreparedStatement ps = con.prepareStatement(sql);
                Cell cell = sheet.getCell(0, row);
                int roll = Integer.parseInt("" + cell.getContents());
                ps.setInt(1, Integer.parseInt(cell.getContents()));
                System.out.print(roll+" ");
                for (int col = 1; col < sheet.getColumns(); col++) {
                    cell = sheet.getCell(col, row);
                    switch (col) {
                        case 1:
                            ps.setString(2, cell.getContents());
                            System.out.print(cell.getContents() + " ");
                            break;
                        default:
                            ps.setInt(col + 1, Integer.parseInt(cell.getContents()));
                            System.out.print(cell.getContents() + " ");
                            if (Integer.parseInt(cell.getContents()) == 1) {
                                insert(subs[col - 2],roll);
                            }
                            break;
                    }
                }
                System.out.println();
                ps.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            return(0);
        }
        File xls = new File(inputFile);
        xls.delete();
        return(1);
    }

    public void insert(String sub, int roll) {
        int flag = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance?zeroDateTimeBehavior=convertToNull", "root", "");
            Statement stmt = con.createStatement();
            try {
                ResultSet rs = stmt.executeQuery("Select * from `" + sub + "` where `roll` = " + roll);
                while (rs.next()) {
                    flag = 1;
                }
            } catch (SQLException e) {
                flag = 0;
            }
            if (flag == 0) {
                ResultSet rs = stmt.executeQuery("Select * from `" + sub + "`");
                ResultSetMetaData rsm = rs.getMetaData();
                String sql = "INSERT INTO `" + sub + "` VALUES (?,?,?,?";
                int no_of_cols;
                for (no_of_cols = 5; no_of_cols <= rsm.getColumnCount(); no_of_cols++) {
                    sql += ",?";
                }
                sql += ")";
                no_of_cols--;
                PreparedStatement pss = con.prepareStatement(sql);
                pss.setInt(1, divi);
                pss.setInt(2, roll);
                pss.setInt(3, 0);
                pss.setInt(4, 0);
                if (no_of_cols > 4) {
                    for (int set = 5; set <= no_of_cols; set++) {
                        pss.setString(set, "A");
                    }
                }
                pss.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, BiffException {
        readExcel file = new readExcel("D:/lars.xls",2);
        file.read();
    }
}
