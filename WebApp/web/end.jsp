</div></div></div><script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script><script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js" integrity="sha384-cs/chFZiN24E4KMATLdqdvsezGxaGsi4hLGOzlXwp5UZB1LY//20VyM2taTB4QvJ" crossorigin="anonymous"></script><script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js" integrity="sha384-uefMccjFJAIv6A+rW+L4AHf99KvxDjWSu1z9VI8SKNVmz4sk7buKt/6v9KI65qnm" crossorigin="anonymous"></script><script src="https://cdnjs.cloudflare.com/ajax/libs/malihu-custom-scrollbar-plugin/3.1.5/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript">
    var mains = document.getElementById('main');
    function setContent(url) {
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
    $(document).ready(function () {
    <%try {
            String url = request.getAttribute("url").toString();
            out.print("setContent('/Cerberus/" + url + "');");
        } catch (Exception e) {
        }
    %>
        $("#sidebar").mCustomScrollbar({
            theme: "minimal"
        });
        $('#sidebarCollapse').on('click', function () {
            $('#sidebar, #content').toggleClass('active');
            $('.collapse.in').toggleClass('in');
            $('a[aria-expanded=true]').attr('aria-expanded', 'false');
        });
    });
    var btnstatus1 = 1;
    var btnstatus2 = 1;
    var btnstatus3 = 1;

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
</script></body></html>

