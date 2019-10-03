<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Message</title>
    </head>
    <body>
        <fieldset>
            <legend>Message from Server</legend>
            <p><%out.print((request.getAttribute("message")).toString());%></p>
        </fieldset>
        <br>
        <div align="center">
            <form action="<%out.print(request.getAttribute("redirect"));%>" method="post">
                <input type ="submit" value="Accept">   
            </form>
        </div>
    </body>
</html>
