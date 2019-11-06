$(document).ready(function () {
    function ValidateEmail(mail)
    {
        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail))
        {
            return (true)
        }
        return (false)
    }
    var emailtf = document.getElementById("email");
    emailtf.addEventListener("keydown", function (e) {
        if (e.keyCode === 13) {
            document.getElementById("loginbtn").click();
        }
    });
    var passtf = document.getElementById("pass");
    passtf.addEventListener("keydown", function (e) {
        if (e.keyCode === 13) {
            document.getElementById("loginbtn").click();
        }
    });
    $('#loginbtn').click(function () {
        $('.alert').fadeIn(500);
        document.getElementById("mess").innerHTML = "<div class='loader-s'><div class='duo-s duo1-s'><div class='dot-s dot-a-s'></div><div class='dot-s dot-b-s'></div></div><div class='duo-s duo2-s'><div class='dot-s dot-a-s'></div><div class='dot-s dot-b-s'></div></div></div>";
        var mail = document.getElementById('email').value;
        var pwd = document.getElementById('pass').value;
        if (!ValidateEmail(mail))
        {
            $('.alert').fadeOut(500);
            document.getElementById("mess").innerHTML = "Invalid Email Address";
            $('.mail-tf').addClass('wrong-entry');
            $('.alert').fadeIn(500);
            setTimeout("$('.alert').fadeOut(1500);", 3000);
            $('.mail-tf').keypress(function () {
                $('.mail-tf').removeClass('wrong-entry');
            });
        } else
        {
            $.post("login",
                    {
                        email: mail,
                        pwd: pwd
                    },
                    function (data, status) {
                        if (data == 1 || data == 2 || data == 3)
                        {
                            window.location.replace("message.jsp?type=login" + data);
                        } else
                        {
                            $('.alert').fadeOut(500);
                            document.getElementById("mess").innerHTML = "Incorrect Username or Password";
                            $('.pass-tf').addClass('wrong-entry');
                            $('.mail-tf').addClass('wrong-entry');
                            $('.alert').fadeIn(500);
                            setTimeout("$('.alert').fadeOut(1500);", 3000);
                            $('.pass-tf').keypress(function () {
                                $('.pass-tf').removeClass('wrong-entry');
                            });
                            $('.mail-tf').keypress(function () {
                                $('.mail-tf').removeClass('wrong-entry');
                            });
                        }

                    });
        }
    });
    $('.form-control').keypress(function () {
        $('.log-status').removeClass('wrong-entry');
    });

});