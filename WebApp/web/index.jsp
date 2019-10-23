<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title> Login </title>
        <style> html {overflow: hidden;} </style>
        <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.min.css" type="text/css">
        <link rel="stylesheet" href="css/custom.css" type="text/css">
        <link rel="icon" href="images/logo-circle-removebg.png" type="image/gif">
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <script>
            function sessionstart() {
                var request;
                var url = "sessionStart";
                if (window.XMLHttpRequest) {
                    request = new XMLHttpRequest();
                } else if (window.ActiveXObject) {
                    request = new ActiveXObject('Microsoft.XMLHTTP');
                }
                try {
                    request.onreadystatechange = getInfo;
                    request.open('GET', url, true);
                    request.send();
                } catch (e) {
                }
            }
            function getInfo() {
                if (request.readyState == 4) {
                    var val = request.responseText;
                }
            }
        </script>
    </head>
    <body onload="sessionstart()">
        <nav class="navbar navbar-expand" style="background-color: #24292E;">
            <div class="container">
                <img class="custom_logo" src="images/logo-circle.png">
                <div class="collapse navbar-collapse">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item">
                            <a href="aboutus.html"> <button class="aboutus-button"> About us </button> </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="main_body">
            <div class="container custom_container">
                <div class="row custom_row">
                    <div class="col-lg-6 my-auto">
                        <div class="main_content mx-auto">
                            <h1 style="font-family: arno pro caption"> C E R B E R U S </h1>
                            <!--  <h2 style="font-size: 14.3px;">
                                  C<small><font style="color: darkgray">OGNITIVE</font></small>
                                  E<small><font style="color: darkgray">FFICIENT</font></small>
                                  R<small><font style="color: darkgray">OBUST</font></small>
                                  B<small><font style="color: darkgray">IOMETRIC</font></small>
                                  E<small><font style="color: darkgray">VALUATION</font></small>
                                  R<small><font style="color: darkgray">EPORTING</font></small>
                                  U<small><font style="color: darkgray">TILITY</font></small>
                                  S<small><font style="color: darkgray">YSTEM</font></small> </h2>
                            -->
                            <h3> The Attendance Initiative </h3>
                        </div>
                    </div>
                    <div class="col-lg-6 my-auto">
                        <div class="form_container">
                            <form method="post" action="login">
                                <div class="form-group">
                                    <input type="email" placeholder="Email" name="email" id="email" required>
                                </div>
                                <div class="form-group">
                                    <input type="password" placeholder="Password" name="password" title="Minimum 8 characters and maximum 12 characters" required>
                                </div>
                                <button type="submit"> Login </button>
                                <p class="message"> Forgot your password? 
                                    <a href="#" onclick="return transfer_data()"> 
                                        <b> Create a new password </b> 
                                    </a>
                                </p>
                            </form>
                            <form method="post" class="recaptcha_form" style="display: none;">
                                <div class="form-group">
                                    <input type="email" placeholder="Email" name="emailid" id="emailid" required>
                                </div>
                                <div class="form-group custom_group">
                                    <div class="g-recaptcha" data-sitekey="6Ldki7oUAAAAALfLMRYy4n45X2jxl1r7dol3BbDM"></div>
                                </div>
                                <p class="link"> 
                                    <a href="resetpassword.html"> 
                                        <b> I already have an OTP </b> 
                                    </a> 
                                </p>
                                <button type="submit"> Send OTP </button>
                                <p class="message"> Want to sign in?
                                    <a href="#"> 
                                        <b> Sign in </b>
                                    </a> 
                                </p>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="particles-js"></div>
        <div class="container">
            <div class="modal custom_modal_recaptcha" id="modalrecaptcha">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <div class="modal-body">
                            <h4 style="font-size: 14px;"> We think you are a robot. Prove us wrong if you think if you are not. </h4>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-success" onclick="closemodalrecaptcha()"> OK </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <script src="js/main.js"></script>
        <script src="js/particles.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>                
<%
    java.util.Date date = new java.util.Date();
    SimpleDateFormat ft = new SimpleDateFormat("w");
    int week = Integer.parseInt(ft.format(date));
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
    if (calendar.get(Calendar.DAY_OF_WEEK) <= 2) {
        week--;
    }
    session.setAttribute("week", week);
    session.setAttribute("access", 2);
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cerberus?zeroDateTimeBehavior=convertToNull", "root", "");
        PreparedStatement ps = con.prepareStatement("select class from class");
        ps.executeQuery();
        con.close();
    } catch (SQLException e) {
    }
%>