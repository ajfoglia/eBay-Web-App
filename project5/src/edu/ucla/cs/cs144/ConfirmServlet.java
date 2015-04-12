package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class ConfirmServlet extends HttpServlet implements Servlet {
       
    public ConfirmServlet() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        // verify secure session and client is in it
        if(session.isNew() || !request.isSecure()) {
            request.setAttribute("Valid_Session", "no");
            session.invalidate();
        }
        else {
            // retrieve parameters from request
            String id = (String)request.getParameter("confirmItemId");
            String name = (String)request.getParameter("confirmItemName");
            String price = (String)request.getParameter("confirmItemBuyPrice");
            request.setAttribute("Valid_Session", "yes");
            // set new session variables (time and card #)
            String cardNum = (String)request.getParameter("creditCard");
            session.setAttribute("cardNumber", cardNum);
            Date date = new Date();
            session.setAttribute("buyTime", date.toString());
        }
       
        // getRequestDispatcher to forward the query to jsp page (part 3 of A.2)
        request.getRequestDispatcher("/confirm.jsp").forward(request, response);
    }
}
