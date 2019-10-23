<%@ page import = "java.io.*,java.util.*" %>
<%if ((request.getAttribute("redirect")).equals("true")) {
        response.setHeader("Refresh", request.getAttribute("sec")+";url=" + request.getAttribute("url"));
    } %>

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
        <link rel="stylesheet" href="fontawesome/fontawesome-free-5.10.1-web/css/all.css">
        <link rel="icon" href="images/logo-circle.png" type="image/gif">
        <style>
            .loader {
                position: relative;
                width: 80px;
                margin: 100px auto;
            }
            .duo {
                height: 20px;
                width: 50px;
                background: hsla(0, 0%, 0%, 0.0);
                position: absolute;

            }
            .duo, .dot {
                animation-duration: 0.8s;
                animation-timing-function: ease-in-out;
                animation-iteration-count: infinite;
            }
            .duo1 {
                left: 0;
            }
            .duo2 {
                left: 30px
            }
            .dot {
                width: 20px;
                height: 20px;
                border-radius: 10px;
                border-color: #24af29;
                background: #24af29;
                position: absolute;
            }
            .dot-a {
                left: 0px;  
            }
            .dot-b {
                right: 0px;
            }
            @keyframes spin {
                0% { transform: rotate(0deg) }
                50% { transform: rotate(180deg) }
                100% { transform: rotate(180deg) }
            }
            @keyframes onOff {
                0% { opacity: 0; }
                49% { opacity: 0; }
                50% { opacity: 1; }
                100% { opacity: 1; }
            }
            .duo1 {
                animation-name: spin;
            }
            .duo2 {
                animation-name: spin;
                animation-direction: reverse;
            }
            .duo2 .dot-b {
                animation-name: onOff;
            }
            .duo1 .dot-a {
                opacity: 0;
                animation-name: onOff;
                animation-direction: reverse;
            }    
        </style>
    </head>
    <body style="background-color: #black;">
        <nav class="navbar navbar-expand navbar-light" style="background-color: black;">
            <div class="container">
                <img class="custom_logo" src="images/logo-circle.png"></a>
                <div class="collapse navbar-collapse">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item">
                            <a href="aboutus.html"> <button class="aboutus-button"> About us </button> </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container" >
            <div class="reset_page" >
                <div class="form_reset">
                            
                            <div align="center">
                            <h2 style="font-family: arno pro caption"> <%out.print((request.getAttribute("head")).toString());%> </h2>
                            <br>
                            <p><%out.print((request.getAttribute("body")).toString());%></p>
                            
                                <%if ((request.getAttribute("redirect")).equals("true")) {
                                        out.print("<small><b>You will be redirected shortly</b></small>"
                                                + "<div class=\"loader\">"
                                                + "<div class=\"duo duo1\">"
                                                + "<div class=\"dot dot-a\">"
                                                + "</div>"
                                                + "<div class=\"dot dot-b\">"
                                                + "</div>"
                                                + "</div>"
                                                + "<div class=\"duo duo2\">"
                                                + "<div class=\"dot dot-a\">"
                                                + "</div>"
                                                + "<div class=\"dot dot-b\">"
                                                + "</div>"
                                                + "</div>"
                                                + "</div>");
                                    } else {
                                        out.print("<br><form action=\"" + request.getAttribute("url") + "\" method=\"post\">"
                                                + "<button type =\"submit\" autofocus>Accept</button>"
                                                + "</form>");
                                    }
                                %>
                            </div>
                </div>
            </div>
        </div>
        <div id="particles-js"></div>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <script src="js/main.js"></script>
        <script src="js/particles.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>
