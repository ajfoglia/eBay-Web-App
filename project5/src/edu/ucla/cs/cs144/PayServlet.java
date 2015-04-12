package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class PayServlet extends HttpServlet implements Servlet {
       
    public PayServlet() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Initiate session
        HttpSession session = request.getSession(true);

        // Verify the session exists
        if(session.isNew()) {
            // kill session if new
            request.setAttribute("Valid_Session", "no");
            session.invalidate();
        } else {
            // get itemID
            String id = (String)request.getParameter("itemId");
            // if id not null then proceed
            if(id != null) {
                String name = (String)request.getParameter("itemName");
                String price = (String)request.getParameter("itemBuyPrice");
                session.setAttribute("sessionId", id);
                session.setAttribute("sessionName", name);
                session.setAttribute("decodedName", URLDecoder.decode(name, "utf-8"));
                session.setAttribute("sessionBuyPrice", price);
                request.setAttribute("Valid_Session", "yes");
            // id bad then kill transaction
            } else {
                request.setAttribute("Valid_Session", "no");
            }
            
        }
       
        // getRequestDispatcher to forward the query to jsp page (part 3 of A.2)
        request.getRequestDispatcher("/purchase.jsp").forward(request, response);
    }
}
