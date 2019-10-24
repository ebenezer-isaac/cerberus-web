<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
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

        <style>
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
            </style>
        </head>
        <body>
            <div class="wrapper">
                <!-- Sidebar --> 
                <nav id="sidebar" >
                    <ul class="list-unstyled components">
                        <p align="center"><br><a href="/Cerberus/homepage">FACULTY PANEL</a></p>
                        <div id='pic' align='center'></div>
                        <li>
                            <a href="#homeSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-calendar-alt"></i>&nbsp;&nbsp;Timetable Management</a>
                            <ul class="collapse list-unstyled" id="homeSubmenu">
                                <li>
                                    <a href="/Cerberus/viewTimetable"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Timetable</a>
                                </li>
                                <li>
                                    <a href="#homeSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-edit"></i>&nbsp;&nbsp;Edit Timetable</a>
                                    <ul>
                                        <ul class="collapse list-unstyled" id="homeSubmenu1">
                                            <li>
                                                <a href="/Cerberus/editTimetable?lab=1"><i class="fas fa-dice-one"></i>&nbsp;&nbsp;Lab 1</a>
                                            </li>
                                            <li>
                                                <a href="/Cerberus/editTimetable?lab=2"><i class="fas fa-dice-two"></i>&nbsp;&nbsp;Lab 2</a>
                                            </li>
                                            <li>
                                                <a href="/Cerberus/editTimetable?lab=3"><i class="fas fa-dice-three"></i>&nbsp;&nbsp;Lab 3</a>
                                            </li>
                                        </ul>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#subSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-list-alt"></i>&nbsp;&nbsp;Subject Management</a>
                            <ul class="collapse list-unstyled" id="subSubmenu">
                                <li>
                                    <a href="/Cerberus/viewSubject"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Subject</a>
                                </li>
                                <li>
                                    <a href="/Cerberus/editSubject?flow=add"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Add Subject</a>
                                </li>
                                <li>
                                    <a href="/Cerberus/editSubject?flow=del"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Subject</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#stuSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-users"></i>&nbsp;&nbsp;Student Management</a>
                            <ul class="collapse list-unstyled" id="stuSubmenu">
                                <li>
                                    <a href="/Cerberus/editStudent?flow=add"><i class="fa fa-plus"></i>&nbsp;&nbsp;Add Student</a>
                                </li>
                                <li>
                                    <a href="/Cerberus/editStudent?flow=del"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Student</a>
                                </li>
                                <li>
                                    <a href="#stuSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-edit"></i>&nbsp;&nbsp;Edit Student Data</a>
                                    <ul>
                                        <ul class="collapse list-unstyled" id="stuSubmenu1">
                                            <li>
                                                <a href="/Cerberus/editStudDetails?class=1"><i class="fas fa-dice-one"></i>&nbsp;&nbsp;BCA FY</a>
                                            </li>
                                            <li>
                                                <a href="/Cerberus/editStudDetails?class=2"><i class="fas fa-dice-two"></i>&nbsp;&nbsp;BCA SY</a>
                                            </li>
                                            <li>
                                                <a href="/Cerberus/editStudDetails?class=3"><i class="fas fa-dice-three"></i>&nbsp;&nbsp;BCA TY</a>
                                            </li>
                                        </ul>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#attSubmenu1" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fas fa-check"></i>&nbsp;&nbsp;Attendance Management</a>
                            <ul>
                                <ul class="collapse list-unstyled" id="attSubmenu1">
                                    <li>
                                        <a href="/Cerberus/attendance?class=1"><i class="fas fa-dice-one"></i>&nbsp;&nbsp;BCA FY</a>
                                    </li>
                                    <li>
                                        <a href="/Cerberus/attendance?class=2"><i class="fas fa-dice-two"></i>&nbsp;&nbsp;BCA SY</a>
                                    </li>
                                    <li>
                                        <a href="/Cerberus/attendance?class=3"><i class="fas fa-dice-three"></i>&nbsp;&nbsp;BCA TY</a>
                                    </li>
                                </ul>
                            </ul>
                        </li>
                        <li>
                            <a href="#admSubmenu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle"><i class="fa fa-chalkboard-teacher"></i>&nbsp;&nbsp;Faculty Management</a>
                            <ul class="collapse list-unstyled" id="admSubmenu">
                                <li>
                                    <a href="/Cerberus/editFaculty?flow=add"><i class="fas fa-user-plus"></i>&nbsp;&nbsp;Add Faculty</a>
                                </li>
                                <li>
                                    <a href="/Cerberus/editFaculty?flow=del"><i class="fas fa-user-minus"></i>&nbsp;&nbsp;Delete Faculty</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#"><i class="fas fa-address-card"></i>&nbsp;&nbsp;Student Progression</a>
                        </li>
                        <li>
                            <a href="editProfilePage.html"><i class="fas fa-address-card"></i>&nbsp;&nbsp;Profile</a>
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
                                        + "<img src='data:image/png;base64," + imgString + "'/>"
                                        + "<br>" + name + "<br><br></a>\";</script>");
                            }
                        } catch (Exception e) {
                        }
                    %>
