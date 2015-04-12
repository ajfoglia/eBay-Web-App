package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // q will contain everything after the ?, so this will be "q=<your query>"
        String q = request.getQueryString();
        // append q to the right position in the google suggest API (after the &, since our suggest proxy will not have any other parameters besides q)
        URL googleSuggest = new URL("http://google.com/complete/search?output=toolbar&" + q);
        // Open a URL connection
    	HttpURLConnection googleSR = (HttpURLConnection) googleSuggest.openConnection();

        // Make sure connection is established
    	if(googleSR.getResponseCode() == HttpURLConnection.HTTP_OK) {

            // Per the spec, make sure output matches google suggest API (which is XML)
    		response.setContentType("text/xml");

    		// Write the output of the url to the response
            // See academic honesty section - I used a stack overflow solution to help with this part
            InputStream in = googleSR.getInputStream();
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
                out.write(buffer, 0, bytesRead);
            out.flush();
            out.close();
    	}
    }
}
