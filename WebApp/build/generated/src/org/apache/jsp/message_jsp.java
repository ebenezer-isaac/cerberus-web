package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import java.util.*;

public final class message_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
if ((request.getAttribute("redirect")).equals("true")) {
        response.setHeader("Refresh", request.getAttribute("seconds") + ";url=" + request.getAttribute("url"));
    } 
      out.write("\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <title>Message</title>\n");
      out.write("    </head>\n");
      out.write("    <body style=\"\">\n");
      out.write("        <fieldset>\n");
      out.write("            <legend>");
out.print((request.getAttribute("head")).toString());
      out.write("</legend>\n");
      out.write("            <p>");
out.print((request.getAttribute("body")).toString());
      out.write("</p>\n");
      out.write("        </fieldset>\n");
      out.write("        <br>\n");
      out.write("        <div align=\"center\">\n");
      out.write("            ");
if ((request.getAttribute("redirect")).equals("true")) {
                    out.println("<p>This page will be redirected automatically in " + request.getAttribute("seconds") + " seconds</p>");
                } else {
                    out.println("<form action=\"" + request.getAttribute("url") + "\" method=\"" + request.getAttribute("method") + "\">"
                            + "<input type =\"submit\" value=\"" + request.getAttribute("button") + "\">"
                            + "</form>");
                }
            
      out.write("\n");
      out.write("        </div>\n");
      out.write("        <div class=\"loader\">\n");
      out.write("            <div class=\"duo duo1\">\n");
      out.write("                <div class=\"dot dot-a\"></div>\n");
      out.write("                <div class=\"dot dot-b\"></div>\n");
      out.write("            </div>\n");
      out.write("            <div class=\"duo duo2\">\n");
      out.write("                <div class=\"dot dot-a\"></div>\n");
      out.write("                <div class=\"dot dot-b\"></div>\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
