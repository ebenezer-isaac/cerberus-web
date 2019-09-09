$('.message a').click(function()
{
   $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});
function transfer_data()
{
	var x = document.getElementById("username").value;
	document.getElementById("user").value = x;
}
$(document).ready(function() {
	$(".recaptcha_form").submit(function(event) {
		var recaptcha = $("#g-recaptcha-response").val();
		if(recaptcha === "")
		{
			event.preventDefault();
			alert("Please check the recaptcha");
		}
		else{
			alert("OTP has been sent to your registered email account. If not found, please check spam.");
			window.open("passwordreset.html","_self");
		}
		$.post("reset.php", {
				"secret":"6LdcSrIUAAAAAC_PvOrb_Z1m6U_8tr6PmoYqn-cQ",
				"response":recaptcha
		}, function(ajaxResponse) {
			console.log(ajaxResponse);
		});
	});
});
