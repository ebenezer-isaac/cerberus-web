</div></div>
<!-- /.container-fluid -->

<!-- Sticky Footer -->
<footer class="sticky-footer">
    <div class="container my-auto">
        <div class="copyright text-center my-auto">
            <span>Copyright © Cerberus 2019</span>
        </div>
    </div>
</footer>

</div>
<!-- /.content-wrapper -->

</div>
<!-- /#wrapper -->

<!-- Scroll to Top Button-->
<a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
</a>

<!-- Logout Modal-->
<div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">Select "Logout" below to end your current session.</div>
            <div class="modal-footer">
                <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
                <a class="btn btn-primary" href="/Cerberus/signout">Logout</a>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap core JavaScript-->
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Core plugin JavaScript-->
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>

<!-- Page level plugin JavaScript-->
<script src="vendor/chart.js/Chart.min.js"></script>
<script src="vendor/datatables/jquery.dataTables.js"></script>
<script src="vendor/datatables/dataTables.bootstrap4.js"></script>

<!-- Custom scripts for all pages-->
<script src="js/sb-admin.min.js"></script>

<!-- Demo scripts for this page-->
<script src="js/demo/datatables-demo.js"></script>
<script src="js/demo/chart-area-demo.js"></script>
<script>
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
        document.getElementById('studbtn2').onclick = function () {
            alert('asdf');
        };

        window.onbeforeunload = function () {
            return "Your work will be lost.";
        };

    <%try {
            String url = request.getAttribute("url").toString();
            out.print("setContent('/Cerberus/" + url + "');");
        } catch (Exception e) {
        }
    %>
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
</script></body></html>

