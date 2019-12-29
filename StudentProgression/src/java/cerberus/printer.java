package cerberus;

public class printer {

    public static String tablestart(String heading, String type, String name, int datatype) {
        String output = "<div class='card'><div class='card-header'>";
        output += heading;
        output += "</div>"
                + "<div class='card-body'>"
                + "<div class='table-responsive'>"
                + "<style>"
                + "tr{vertical-align : middle;text-align:center;}"
                + "th{vertical-align : middle;text-align:center;}"
                + "td{vertical-align : middle;text-align:center;}"
                + "</style>"
                + "<table class='table table-bordered";
        if (type != null) {
            output += " table-" + type;
        }
        output += "' ";
        if (datatype == 1) {
            output += "id='dataTable' ";
        }
        output += " name='" + name + "' width='100%' cellspacing='0'>";
        return output;
    }

    public static String tablehead(String headers) {
        String output = "<thead>" + headers + "</thead><tfoot>" + headers + "</tfoot>";
        return output;
    }

    public static String tableend(String footer, int datatype) {
        String output = "</table>"
                + "</div>"
                + "</div>"
                + "</div>";
        if (footer != null) {
            output += "<div class='card-footer small text-muted'>" + footer + "</div>";
        }
        if (datatype == 1) {
            output += "</div><script>$('#dataTable').DataTable();</script>";
        }
        return output;
    }

    public static String kids() {
        return "Sorry Kid!!<br><br>You do are not old enough to access this page";
    }

    public static String nouser() {
        return "Sorry!!<br>An active session was not found. Please login again to continue<br>"
                + "<button type='submit' onclick=\"window.location.href='index.jsp'\"class='btn btn-primary'><span>Redirect</span></button>";
    }

    public static String error(String error) {
        return "There was an error<br>" + error;
    }
}
