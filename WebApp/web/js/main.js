$('.message a').click(function ()
{
    $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});

function transfer_data()
{
    var x = document.getElementById("username").value;
    document.getElementById("user").value = x;
}

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
        "secret": "6LcmWcoUAAAAADP_fXbw-uqDMKDQwmxtbAWtNHFX",
        "response": recaptcha
    }, function (ajaxResponse) {
        console.log(ajaxResponse);
    });

});

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

function showPassNew() {
    var x = document.getElementById("newpass");
    var y = document.getElementById("eyen");
    if (x.type === "password") {
        x.type = "text";
        y.innerHTML = "<i class='fa fa-eye'></i>";
    } else {
        x.type = "password";
        y.innerHTML = "<i class='fa fa-eye-slash'></i>";
    }
}

function showPassConf() {
    var x = document.getElementById("conpass");
    var y = document.getElementById("eyec");
    if (x.type === "password") {
        x.type = "text";
        y.innerHTML = "<i class='fa fa-eye'></i>";
    } else {
        x.type = "password";
        y.innerHTML = "<i class='fa fa-eye-slash'></i>";
    }
}