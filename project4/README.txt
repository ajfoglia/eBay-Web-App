**********************************************************
TEAM: Me_Only
**********************************************************

Andy Foglia
903895596

****************************************************************
ACADEMIC HONESTY:

I followed this link very closely when converting my getXMLDataFromId to an XML document so I could reparse it. There are a lot of similarities between this and my code.
http://stackoverflow.com/questions/43157/easy-way-to-write-contents-of-a-java-inputstream-to-an-outputstream

I used Twitter bootstrap 2.3.2 (previous experience) to style my website. I know it is the outdated version, but I preferred working with the version I was most comfortable with.

I followed links on W3schools and StackOverflow when solving basic web programming issues like CSS stylesheets and XMLHttpRequest.

I also used the links provided on the project spec page regarding the google suggest client and copied the code over that I needed, and then restructured the functions so that I could use my ProxyServlet.
****************************************************************

My static html pages are straightforward.

queryResults.jsp Notes:
- Extract the form values
- If no results match query, tell user without 500 error
- Otherwise list off matching items
- Next/Previous Links
	* Return 40 results, but only display 20 so then I can tell how many results are left after the 20 I display
	* e.g. Superman returns 68, so on the 41-60 page the next button can be spot on and say 61-68
- JS code for google suggest

queryItem.jsp Notes:
- Included notes on what each field meant
- JS code for google maps

Servlet Notes:
- Search: Use of getParameter, run basicSearch, setAttributes, forward request to response
- Item: Copied a lot of project 2 code to reconstruct XML, used Document class help from online to do it, same process as search once XML created (parse for each field and set attributes)
- Proxy: Retrieve query, create URL to get data from google suggest and write URL data to response object using Input/Output stream

Google Maps: I followed the online documentation to do effective geocoding, and if nothing worked I displayed a map of the USA.

Google Suggest: I used the project links extensively and the JS Console to debug.