<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
<%@page import="cerberus.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="images/logo-circle-removebg.png" type="image/gif">
        <title>Cerberus</title>
        <!-- Custom fonts for this template-->
        <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
        <!-- Page level plugin CSS-->
        <link href="vendor/datatables/dataTables.bootstrap4.css" rel="stylesheet">
        <!-- Custom styles for this template-->
        <link href="css/sb-admin.css" rel="stylesheet">
        <link href="css/dropdowns.css" rel="stylesheet">
        <style>
            .warning{
                background-color:red;
                color:white;
            }
            .success{
                background-color:green;
                color:white;
            }
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
            /* width */
            ::-webkit-scrollbar {
                width: 10px;
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
                background: #555; 
            }
            .tooltipp .tooltiptext {
                visibility: hidden;
                background-color: #0d0d0d;
                font-size: 12px;
                color: #fff;
                text-align: center;
                border-radius: 6px;
                padding: 7px;
                margin-left:10px;
                margin-top:10px;
                /* Position the tooltip */
                position: absolute;
                z-index: 1;
            }

            .tooltipp:hover .tooltiptext {
                visibility: visible;
            }
        </style>
    </head>
    <body id="page-top">
        <nav class="navbar navbar-expand navbar-dark bg-dark static-top">
            <img src="images/logomain.png"  height="27" align='center' width="27">
            <a class="navbar-brand mr-1" href="index.jsp">Cerberus</a>
            <button class="btn btn-link btn-sm text-white order-1 order-sm-0" id="sidebarToggle" href="#">
                <i class="fas fa-bars"></i>
            </button>
            <form class="d-none d-md-inline-block form-inline ml-auto mr-0 mr-md-3 my-2 my-md-0">

            </form>
            <!-- Navbar -->
            <ul class="navbar-nav ml-auto ml-md-0">
                <li class="nav-item dropdown no-arrow">
                    <a id='profile-menu' class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div id='pic'></div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/profile');">Settings</a>
                        <a class="dropdown-item" href="#">Activity Log</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="#" id='logout-modal' data-toggle="modal" data-target="#logoutModal">Logout</a>
                    </div>
                </li>
            </ul>
        </nav>
        <div id="wrapper">
            <!-- Sidebar -->
            <ul class="sidebar navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="pagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fas fa-fingerprint"></i>
                        <span>Attendance</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="pagesDropdown">
                        <h6 class="dropdown-header" align='center'>View : </h6>
                        <%
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT classID, class FROM `class` ORDER BY `class` ASC");
                                while (rs.next()) {
                                    EnglishNumberToWords a = new EnglishNumberToWords();
                                    String number = a.convert(rs.getInt(1));
                                    out.print("<a class='dropdown-item' href=\"javascript:setContent('/Cerberus/attendance?class=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a>");
                                }
                                con.close();
                            } catch (Exception e) {
                            }
                        %>
                        <div class="dropdown-divider"></div>
                        <h6 class="dropdown-header" align='center'>Management : </h6>
                        <a class="dropdown-item" href="#">404 Page</a>
                        <a class="dropdown-item" href="#">Blank Page</a>
                    </div>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="pagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-calendar-alt"></i>
                        <span>TimeTable</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="pagesDropdown">
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/viewTimetable');"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Timetable</a>
                        <div class="dropdown-divider"></div>
                        <h6 class="dropdown-header" align='center'>Edit :</h6>
                        <%
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT labID, name FROM `lab` ORDER BY `labID` ASC");
                                while (rs.next()) {
                                    EnglishNumberToWords a = new EnglishNumberToWords();
                                    String number = a.convert(rs.getInt(1));
                                    out.print("<a class='dropdown-item' href=\"javascript:setContent('/Cerberus/editTimetable?lab=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a>");
                                }
                                con.close();
                            } catch (Exception e) {
                            }
                        %>
                        <div class="dropdown-divider"></div>
                        <h6 class="dropdown-header" align='center'>Timings :</h6>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editSlot?flow=edit');"><i class="fa fa-edit"></i>&nbsp;&nbsp;Edit Timings</a>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editSlot?flow=add');"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Add Slot</a>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editSlot?flow=del');"><i class="fa fa-times"></i>&nbsp;&nbsp;Remove Slot</a>
                    </div>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="pagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-users"></i>
                        <span>Student</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="pagesDropdown">
                        <h6 class="dropdown-header" align='center'>Details : </h6>
                        <%
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT classID, class FROM `class` ORDER BY `class` ASC");
                                while (rs.next()) {
                                    EnglishNumberToWords a = new EnglishNumberToWords();
                                    String number = a.convert(rs.getInt(1));
                                    out.print("<a class='dropdown-item' href=\"javascript:setContent('/Cerberus/editStudDetails?class=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a>");
                                }
                                con.close();
                            } catch (Exception e) {
                            }
                        %>
                        <div class="dropdown-divider"></div>
                        <h6 class="dropdown-header" align='center'>Subject Selection : </h6>
                        <%
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
                                Statement stmt = con.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT classID, class FROM `class` ORDER BY `class` ASC");
                                while (rs.next()) {
                                    EnglishNumberToWords a = new EnglishNumberToWords();
                                    String number = a.convert(rs.getInt(1));
                                    out.print("<a class='dropdown-item' href=\"javascript:setContent('/Cerberus/editSubSelection?class=" + rs.getInt(1) + "');\"><i class='fas fa-dice-" + number + "'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a>");
                                }
                                con.close();
                            } catch (Exception e) {
                            }
                        %>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editAddStudent');"><i class="fa fa-plus"></i>&nbsp;&nbsp;Add Student</a>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editDelStudent');"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Student</a>
                    </div>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="pagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-list-alt"></i>
                        <span>Subject</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="pagesDropdown">
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/viewSubject');"><i class="fa fa-eye"></i>&nbsp;&nbsp;View Subject</a>
                        <div class="dropdown-divider"></div>
                        <h6 class="dropdown-header" align='center'>Management : </h6>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editSubject?flow=add');"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Add Subject</a>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editSubject?flow=del');"><i class="fa fa-times"></i>&nbsp;&nbsp;Delete Subject</a>
                    </div>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="pagesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-chalkboard-teacher"></i>
                        <span>Faculty</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="pagesDropdown">
                        <h6 class="dropdown-header" align='center'>Management : </h6>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editFaculty?flow=add');"><i class="fas fa-user-plus"></i>&nbsp;&nbsp;Add Faculty</a>
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/editFaculty?flow=del');"><i class="fas fa-user-minus"></i>&nbsp;&nbsp;Delete Faculty</a>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="javascript:setContent('/Cerberus/studentProgression');">
                        <i class="fas fa-address-card"></i>
                        <span>Student Progression</span></a>
                </li>
            </ul>

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
                        out.print("<script>document.getElementById('pic').innerHTML=\"" + name
                                + "&nbsp&nbsp<img style='border-radius:50%;' height='30px'src='data:image/png;base64," + imgString + "'/>\";"
                                + "var name='';</script>");
                    }
                } catch (Exception e) {
                }
            %>
            <div id="content-wrapper">
                <div class="container-fluid">
                    <!-- Breadcrumbs-->
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="#">Dashboard</a>
                        </li>
                        <li class="breadcrumb-item active">Overview</li>
                    </ol>
                    <div id='main' style='display: none;' align='center'> 