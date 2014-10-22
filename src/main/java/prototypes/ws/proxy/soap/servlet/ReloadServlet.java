package prototypes.ws.proxy.soap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import prototypes.ws.proxy.soap.io.Requests;

/**
 * Servlet implementation class ReloadServlet
 */
public class ReloadServlet extends AbstractServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Requests.getProxy(this.getServletContext()).reloadWsdl();
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("OK");
    }

}
