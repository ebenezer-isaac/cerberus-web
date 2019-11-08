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
        </style>
    </head>
    <body id="page-top">
        <nav class="navbar navbar-expand navbar-dark bg-dark static-top">
            <img src="images/logomain.png"  height="27" align='center' width="27">
            <a class="navbar-brand mr-1" href="index.html">Cerberus</a>
            <button class="btn btn-link btn-sm text-white order-1 order-sm-0" id="sidebarToggle" href="#">
                <i class="fas fa-bars"></i>
            </button>
            <form class="d-none d-md-inline-block form-inline ml-auto mr-0 mr-md-3 my-2 my-md-0">

            </form>
            <!-- Navbar -->
            <ul class="navbar-nav ml-auto ml-md-0">
                <li class="nav-item dropdown no-arrow">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div id='pic'></div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                        <a class="dropdown-item" href="javascript:setContent('/Cerberus/profile');">Settings</a>
                        <a class="dropdown-item" href="#">Activity Log</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="#" data-toggle="modal" data-target="#logoutModal">Logout</a>
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
                                PreparedStatement ps1 = con.prepareStatement("select subject.subjectID, subject.Abbreviation from studentsubject "
                                        + "inner join subject "
                                        + "on subject.subjectID=studentsubject.subjectID "
                                        + "where PRN = ?");
                                ps1.setString(1, session.getAttribute("user").toString());
                                ResultSet rs = ps1.executeQuery();
                                int index = 1;
                                while (rs.next()) {
                                    out.print("<a class='dropdown-header' href=\"javascript:setContent('/Cerberus/studSubAttendance?sub=" + rs.getString(1) + "');\"><i class='fas fa-chevron-right'></i>&nbsp;&nbsp;" + rs.getString(2) + "</a>");
                                    index++;
                                }
                                con.close();
                                if (index == 1) {
                                    out.print("<a class='dropdown-header' href=''><i class='fas fa-book'></i>&nbsp;&nbsp;No Subjects Available</a>");
                                }
                            } catch (Exception e) {
                            }
                        %>
                    </div>
                </li>
            </ul>
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
                        out.print("<script>document.getElementById('pic').innerHTML=\"" + name
                                + "&nbsp&nbsp<img style='border-radius: 50%;' height='30px'src='data:image/png;base64," + imgString + "'/>\";"
                                + "var name='';</script>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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