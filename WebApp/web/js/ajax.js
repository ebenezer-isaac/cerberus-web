var mains = document.getElementById('main');
var url_main;
function setContent(url) {
    $("#main").html("<br><br><br><br><br>"
            + "<div class='loader'>"
            + "<div class='duo duo1'>"
            + "<div class='dot dot-a'></div>"
            + "<div class='dot dot-b'></div>"
            + "</div>"
            + "<div class='duo duo2'>"
            + "<div class='dot dot-a'></div>"
            + "<div class='dot dot-b'></div>"
            + "</div>"
            + "</div>");
    url_main = url;
    if (window.XMLHttpRequest) {
        request = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    try {
        request.onreadystatechange = getInfoSession;
        request.open("GET", "/Cerberus/ajaxCheckSession", true);
        request.send();
    } catch (e) {
        alert("Unable to connect to server");
    }

}

function getInfoSession() {
    if (request.readyState == 4) {
        var val = request.responseText;
        if (val == 2) {
            window.location.replace("/Cerberus/");
        } else {
            setContent_main(url_main);
        }
    }
}


function setContent_main(url) {
    temp = url.replace('/Cerberus/', '/Cerberus/ajaxContent?url=');
    window.history.pushState({}, 'Previous', temp);
    if (his[his.length - 1] != url)
    {
        his.push(url);
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
        if (val.length > 3) {
            $("#main").html("");
            $("#main").html(val);
            unfade(mains);
        }
    }
}

var btnstatus1 = 1;
var btnstatus2 = 1;
var btnstatus3 = 1;
var btnstatus4 = 1;
var btnstatus6 = 1;

function checkValidations(x) {
    var url;
    id = x;
    if (x == 0 || x == 4) {
        v = document.getElementById('email').value;
        url = "ajaxCheckEmail?email=" + v;
    } else if (x == 1) {
        v = document.getElementById('prn').value;
        url = "ajaxCheckPRN?prn=" + v;
    } else if (x == 2) {
        v = document.getElementById('roll').value;
        cl = document.getElementById('clas').selectedIndex;
        url = "ajaxCheckRoll?roll=" + v + "&clas=" + cl;
    } else if (x == 3) {
        v = document.getElementById('subid').value;
        url = "ajaxSubjectId?subjectid=" + v;
    }
    if (window.XMLHttpRequest) {
        request = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    try {
        request.onreadystatechange = getValidations;
        request.open("GET", url, true);
        request.send();
    } catch (e) {
        alert("Unable to connect to server");
    }
}

function getValidations() {
    if (request.readyState == 4) {
        var val = request.responseText;
        if (id == 0) {
            if (val == 0) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i><span class='tooltiptext'>Email not valid</span>";
                document.getElementById('email').style.borderColor = "red";
                btnstatus1 = 1;
            } else if (val == 1) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i><span class='tooltiptext'>Email valid</span>";
                document.getElementById('email').style.borderColor = "green";
                btnstatus1 = 0;
            } else if (val == 2) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i><span class='tooltiptext'>Email already registered</span>";
                document.getElementById('email').style.borderColor = "red";
                btnstatus1 = 1;
            }
        } else if (id == 1) {
            if (val == 0) {
                document.getElementById('disp2').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i><span class='tooltiptext'>PRN not valid</span>";
                document.getElementById('prn').style.borderColor = "red";
                btnstatus2 = 1;
            } else if (val == 1) {
                document.getElementById('disp2').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i><span class='tooltiptext'>PRN valid</span>";
                document.getElementById('prn').style.borderColor = "green";
                btnstatus2 = 0;
            } else if (val == 2) {
                document.getElementById('disp2').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i><span class='tooltiptext'>PRN already registered</span>";
                document.getElementById('prn').style.borderColor = "red";
                btnstatus2 = 1;
            }
        } else if (id == 2) {
            if (val == 0) {
                document.getElementById('disp3').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i><span class='tooltiptext'>Roll Number not valid</span>";
                document.getElementById('roll').style.borderColor = "red";
                btnstatus3 = 1;
            } else if (val == 1) {
                document.getElementById('disp3').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i><span class='tooltiptext'>Roll Number valid</span>";
                document.getElementById('roll').style.borderColor = "green";
                btnstatus3 = 0;
            } else if (val == 2) {
                document.getElementById('disp3').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i><span class='tooltiptext'>Roll Number already registered</span>";
                document.getElementById('roll').style.borderColor = "red";
                btnstatus3 = 1;
            }
        } else if (id == 3) {
            if (val == 0) {
                document.getElementById('subid').style.borderColor = "green";
                btnstatus4 = 0;
            } else if (val == 1) {
                document.getElementById('subid').style.borderColor = "red";
                btnstatus4 = 1;
            }
        } else if (id == 4) {
            if (val == 0) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i><span class='tooltiptext'>Email not valid</span>";
                document.getElementById('email').style.borderColor = "red";
                btnstatus6 = 1;
            } else if (val == 1) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-check' aria-hidden='true'></i><span class='tooltiptext'>Email valid</span>";
                document.getElementById('email').style.borderColor = "green";
                btnstatus6 = 0;
            } else if (val == 2) {
                document.getElementById('disp1').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i><span class='tooltiptext'>Email already registered</span>";
                document.getElementById('email').style.borderColor = "red";
                btnstatus6 = 1;
            }
        }
        if (id == 3) {
            if (btnstatus4 == 0) {
                document.getElementById('studbtn1').disabled = false;
                document.getElementById('validations').innerHTML = '<br>';
            } else {
                document.getElementById('studbtn1').disabled = true;
                document.getElementById('validations').innerHTML = 'Subject Code Already Registered';
            }
        } else if (id == 4) {
            if (btnstatus6 == 0) {
                document.getElementById('facbtn1').disabled = false;
                document.getElementById('validations').innerHTML = '<br>';
            } else {
                document.getElementById('facbtn1').disabled = true;
                document.getElementById('validations').innerHTML = 'Email Validation Error';
            }
        } else {
            var index = document.getElementById('clas').selectedIndex;
            if (index != 0) {
                if (btnstatus1 == 0 && btnstatus2 == 0 && btnstatus3 == 0) {
                    document.getElementById('studbtn1').disabled = false;
                    document.getElementById('validations').innerHTML = '<br>';
                } else {
                    document.getElementById('studbtn1').disabled = true;
                    document.getElementById('validations').innerHTML = 'Validations Error';
                }
            } else {
                document.getElementById('studbtn1').disabled = true;
                document.getElementById('validations').innerHTML = 'Validations Error';
            }
        }
    }
}

