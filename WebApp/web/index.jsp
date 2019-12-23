<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.sql.*"%>
<%@page import="cerberus.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" > <head> <style>.tooltipp .tooltiptext{visibility: hidden; background-color: #0d0d0d; font-size: 12px; color: #fff; text-align: center; border-radius: 6px; padding: 7px; /* Position the tooltip */ z-index: 1;}.tooltipp:hover .tooltiptext{visibility: visible;}body{height: 100%; overflow-y: scroll; -webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; -ms-overflow-style: none;}/* Hide scrollbar for Chrome, Safari and Opera */ ::-webkit-scrollbar{display: none;}/* Hide scrollbar for IE and Edge */ .abt:hover{text-decoration: none;}</style> <meta charset="utf-8"> <meta http-equiv="X-UA-Compatible" content="IE=edge"> <meta name="viewport" content="width=device-width,height=device-height, initial-scale=1"> <title>Login </title> <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css"> <link rel="stylesheet" href="css/shakeEffect.css"> <link rel="stylesheet" href="css/bootstrap.css" type="text/css"> <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css"> <link rel="stylesheet" href="css/bootstrap-grid.css" type="text/css"> <link rel="stylesheet" href="css/bootstrap-grid.min.css" type="text/css"> <link rel="stylesheet" href="css/anim.css" type="text/css"> <link rel="stylesheet" href="css/loader-index.css" type="text/css"> <link rel="stylesheet" href="css/splash.css" type="text/css"> <link rel="icon" href="images/logo-circle-removebg.png" type="image/gif"> <script src='https://www.google.com/recaptcha/api.js?onload=recaptchaOnload&render=explicit' async defer></script> </head> <body class='unselectable'> <div id="demo-content">
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
                    out.print("<div id='splash-wrapper'><div id='splash'></div><div class='splash-section section-left'></div><div class='splash-section section-right'></div></div>");
                }
            } catch (Exception m) {
                m.printStackTrace();
            }
        %>
            <div id="content"><div class="wrapper"><div class="main_body"><div class="container custom_container"><div id='loginform'></div></div></div></div><div id="particles-js"></div><div class="container"><div class="modal custom_modal_recaptcha" id="modalrecaptcha"><div class="modal-dialog modal-sm"><div class="modal-content"><div class="modal-body"><h4 style="font-size: 14px;"> We think you are a robot. Prove us wrong if you think if you are not.</h4></div><div class="modal-footer"> <button type="button" class="btn btn-success" onclick="closemodalrecaptcha()"> OK </button></div></div></div></div></div></div></div><script src="js/particles.js"></script> <script defer src="js/index.js"></script> <script src="js/jquery.min.js"></script><script>var _captchaTries=0;function recaptchaOnload(){_captchaTries++;if(_captchaTries>9) return;if($('.g-recaptcha').length>0){grecaptcha.render("recaptcha",{sitekey:'site key here',callback:function(){console.log('recaptcha callback');}});return;} window.setTimeout(recaptchaOnload,1000);}</script> </body></html>