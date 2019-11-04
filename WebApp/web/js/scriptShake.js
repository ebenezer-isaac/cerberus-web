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
    $('.log-btn').click(function () {

        var mail = document.getElementById('email').value;
        var pwd = document.getElementById('pass').value;

        if (!ValidateEmail(mail))
        {
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
                        if (data == 1 || data == 2)
                        {
                            window.location.replace("message.jsp?type=login" + data);
                        } else
                        {
                            document.getElementById("mess").innerHTML = "Incorrect Password";
                            $('.pass-tf').addClass('wrong-entry');
                            $('.alert').fadeIn(500);
                            setTimeout("$('.alert').fadeOut(1500);", 3000);
                            $('.pass-tf').keypress(function () {
                                $('.pass-tf').removeClass('wrong-entry');
                            });
                        }

                    });
        }
    });
    $('.form-control').keypress(function () {
        $('.log-status').removeClass('wrong-entry');
    });

});