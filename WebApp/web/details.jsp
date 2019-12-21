<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cerberus.*"%>
<%@ page import = "java.io.*,java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title> Student Details </title>
        <style> html {overflow: hidden;}</style>
        <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.min.css" type="text/css">
        <link rel="stylesheet" href="css/custom.css" type="text/css">
        <link rel="stylesheet" href="css/dropdowns.css" type="text/css">
        <link rel="stylesheet" href="css/checkbox.css" type="text/css">
        <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
        <link rel="icon" href="images/logo-circle-removebg.png" type="image/gif">
        <style>
            .btn-primary{color:#fff;background-color:#007bff;border-color:#007bff}.btn-primary:hover{color:#fff;background-color:#0069d9;border-color:#0062cc}.btn-primary.focus,.btn-primary:focus{-webkit-box-shadow:0 0 0 .2rem rgba(38,143,255,.5);box-shadow:0 0 0 .2rem rgba(38,143,255,.5)}.btn-primary.disabled,.btn-primary:disabled{color:#fff;background-color:#007bff;border-color:#007bff}.btn-primary:not(:disabled):not(.disabled).active,.btn-primary:not(:disabled):not(.disabled):active,.show>.btn-primary.dropdown-toggle{color:#fff;background-color:#0062cc;border-color:#005cbf}.btn-primary:not(:disabled):not(.disabled).active:focus,.btn-primary:not(:disabled):not(.disabled):active:focus,.show>.btn-primary.dropdown-toggle:focus{-webkit-box-shadow:0 0 0 .2rem rgba(38,143,255,.5);box-shadow:0 0 0 .2rem rgba(38,143,255,.5)}
            .body {
                -webkit-touch-callout: none;
                -webkit-user-select: none;
                -khtml-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
            }
        </style>
    </head>
    <body>
        <div class="container" >
            <div class="reset_page" style=' margin-top: 40px;' >
                <div class="form_reset" style='max-width: 320px;'>
                    <div align="center">
                        <h2 style="font-family: arno pro caption"><div id = 'name'></div></h2>
                        <div id='validations' style='color:red;font-size:14px;'>Select the subject you have opted and their corresponding batch alloted to you carefully. This is the only time you will be asked to fill this form.</div>
                        <br>
                        <form action='saveDetails' method = 'post'>
                            <div id='validations' style='color:black;font-size:14px;'>Enter your MSU Username Below</div>
                            <input type='text' id='msuid' name='msuid' class='editSubjectForm' placeholder='DxxCJxxxxxxx' onkeyup='validateBtn();'>
                            <div id='subs'></div><br>
                            <button type='submit' style='width:200px; visibility: hidden;' id='studbtn1' class='btn btn-primary'>Save Details</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div id="particles-js"></div>
        <% try {
                Integer access = AttFunctions.getAccess(request);
                if (access == 0) {
                    int count = 0;
                    String prn = (String) session.getAttribute("user");
                    System.out.println(prn);
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                        PreparedStatement ps = con.prepareStatement("select count(batchID) from studentsubject where prn = ? and batchID !=0");
                        ps.setString(1, prn);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            count = rs.getInt(1);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        messages b = new messages();
                        b.error(request, response, e.getMessage(), "homepage");
                    }
                    if (count > 0) {
                        RequestDispatcher rd = request.getRequestDispatcher("homepage.jsp");
                        rd.forward(request, response);
                    } else {
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                            int oddeve = AttFunctions.oddEve();
                            out.print("<script>");
                            out.print("function getbatch(name,sub){batch=\"");
                            out.print("<select class='editSelectTimeTable not-allowed' onchange = 'subsdisable(this.id);validateBtn();' name = 'batch\"+sub+\"' id = 'batch\"+name+\"' disabled>"
                                    + "<option name='-' value='-' selected >No Batch</option>");
                            PreparedStatement ps = con.prepareStatement("Select batchID, name from batch where batchID>0");
                            ResultSet rs4 = ps.executeQuery();
                            while (rs4.next()) {
                                out.print("<option name='batch" + rs4.getString(1) + "' value='" + rs4.getString(1) + "'>" + rs4.getString(2) + "</option>");
                            }
                            out.print("</select>\";return batch;}");
                            PreparedStatement ps2 = con.prepareStatement("select classID from rollcall where prn = ?");
                            System.out.println("PRN :"+prn);
                            ps2.setString(1, prn);
                            ResultSet rs2 = ps2.executeQuery();
                            int classID = 0;
                            while (rs2.next()) {
                                classID = rs2.getInt(1);
                            }
                            System.out.println("classID :"+classID);
                            out.print("var division;");
                            int sem = AttFunctions.getSem(oddeve, classID);
                            System.out.println(classID);
                            System.out.println(sem);
                            PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                            ps3.setInt(1, sem);
                            ResultSet rs3 = ps3.executeQuery();
                            out.print("division=\"<table align='center'>");
                            int no_of_sub = 0;
                            while (rs3.next()) {
                                no_of_sub += 1;
                                out.print("<tr><td><input type='checkbox' name='subjects' id='subject" + no_of_sub + "' value='" + rs3.getString(1) + "' onchange='batchdisable(" + no_of_sub + ");validateBtn();'><label for='subject" + no_of_sub + "'></label></option></td><td>&nbsp;&nbsp;" + rs3.getString(2) + "</td>"
                                        + "<td>");
                                out.print("\"+getbatch(" + no_of_sub + ",'" + rs3.getString(1) + "')+\"");
                                out.print("<td></tr>");
                            }
                            out.print("</table>\";");
                            ps2 = con.prepareStatement("select name from student where prn = ?");
                            ps2.setString(1, prn);
                            rs2 = ps2.executeQuery();
                            String name = "";
                            while (rs2.next()) {
                                name = rs2.getString(1);
                            }
                            out.print("document.getElementById('subs').innerHTML = division;"
                                    + "function batchdisable(id) { "
                                    + "   if (document.getElementById('subject' + id).checked == true) {"
                                    + "        document.getElementById('batch' + id).selectedIndex = 1;"
                                    + "        document.getElementById('batch' + id).disabled = false;"
                                    + "        document.getElementById('batch' + id).classList.remove('not-allowed');"
                                    + "    } else {        document.getElementById('batch' + id).selectedIndex = 0; "
                                    + "       document.getElementById('batch' + id).disabled = true;      "
                                    + "  document.getElementById('batch' + id).classList.add('not-allowed'); "
                                    + "   }}var request;var id;function subsdisable(id) { "
                                    + "   var index = document.getElementById(id).selectedIndex;  "
                                    + "  if (index == 0) {   "
                                    + "    id = id.substr(5);   "
                                    + "     document.getElementById('subject' + id).checked = false;"
                                    + "    }    document.getElementById('batch' + id).disabled = true;  "
                                    + "  document.getElementById('batch' + id).classList.add('not-allowed');"
                                    + "}document.getElementById('name').innerHTML='" + name + "';"
                                    + "function validateBtn(){"
                                    + "var msu = document.getElementById('msuid').value;"
                                    + "if(msu.length==12){"
                                    + "for(var i = 1;i<=" + no_of_sub + ";i++)"
                                    + "{var subcheck = document.getElementById('subject'+i);"
                                    + "if(subcheck.checked){"
                                    + "document.getElementById('studbtn1').style.visibility='visible';console.log('button enabled');i=" + (no_of_sub + 1) + ""
                                    + "}else{document.getElementById('studbtn1').style.visibility='hidden';"
                                    + "}"
                                    + "}"
                                    + "}else{console.log('button disabled');"
                                    + "document.getElementById('studbtn1').style.visibility='hidden';"
                                    + "}"
                                    + "}validateBtn();"
                                    + "</script>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    out.print("<script>window.location.replace('/Cerberus/ajaxContent?url=homepage')</script>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
        <script src="js/jquery.min.js"></script>
        <script src="js/main.js"></script>
        <script src="js/particles.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>