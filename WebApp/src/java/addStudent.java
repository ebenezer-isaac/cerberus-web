
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class addStudent extends HttpServlet {

    String prn = "";
    String name = "";
    String email = "";
    String rawpass = "";
    String body = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            this.prn = request.getParameter("prn");
            this.name = request.getParameter("name");
            this.email = request.getParameter("email");
            int seleclass = Integer.parseInt(request.getParameter("clas"));
            int roll = Integer.parseInt(request.getParameter("roll"));
            String photoID = request.getParameter("photo_id");
            this.rawpass = this.prn;
            String pass = AttFunctions.hashIt(this.prn);
            InputStream inputStream = null;
            try{
                URL url = new URL("http://msubcdndwn.digitaluniversity.ac/resources/public/msub/Photosign/Photo/" + this.prn.substring(0, 4) + "/" + photoID + "_P.JPG");
                inputStream = url.openStream();
            }
            catch(IOException e){
                
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "")) {

                    PreparedStatement ps = con.prepareStatement("INSERT INTO `student`(`PRN` ,`name`, `email`, `password`,`photo_id`, `photo`) VALUES (?,?,?,?,?,?)");
                    ps.setString(1, this.prn);
                    ps.setString(2, this.name);
                    ps.setString(3, this.email);
                    ps.setString(4, pass);
                    ps.setString(5, photoID);
                    ps.setBlob(6, inputStream);
                    ps.executeUpdate();
                    
                    ps = con.prepareStatement("INSERT INTO `rollcall`(`classID`, `rollNo`, `PRN`) VALUES (?,?,?)");
                    ps.setInt(1, seleclass);
                    ps.setInt(2, roll);
                    ps.setString(3, this.prn);
                    ps.executeUpdate();

                    String subjects[] = request.getParameterValues("subjects");
                    System.out.println("hwkki world " + subjects.length);
                    for (int i = 0; i < subjects.length; i++) {
                        try {
                            int batchid = Integer.parseInt(request.getParameter("batch" + (i + 1)));
                            System.out.println(batchid);
                            System.out.println(subjects[i]);
                            System.out.println(batchid);
                            ps = con.prepareStatement("INSERT INTO `studentsubject`(`PRN`, `subjectID`, `batchID`) VALUES (?,?,?)");
                            ps.setString(1, this.prn);
                            ps.setString(2, subjects[i]);
                            ps.setInt(3, batchid);
                            ps.executeUpdate();
                        } catch (NumberFormatException | SQLException e) {
                        }
                    }
                    this.body = "Hello " + this.name + ",\n    This mail is in response to a request to add you as a student at MSU-CA Department.\n\n"
                            + "Email/Username : " + this.email + "\n"
                            + "One Time Password : " + this.rawpass + "\n\n"
                            + "You can now login with given username and password at at CA Department's Intranet WebSite\n"
                            + "and view timetable attendance through this portal.\n\n"
                            + "Note: You will be prompted to change password on first login.\n\n"
                            + "This is an auto-generated e-mail, please do not reply.\n"
                            + "Regards\nCerberus Support Team";

                    Mailer mail = new Mailer();
                    mail.send(this.email, "Account Registration", this.body);
                    RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                    request.setAttribute("redirect", "false");
                    request.setAttribute("head", "Student Added");
                    request.setAttribute("body", this.name + " was added to the list of Students. A mail has been sent to respective student.");
                    request.setAttribute("url", "homepage");
                    rd.forward(request, response);

                    con.close();
                }
            } catch (SQLException e) {
                RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
                request.setAttribute("redirect", "false");
                request.setAttribute("head", "Request Failed");
                request.setAttribute("body", "Student alredy exists with given PRN");
                request.setAttribute("url", "editStudent?flow=add");
                rd.forward(request, response);
                e.printStackTrace();

            }
        } catch (Exception e) {
            /*RequestDispatcher rd = request.getRequestDispatcher("message.jsp");
            request.setAttribute("redirect", "false");
            request.setAttribute("head", "Error");
            request.setAttribute("body", e.getMessage());
            request.setAttribute("url", "index.jsp");
            rd.forward(request, response);*/
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
