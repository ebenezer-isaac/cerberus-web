var mains = document.getElementById('main');
function setContent(url) {
    temp = url.replace('/Cerberus/', '/Cerberus/ajaxContent?url=');
    window.history.pushState({}, 'Previous', temp);
    if (his[his.length - 1] != url)
    {
        his.push(url);
    }
    //$("#main").html("<div style='height: 100vh;vertical-align: middle;'><div class='loader'><div class='duo duo1'><div class='dot dot-a'></div><div class='dot dot-b'></div></div><div class='duo duo2'><div class='dot dot-a'></div><div class='dot dot-b'></div></div></div></div>");
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
        $("#main").html("");
        $("#main").html(val);
        unfade(mains);
    }
}
var btnstatus1 = 1;
var btnstatus2 = 1;
var btnstatus3 = 1;
var btnstatus4 = 1;
function checkValidations(x) {
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
    }else if (x == 3) {
        v = document.getElementById('subid').value;
        var url = "ajaxSubjectId?subjectid=" + v;
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
        else if (id == 3) {
            if (val == 0) {
                document.getElementById('subid').style.borderColor="green";
                btnstatus4 = 0;
                
            } else if (val == 1) {
                document.getElementById('subid').style.borderColor="red";
                btnstatus4 = 1;
                
            }
        }
        if (btnstatus1 == 0 && btnstatus2 == 0 && btnstatus3 == 0) {
            document.getElementById('studbtn1').disabled = false;
        } else {
            document.getElementById('studbtn1').disabled = true;
        }
        if (btnstatus4 == 0 ) {
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
        if (val == 2) {
            document.getElementById('disp4').innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>";
            btnstatus4 = 1;
        } else {
            document.getElementById('disp4').innerHTML = "<i class='fa fa-times' aria-hidden='true'></i>";
            btnstatus4 = 0;
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