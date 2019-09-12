// JavaScript Document

//jQuery for form animation
$('.message a').click(function ()
{
    $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});

//for tranfering data of username from one form to another
function transfer_data()
{
    var x = document.getElementById("username").value;
    document.getElementById("user").value = x;
}

//jQuery for Google Recaptcha for sending post request

$(document).ready(function () {
    $(".recaptcha_form").submit(function (event) {
        var recaptcha = $("#g-recaptcha-response").val();
        if (recaptcha === "")
        {
            event.preventDefault();
            $(".custom_modal_recaptcha").show();
        } else
        {
            var form = document.createElement("form");
            form.method = "POST";
            form.action = "otp";
            var textbox = document.createElement("input");
            textbox.type = "hidden";
            textbox.value = document.getElementById("emailid").value;
            textbox.name = "emailadd";
            form.appendChild(textbox);
            document.body.appendChild(form);
            form.submit();
        }
        event.preventDefault();
        $.post("grecaptcha.php", {
            "secret": "6LdcSrIUAAAAAC_PvOrb_Z1m6U_8tr6PmoYqn-cQ",
            "response": recaptcha
        }, function (ajaxResponse) {
            console.log(ajaxResponse);
        });

    });

});

//check new password and confirm password on resetpassword page
function check_password()
{
    var np = document.getElementById("newpass");
    var cp = document.getElementById("conpass");

    if (np.value === cp.value)
    {
        cp.setCustomValidity("");
    } else
    {
        cp.setCustomValidity("Passwords don't match");
    }
    np.onchange = check_password;
    cp.onkeyup = check_password;
}

//for close modal recaptcha
function closemodalrecaptcha()
{
    document.getElementById("modalrecaptcha").style.display = "none";
}