var btnstatus4 = 0;
function checkPRN() {
    v = document.getElementById('prn').value;
    var url = "ajaxCheckPRN?prn=" + v;
    if (window.XMLHttpRequest) {
        request = new XMLHttpRequest()
    } else if (window.ActiveXObject) {
        request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    try {
        request.onreadystatechange = getInf;
        request.open("GET", url, true);
        request.send();
    } catch (e) {
        alert("Unable to connect to server");
    }
}

function getInf() {
    if (request.readyState == 4) {
        var val = request.responseText;
        var name = val.split(",");
        val = name[0];
        name = name[1];
        if (val == 2) {
            document.getElementById('disp4').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i><span class='tooltiptext'>Student found</span>";
            document.getElementById('prn').style.borderColor = "green";
            btnstatus4 = 1;

        } else {
            document.getElementById('disp4').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i><span class='tooltiptext'>No Student with that PRN found</span>";
            document.getElementById('prn').style.borderColor = "red";
            btnstatus4 = 0;
        }
        if (btnstatus4 == 1) {
            document.getElementById('studbtn2').disabled = false;
            document.getElementById('validations').innerHTML = '' + name;
        } else {
            document.getElementById('studbtn2').disabled = true;
            document.getElementById('validations').innerHTML = 'Validations Error';
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
window.onload = function () {
    var labels = document.getElementsByTagName('body');
    for (var i = 0; i < labels.length; i++) {
        labels[i].classList.add("unselectable");
    }
};
function disableSelection(element) {

    if (typeof element.onselectstart != 'undefined') {
        element.onselectstart = function () {
            return false;
        };
    } else if (typeof element.style.MozUserSelect != 'undefined') {
        element.style.MozUserSelect = 'none';
    } else {
        element.onmousedown = function () {
            return false;
        };
    }
}
function unfade(element) {
    var op = 0.1; // initial opacity

    var timer = setInterval(function () {
        if (op >= 1) {
            clearInterval(timer);
        }
        element.style.opacity = op;
        element.style.filter = 'alpha(opacity=' + op * 100 + ")";
        element.style.display = 'block';
        op += op * 0.08;
    }, 10);
}
function zeroPad(num) {
    var s = num + '';
    while (s.length < 3) {
        s = '0' + s;
    }
    return(s);
}