package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Fetch arguments passed and call basicSearch
        String searchQuery = request.getParameter("q");
        String numSkip = request.getParameter("numResultsToSkip");
        String numResults = request.getParameter("numResultsToReturn");
        AuctionSearchClient asc = new AuctionSearchClient();
        SearchResult[] queryResults = asc.basicSearch(searchQuery, Integer.parseInt(numSkip), Integer.parseInt(numResults));

        // Use setAttribute to create proper request
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("numSkip", Integer.parseInt(numSkip));
        request.setAttribute("numReturn", Integer.parseInt(numResults));
        request.setAttribute("queryResults", queryResults);
       
        // getRequestDispatcher to forward the query to jsp page (part 3 of A.2)
        request.getRequestDispatcher("/queryResults.jsp").forward(request, response);
    }
}
