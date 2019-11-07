package cerberus;

public class printer {

    public static String tablestart(String heading, String type, String name, int datatype) {
        String output = "<div class='card-header'>";
        output += heading;
        output += "</div>"
                + "<div class='card-body'>"
                + "<div class='table-responsive'>"
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

    public static String tableend(String footer) {
        String output = "</table>"
                + "</div>"
                + "</div>";
        if (footer != null) {
            output += "<div class='card-footer small text-muted'>" + footer + "</div>";
        }
        output += "</div>";
        return output;
    }

    public static String kids() {
        return "Sorry Kid!!<br><br>You do are not old enough to access this page";
    }

    public static String nouser() {
        return "Sorry!!<br>An active session was not found. Please login again to continue";
    }

    public static String error(String error) {
        return "There was an error<br>" + error;
    }
}
