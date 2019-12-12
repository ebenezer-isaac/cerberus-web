<br><br>
</div></div>
</div>
</div>
<a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
</a>
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
                <form action='signout' method='post'>
                    <button type='submit' class="btn btn-primary" id='logout-btn'>Logout</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="vendor/chart.js/Chart.min.js"></script>
<script src="vendor/datatables/jquery.dataTables.js"></script>
<script src="vendor/datatables/dataTables.bootstrap4.js"></script>
<script src="js/sb-admin.min.js"></script>
<script src="js/ajax.js"></script>
<script>
    var his = [" ", "/Cerberus/homepage"];
    console.log(his);
    $(document).ready(function () {
    <%
        try {
            String url = request.getAttribute("url").toString();
            out.print("setContent('/Cerberus/" + url + "');");
        } catch (Exception e) {
        }
    %>
    });
    window.addEventListener('popstate', function (event) {
        url = his[his.length - 2];
        his.pop();
        if (his.length <= 1) {
            $("#profile-menu").click();
            $("#logout-modal").click();
        } else {
            setContent(url);
            history.pushState(null, null, window.location.pathname);
        }
    }, false);
    window.addEventListener('unload', function (event) {
        url = his[his.length - 2];
        his.pop();
        if (his.length <= 1) {
            $("#profile-menu").click();
            $("#logout-modal").click();
        } else {
            setContent(url);
            history.pushState(null, null, window.location.pathname);
        }
    }, false);
</script></body></html>

