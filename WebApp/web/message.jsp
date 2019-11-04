<%@ page import = "java.io.*,java.util.*" %>
<%
    int access = 2;
    try {
        access = Integer.parseInt(session.getAttribute("access").toString());
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println(access);
    }
    String redirect = "", head = "", body = "", fullpage = "", sec = "", type = "", url = "";
    try {
        type = request.getAttribute("type").toString();
    } catch (Exception e) {
        try {
            type = request.getParameter("type").toString();
        } catch (Exception d) {
            type = "";
            d.printStackTrace();
        }
        e.printStackTrace();
    }
    System.out.println(type);
    if (type.equals("login0")) {
        System.out.println(0);
        redirect = "false";
        head = "Security Firewall";
        body = "Invalid Username or Password. Please check your credentials and try again";
        url = "index.jsp";
        fullpage = "false";
    } else if (type.equals("login1")) {
        System.out.println(1);
        if (access == 1 || access == 0) {
            redirect = "true";
            head = "Login Successfull";
            body = "Populating your profile";
            url = "homepage";
            sec = "2";
            fullpage = "true";
        } else {
            redirect = "true";
            head = "Security Firewall";
            body = "Please login to continue";
            url = "index.jsp";
            fullpage = "false";
            sec = "2";
        }
    } else if (type.equals("login2")) {
        System.out.println(2);
        redirect = "true";
        fullpage = "false";
        head = "Nice Try!";
        body = "You're smart.<br>But not half as smart enough.<br><br>" + new String(Character.toChars(0x1F60F));
        url = "index.jsp";
        sec = "2";
    } else if (type.equals("login3")) {
        System.out.println(2);
        redirect = "true";
        fullpage = "false";
        head = "Security Firewall";
        body = "You have exceeded allowed number of login attempts";
        url = "index.jsp";
        sec = "2";
    } else {
        redirect = request.getAttribute("redirect").toString();
        head = request.getAttribute("head").toString();
        body = request.getAttribute("body").toString();
        fullpage = request.getAttribute("fullpage").toString();
        url = request.getAttribute("url").toString();
        sec = request.getAttribute("sec").toString();
    }
    System.out.println("redirect:" + redirect);
    System.out.println("head:" + head);
    System.out.println("body:" + body);
    System.out.println("fullpage:" + fullpage);
    System.out.println("url:" + url);
    System.out.println("sec:" + sec);
    int ajax = 0;
    if (fullpage.equals("false") && redirect.equals("true")) {
        response.setHeader("Refresh", sec + ";url=" + url + "");
    } else if (fullpage.equals("true") && redirect.equals("true")) {
        response.setHeader("Refresh", sec + ";url=ajaxContent?url=" + url + "");
    } else if (fullpage.equals("true") && redirect.equals("false")) {
        ajax = 1;
    }
%>

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
        <link rel="stylesheet" href="fontawesome/fontawesome-free-5.10.1-web/css/all.css">
        <link rel="icon" href="images/logo-circle.png" type="image/gif">
        <style>
        </style>
    </head>
    <body style="background-color: #black;">
        
        <div class="container" >
            <div class="reset_page" >
                <div class="form_reset">

                    <div align="center">
                        <h2 style="font-family: arno pro caption"> <%out.print(head);%> </h2>
                        <br>
                        <p><%out.print(body);%></p>

                        <%if (redirect.equals("true")) {
                                out.print("<small><b>You will be redirected shortly</b></small>"
                                        + "<div class='loader'>"
                                        + "<div class='duo duo1'>"
                                        + "<div class='dot dot-a'>"
                                        + "</div>"
                                        + "<div class='dot dot-b'>"
                                        + "</div>"
                                        + "</div>"
                                        + "<div class='duo duo2'>"
                                        + "<div class='dot dot-a'>"
                                        + "</div>"
                                        + "<div class='dot dot-b'>"
                                        + "</div>"
                                        + "</div>"
                                        + "</div>");
                            } else {
                                out.print("<br><form action='");
                                if (ajax == 1) {
                                    out.print("ajaxContent' method='post'>"
                                            + "<input name='url' type='text' value='" + url + "' hidden>");
                                } else {
                                    out.print(url + "' method='post'>");
                                }
                                out.print(""
                                        + "<button type ='submit' autofocus>Accept</button>"
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