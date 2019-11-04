<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
<%@page import="cerberus.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Cerberus</title>
        <link rel="stylesheet" href="fontawesome/fontawesome-free-5.11.2-web/css/all.css">
        <link rel="icon" href="images/logo-circle.png" type="image/gif">
        <link rel="stylesheet" href="css/loader.css" type="text/css">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">
        <!-- Our Custom CSS -->
        <link rel="stylesheet" href="css/style2.css">
        <!-- Scrollbar Custom CSS -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/malihu-custom-scrollbar-plugin/3.1.5/jquery.mCustomScrollbar.min.css">

        <!-- Font Awesome JS -->
        <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/solid.js" integrity="sha384-tzzSw1/Vo+0N5UhStP3bvwWPq+uvzCMfrN1fEFe+xBmv1C/AtVX5K0uZtmcHitFZ" crossorigin="anonymous"></script>
        <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/fontawesome.js" integrity="sha384-6OIrr52G08NpOFSZdxxz1xdNSndlD4vdcf/q2myIUVO0VsqaGHJsB0RaBE01VTOY" crossorigin="anonymous"></script>
    </head>
    <body>
        <div class = "wrapper" > 
            <nav id="sidebar" ><ul class="list-unstyled components"><p align="center"><br><a href="/Cerberus/homepage">STUDENT PANEL</a></p><div id='pic' align='center'></div>
                    <li>
                        <a href="#attSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fas fa-calendar-alt"></i>&nbsp;&nbsp;View Attendance</a>
                        <ul class="collapse list-unstyled" id="attSubmenu1">
                            <%
                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                    PreparedStatement ps1 = con.prepareStatement("select subject.subjectID, subject.Abbreviation from studentsubject "
                                            + "inner join subject "
                                            + "on subject.subjectID=studentsubject.subjectID "
                                            + "where PRN = ?");
                                    ps1.setString(1, session.getAttribute("user").toString());
                                    ResultSet rs = ps1.executeQuery();
                                    int index = 1;
                                    while (rs.next()) {
                                        out.print("<li><a href='/Cerberus/studSubAttendance?sub=" + rs.getString(1) + "'><i class='fas fa-chevron-right'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a></li>");
                                        index++;
                                    }
                                    if (index == 1) {
                                        out.print("<li><a href=''><i class='fas fa-book'></i>&nbsp;&nbsp;No Subjects Available</a></li>");
                                    }
                                } catch (Exception e) {
                                }
                            %>
                        </ul>
                    </li>
                    </li></ul></nav> 
            <div id = "content" > 
                <nav class="navbar navbar-expand-lg bg-light">
                    <div class="container-fluid"><button type="button" id="sidebarCollapse" style="background-color:#000000;" class="btn"><font style="color: #ffffff"><i class="fas fa-bars"></i></font><img src="images/logo-circle.png"  height="42" width="42"></button>Cerberus Attendance Management System<a class="nav-link" href="/Cerberus/signout"><button type="button" style="background-color:#000000;" class="btn"><span><font style="color: #ffffff">Sign out</font></span><font style="color: #ffffff"><i class="fas fa-door-open"></i></font></button></a></div></nav>
                <script>
                    window.onload = function () {
                        var labels = document.getElementsByTagName('ul');
                        for (var i = 0; i < labels.length; i++) {

                            labels[i].classList.add("unselectable");
                        }
                        var labels = document.getElementsByTagName('body');
                        for (v
                                ar i = 0;
                        i < labels.length;
                        i++
                                ) {                     labels[i].classList.add("unselectable")
                        ;
                        }
                    };

                </script>
                <style>
                    img {
                        pointer-events: none;
                    }
                    .unselectable {
                        -webkit-touch-callout: none;
                        -webkit-user-select: none;
                        -khtml-user-select: none;
                        -moz-user-select: none;
                        -ms-user-select: none;
                        user-select: none;
                    }
                </style>
                <%
                    Class.forName(
                            "com.mysql.cj.jdbc.Driver");
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                        PreparedStatement ps2 = con.prepareStatement("SELECT name, photo FROM student WHERE PRN = ?");
                        ps2.setString(1, session.getAttribute("user").toString());
                        byte[] blob = null;
                        String name = "";
                        ResultSet rs = ps2.executeQuery();
                        while (rs.next()) {
                            blob = rs.getBytes("photo");
                            name = rs.getString("name");
                        }
                        con.close();
                        if (blob != null) {
                            String imgString = DatatypeConverter.printBase64Binary(blob);
                            out.print("<script>document.getElementById('pic').innerHTML=\""
                                    + "<a href='editProfilePage.html'>"
                                    + "<img src='data:image/png;base64," + imgString + "'/>"
                                    + "<br>" + name + "<br><br></a>\";</script>");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                %>