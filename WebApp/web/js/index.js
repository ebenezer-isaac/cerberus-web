
function setContent() {
    url = "login.jsp"
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
        $("#loginform").html(val);
        $('body').addClass('loaded');
    }
}
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
$(document).ready(function () {
    setContent();
});