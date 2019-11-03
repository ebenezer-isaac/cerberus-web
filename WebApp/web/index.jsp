<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
<%@page import="cerberus.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    try {
        int access;
        try {
            access = Integer.parseInt(session.getAttribute("access").toString());
            if (access == 0 || access == 1) {
                RequestDispatcher rd = request.getRequestDispatcher("ajaxContent?url=homepage");
                rd.forward(request, response);
            }
        } catch (Exception e) {
            //Performance Tweaks
            java.util.Date date = new java.util.Date();
            SimpleDateFormat ft = new SimpleDateFormat("w");
            int week = Integer.parseInt(ft.format(date));
            session.setAttribute("week", week);
            request.getRequestDispatcher("newTimetable?week=" + week + "&pwd=cerberus@123").include(request, response);
            session.setAttribute("access", 2);
        }
    } catch (Exception m) {
        m.printStackTrace();
    }

%>
<html lang="en" >
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title> Login Shake Effect </title>
        <style> html {overflow: hidden;} </style>
        <title>Login form shake effect</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="css/shakeEffect.css">
        <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.css" type="text/css">
        <link rel="stylesheet" href="css/bootstrap-grid.min.css" type="text/css">
        <link rel="stylesheet" href="css/anim.css" type="text/css">
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    </head>
    <body>
        <div class="main_body">
            <div class="container custom_container">
                <div class="row custom_row">
                    <div class="col-lg-6 my-auto">
                        <div class="main_content mx-auto">
                            <img src="images/logo-main.png" style="width: 101px; height: 101px;"><h1 style="font-family: DPSDbeyond;"> C E R B E R U S </h1>
                            <h3> The Attendance Initiative </h3>
                        </div>
                    </div>

                    <div class="col-lg-6 my-auto">
                        <div class="form_container">

                            <form>
                                <div class="form-group mail-tf">
                                    <input type="email" id='email' class="form-control" name="email" placeholder="Email" required>
                                    <i class="fa fa-user"></i>
                                </div>
                                <div class="form-group pass-tf">
                                    <input type="password" class="form-control active" id="pass" placeholder="Password" title="Minimum 8 characters and maximum 12 characters" required>
                                    <i id="icon" class="fa fa-eye-slash"></i>
                                </div>
                                <span id = 'mess' class="alert">Invalid Password</span><br>
                                <button type="button" class="log-btn" id='loginbtn'>Login</button>
                                <p class="message"> Forgot your password? 
                                    <a href="#" onclick="return transfer_data()"> 
                                        <b> Create a new password </b> 
                                    </a>
                                </p>
                            </form>

                            <form method="post" class="recaptcha_form" style="display: none;">
                                <div class="form-group">
                                    <input type="email" placeholder="Email" class="form-control" name="emailid" id="emailid" required>
                                    <i class="fa fa-user"></i>
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
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <script src="js/scriptShake.js"></script>
        <script src="js/main.js"></script>
        <script src="js/particles.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>