var btnstatus4 = 0;

function sendInfo(x) {
	v = document.getElementById('prn').value;
	var url = "ajaxCheckPRN?prn=" + v;
	if (window.XMLHttpRequest) {
		request = new XMLHttpRequest()
	} else if (window.ActiveXObject) {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	try {
		request.onreadystatechange = getInfo;
		request.open("GET", url, true);
		request.send();
	} catch (e) {
		alert("Unable to connect to server");
	}
}

function getInfo() {
	if (request.readyState == 4) {
		var val = request.responseText;
		if (val == 0 || val == 1) {
			document.getElementById('disp4').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i>";
			btnstatus4 = 0;
		} else if (val == 2) {
			document.getElementById('disp4').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>";
			btnstatus4 = 1;
		}
		if (btnstatus4 == 1) {
			document.getElementById('studbtn2').disabled = false;
		} else {
			document.getElementById('studbtn2').disabled = true;
		}
	}
}

function myFunction() {
	if (document.getElementById('warn').checked == true) {
		document.getElementById('butt').style.display = 'block';
	} else {
		document.getElementById('butt').style.display = 'none';
	}
}