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
        <title> Server Message </title>
        <style> html {overflow: hidden;}</style>
        <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.min.css" type="text/css">
        <link rel="stylesheet" href="css/custom.css" type="text/css">
        <link rel="stylesheet" href="css/loader.css" type="text/css">
        <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
        <link rel="icon" href="images/logo-circle-removebg.png" type="image/gif">
        <style>
        </style>
    </head>
    <body style="background-color: #black;">
        <div class="container" >
            <div class="reset_page" >
                <div class="form_reset">
                    <%
                        Integer access = 0;
                        try {
                            access = (Integer) session.getAttribute("access");
                        } catch (Exception e) {
                            HttpSession sess = request.getSession(true);
                            java.util.Date date = new java.util.Date();
                            SimpleDateFormat ft = new SimpleDateFormat("w");
                            int week = Integer.parseInt(ft.format(date));
                            sess.setAttribute("week", week);
                            sess.setAttribute("access", 2);
                            access = 2;
                        }
                        System.out.println(access);
                        switch (access) {
                            case 2:
                                out.print("<script>");
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    Connection con = DriverManager.getConnection("jdbc:mysql://172.21.170.14:3306/cerberus?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "cerberus", "abc@123");
                                    Statement stmt = con.createStatement();
                                    ResultSet rs = stmt.executeQuery("SELECT `class` FROM `class` ORDER BY `class` ASC");
                                    int oddeve = 0;

                                    String fileName = "D:\\oddEve.txt";
                                    File file = new File(fileName);
                                    FileReader fr = new FileReader(file);
                                    BufferedReader br = new BufferedReader(fr);
                                    String text;
                                    while ((text = br.readLine()) != null) {
                                        try {
                                            oddeve = Integer.parseInt(text.trim());
                                            break;
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    int classcount = 1;
                                    out.print("function getbatch(name,sub){batch=\"");
                                    out.print("<select class='editSelectTimeTable not-allowed' onchange = 'subsdisable(this.id)' name = 'batch\"+sub+\"' id = 'batch\"+name+\"' disabled>"
                                            + "<option name='-' value='-' selected >No Batch</option>");
                                    PreparedStatement ps = con.prepareStatement("Select batchID, name from batch");
                                    ResultSet rs4 = ps.executeQuery();
                                    while (rs4.next()) {
                                        out.print("<option name='batch" + rs4.getString(1) + "' value='" + rs4.getString(1) + "'>" + rs4.getString(2) + "</option>");
                                    }
                                    out.print("</select>\";return batch;}");
                                    while (rs.next()) {
                                        out.print("var class" + classcount + ";");
                                        int sem = AttFunctions.getSem(oddeve, classcount);
                                        PreparedStatement ps3 = con.prepareStatement("Select subjectID,abbreviation from subject where sem = ?");
                                        ps3.setInt(1, sem);
                                        ResultSet rs3 = ps3.executeQuery();
                                        out.print("class" + classcount + "=\"<table align='center'>");
                                        int no_of_sub = 0;
                                        while (rs3.next()) {
                                            no_of_sub += 1;
                                            out.print("<tr><td><input type='checkbox' name='subjects' id='subject" + no_of_sub + "' value='" + rs3.getString(1) + "' onchange='batchdisable(" + no_of_sub + ")'></option></td><td>" + rs3.getString(2) + "</td>"
                                                    + "<td>");
                                            out.print("\"+getbatch(" + no_of_sub + ",'" + rs3.getString(1) + "')+\"");
                                            out.print("<td></tr>");
                                        }
                                        out.print("</table>\";");
                                        classcount++;
                                    }
                                    classcount--;
                                    out.print("function dissub()"
                                            + "{var index = document.getElementById('clas').selectedIndex;"
                                            + "if(index==0)"
                                            + "{document.getElementById('subs').innerHTML = ' ';}");
                                    for (int i = 1; i <= classcount; i++) {
                                        out.print("else if (index==" + i + ")"
                                                + "{document.getElementById('subs').innerHTML = class" + i + "}");
                                    }
                                    out.print("}</script>");
                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                break;
                            case 0:

                                break;
                        }
                    %>
                    <div align="center">
                        <h2 style="font-family: arno pro caption">heading </h2>
                        <br>
                        <p>body</p>
                        the button 
                    </div>
                </div>
            </div>
        </div>
        <div id="particles-js"></div>
        <script src="js/jquery.min.js"></script>
        <script src="js/main.js"></script>
        <script src="js/particles.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>