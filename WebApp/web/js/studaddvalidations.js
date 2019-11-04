var btnstatus1 = 1;
var btnstatus2 = 1;
var btnstatus3 = 1;

function sendInfo(x) {
	id = x;
	if (x == 0) {
		v = document.getElementById('email').value;
		var url = "ajaxCheckEmail?email=" + v;
	} else if (x == 1) {
		v = document.getElementById('prn').value;
		var url = "ajaxCheckPRN?prn=" + v;
	} else if (x == 2) {
		v = document.getElementById('roll').value;
		cl = document.getElementById('clas').selectedIndex;
		var url = "ajaxCheckRoll?roll=" + v + "&clas=" + cl;
	}
	if (window.XMLHttpRequest) {
		request = new XMLHttpRequest();
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
		if (id == 0) {
			if (val == 0) {
				document.getElementById('disp1').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i>";
				btnstatus1 = 1;
			} else if (val == 1) {
				document.getElementById('disp1').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i>";
				btnstatus1 = 0;
			} else if (val == 2) {
				document.getElementById('disp1').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>";
				btnstatus1 = 1;
			}
		} else if (id == 1) {
			if (val == 0) {
				document.getElementById('disp2').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i>";
				btnstatus2 = 1;
			} else if (val == 1) {
				document.getElementById('disp2').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i>";
				btnstatus2 = 0;
			} else if (val == 2) {
				document.getElementById('disp2').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>";
				btnstatus2 = 1;
			}
		} else if (id == 2) {
			if (val == 0) {
				document.getElementById('disp3').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i>";
				btnstatus3 = 1;
			} else if (val == 1) {
				document.getElementById('disp3').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i>";
				btnstatus3 = 0;
			} else if (val == 2) {
				document.getElementById('disp3').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>";
				btnstatus3 = 1;
			}
		}
		if (btnstatus1 == 0 && btnstatus2 == 0 && btnstatus3 == 0) {
			document.getElementById('studbtn1').disabled = false;
		} else {
			document.getElementById('studbtn1').disabled = true;
		}
	}
}

function batchdisable(id) {
	if (document.getElementById('subject' + id).checked == true) {
		document.getElementById('batch' + id).selectedIndex = 1;
		document.getElementById('batch' + id).disabled = false;
		document.getElementById('batch' + id).classList.remove('not-allowed');
	} else {
		document.getElementById('batch' + id).selectedIndex = 0;
		document.getElementById('batch' + id).disabled = true;
		document.getElementById('batch' + id).classList.add('not-allowed');
	}
}
var request;
var id;

function subsdisable(id) {
	var index = document.getElementById(id).selectedIndex;
	if (index == 0) {
		id = id.substr(5);
		document.getElementById('subject' + id).checked = false;
	}
	document.getElementById('batch' + id).disabled = true;
	document.getElementById('batch' + id).classList.add('not-allowed');
}