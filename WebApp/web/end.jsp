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
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="vendor/chart.js/Chart.min.js"></script>
<script src="vendor/datatables/jquery.dataTables.js"></script>
<script src="vendor/datatables/dataTables.bootstrap4.js"></script>
<script src="js/sb-admin.min.js"></script>
<script src="js/ajax.js"></script></body></html>
<script>
    $(document).ready(function () {
    <%
        try {
            String url = request.getAttribute("url").toString();
            out.print("setContent('/Cerberus/" + url + "');");
        } catch (Exception e) {
        }
    %>
        window.addEventListener('popstate', function (event) {
            // The popstate event is fired each time when the current history entry changes.

            var r = confirm("You pressed a Back button! Are you sure?!");

            if (r == true) {
                // Call Back button programmatically as per user confirmation.
                alert('asdf');
                //history.back();
                // Uncomment below line to redirect to the previous page instead.
                // window.location = document.referrer // Note: IE11 is not supporting this.
            } else {
                // Stay on the current page.
                history.pushState(null, null, window.location.pathname);
            }
             history.pushState(null, null, window.location.pathname);
        }, false);
    });
</script>

