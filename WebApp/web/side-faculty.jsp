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
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">
        <!-- Our Custom CSS -->
        <link rel="stylesheet" href="css/style2.css">
        <!-- Scrollbar Custom CSS -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/malihu-custom-scrollbar-plugin/3.1.5/jquery.mCustomScrollbar.min.css">

        <!-- Font Awesome JS -->
        <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/solid.js" integrity="sha384-tzzSw1/Vo+0N5UhStP3bvwWPq+uvzCMfrN1fEFe+xBmv1C/AtVX5K0uZtmcHitFZ" crossorigin="anonymous"></script>
        <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/fontawesome.js" integrity="sha384-6OIrr52G08NpOFSZdxxz1xdNSndlD4vdcf/q2myIUVO0VsqaGHJsB0RaBE01VTOY" crossorigin="anonymous"></script>
        <script>
            window.onload = function () {
                var labels = document.getElementsByTagName('li');
                for (var i = 0; i < labels.length; i++) {
                    disableSelection(labels[i]);
                }
                var labels = document.getElementsByTagName('ul');
                for (var i = 0; i < labels.length; i++) {
                    labels[i].classList.add("unselectable");
                }
                var labels = document.getElementsByTagName('body');
                for (var i = 0; i < labels.length; i++) {
                    labels[i].classList.add("unselectable");
                }
            };
            function disableSelection(element) {

                if (typeof element.onselectstart != 'undefined') {
                    element.onselectstart = function () {
                        return false;
                    };
                } else if (typeof element.style.MozUserSelect != 'undefined') {
                    element.style.MozUserSelect = 'none';
                } else {
                    element.onmousedown = function () {
                        return false;
                    };
                }
            }
            function unfade(element) {
                var op = 0.1; // initial opacity

                var timer = setInterval(function () {
                    if (op >= 1) {
                        clearInterval(timer);
                    }
                    element.style.opacity = op;
                    element.style.filter = 'alpha(opacity=' + op * 100 + ")";
                    element.style.display = 'block';
                    op += op * 0.08;
                }, 10);
            }
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
            @media screen and (max-width: 1000px) {
                div.example {
                    font-size: 17px;
                    content: "hi";
                }

                .toBeReplaced
                { 
                    visibility: hidden; 
                    position: relative; 
                }

                .toBeReplaced:after
                { 
                    visibility: visible; 
                    position: absolute;
                    left: 0;
                    content: "Cerberus";
                    font-family: DPSDbeyond;
                    float: none;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                } 
                /* width */
                ::-webkit-scrollbar {
                    width: 7.5px;
                }

                /* Track */
                ::-webkit-scrollbar-track {
                    background: #f1f1f1; 
                }

                /* Handle */
                ::-webkit-scrollbar-thumb {
                    background: #888; 
                }

                /* Handle on hover */
                ::-webkit-scrollbar-thumb:hover {

                </style>
            </head>
            <body>
                <div class="wrapper">
                    <!-- Sidebar --> 
                    <nav id="sidebar" >
                        <ul class="list-unstyled components">
                            <div align="center"><br><a href="javascript:setContent('/Cerberus/homepage');"><i class="fas fa-home"></i>&nbsp;&nbsp;FACULTY PANEL</a></div>
                            <br><div id='pic' align='center'></div>
                            <li>
                                <a href="#attSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fas fa-check"></i>&nbsp;&nbsp;Attendance Management</a>
                                <ul class="collapse list-unstyled" id="attSubmenu1">
                                    <%
                                        try {
                                            Class.forName("com.mysql.cj.jdbc.Driver");
                                            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                            Statement stmt = con.createStatement();
                                            ResultSet rs = stmt.executeQuery("SELECT classID, class FROM `class` ORDER BY `class` ASC");
                                            while (rs.next()) {
                                                EnglishNumberToWords a = new EnglishNumberToWords();
                                                String number = a.convert(rs.getInt(1));
                                                out.print("<li><a href=\"javascript:setContent('/Cerberus/attendance?class=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a></li>");
                                            }
                                        } catch (Exception e) {
                                        }
                                    %>
                                </ul>
                            </li>
                            <li>
                                <a href="#homeSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-calendar-alt"></i>&nbsp;&nbsp;Timetable Management</a>
                                <ul class="collapse list-unstyled" id="homeSubmenu">
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/viewTimetable');"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Timetable</a>
                                    </li>
                                    <li>
                                        <a href="#homeSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-edit"></i>&nbsp;&nbsp;Edit Timetable</a>
                                        <ul>
                                            <ul class="collapse list-unstyled" id="homeSubmenu1">
                                                <%
                                                    try {
                                                        Class.forName("com.mysql.cj.jdbc.Driver");
                                                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                                        Statement stmt = con.createStatement();
                                                        ResultSet rs = stmt.executeQuery("SELECT labID, name FROM `lab` ORDER BY `labID` ASC");
                                                        while (rs.next()) {
                                                            EnglishNumberToWords a = new EnglishNumberToWords();
                                                            String number = a.convert(rs.getInt(1));
                                                            out.print("<li><a href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a></li>");
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                %>
                                            </ul>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="#stuSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-users"></i>&nbsp;&nbsp;Student Management</a>
                                <ul class="collapse list-unstyled" id="stuSubmenu">
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editStudent?flow=add');"><i class="fa fa-plus"></i>&nbsp;&nbsp;Add Student</a>
                                    </li>
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editStudent?flow=del');"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Student</a>
                                    </li>
                                    <li>
                                        <a href="#stuSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-edit"></i>&nbsp;&nbsp;Edit Student Data</a>
                                        <ul>
                                            <ul class="collapse list-unstyled" id="stuSubmenu1">
                                                <%
                                                    try {
                                                        Class.forName("com.mysql.cj.jdbc.Driver");
                                                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                                        Statement stmt = con.createStatement();
                                                        ResultSet rs = stmt.executeQuery("SELECT classID, class FROM `class` ORDER BY `class` ASC");
                                                        while (rs.next()) {
                                                            EnglishNumberToWords a = new EnglishNumberToWords();
                                                            String number = a.convert(rs.getInt(1));
                                                            out.print("<li><a href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a></li>");
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                %>
                                            </ul>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="#admSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-chalkboard-teacher"></i>&nbsp;&nbsp;Faculty Management</a>
                                <ul class="collapse list-unstyled" id="admSubmenu">
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editFaculty?flow=add');"><i class="fas fa-user-plus"></i>&nbsp;&nbsp;Add Faculty</a>
                                    </li>
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editFaculty?flow=del');"><i class="fas fa-user-minus"></i>&nbsp;&nbsp;Delete Faculty</a>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="#subSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-list-alt"></i>&nbsp;&nbsp;Subject Management</a>
                                <ul class="collapse list-unstyled" id="subSubmenu">

                                    <li>
                                        <a href="javascript:setContent('/Cerberus/viewSubject');"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Subject</a>
                                    </li>
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editSubject?flow=add');"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Add Subject</a>
                                    </li>
                                    <li>
                                        <a href="javascript:setContent('/Cerberus/editSubject?flow=del');"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Subject</a>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="#"><i class="fas fa-address-card"></i>&nbsp;&nbsp;Student Progression</a>
                            </li>
                        </ul>
                    </nav>
                    <div id="content">
                        <nav class="navbar navbar-expand bg-light">
                            <div class="container example">
                                <button type="button" id="sidebarCollapse" style="background-color:#000000;" class="btn">
                                    <font style="color: #ffffff"><i class="fas fa-bars"></i></font>
                                        <img src="images/logo-circle.png"  height="42" width="42">
                                    </button>

                                    <font class="toBeReplaced" style="font-size: 17px; color: #000000; font-family: DPSDbeyond;"> Cerberus Attendance Management System </font>

                                <a class="nav-link" href="/Cerberus/signout"><button type="button" style="background-color:#000000;" class="btn"><span><font style="color: #ffffff">Sign out</font></span>
                                            <font style="color: #ffffff"><i class="fas fa-door-open"></i></font>
                                        </button></a>

                                </div>
                            </nav>

                            <%
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                try {
                                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                    PreparedStatement ps2 = con.prepareStatement("SELECT name, photo FROM faculty WHERE facultyID = ?");
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
                                                + "<img style='border-radius: 10%;' src='data:image/png;base64," + imgString + "'/>"
                                                + "<br><br>" + name + "<br><br></a>\";</script>");
                                    }
                                } catch (Exception e) {
                                }
                            %>
                            <div id='main' style='display: none;' align='center'> 
