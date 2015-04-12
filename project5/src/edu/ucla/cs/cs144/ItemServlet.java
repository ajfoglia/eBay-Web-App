package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Timestamp;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    // Time Constants
    static String oldTimeFormat = "MMM-dd-yy HH:mm:ss";

    // This is how I will show time on the website
    static String newTimeFormat = "EEE MMM d, yyyy hh:mm:ss a";

    /** FUNCTIONS FROM PROJECT 2 **/

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    /* Function to convert time from old format to
    new format that is favorable for TIMESTAMP in SQL
    */
    static String convertTime(String timeToConvert) {
        // Use SimpleDateFormat object to convert
        SimpleDateFormat newTime = new SimpleDateFormat(oldTimeFormat);
        String convertedTime = "";
        try {
            // Parse the time given
            Date newDateAndTime = newTime.parse(timeToConvert);
            // Change the format of the time to new desired style
            newTime.applyPattern(newTimeFormat);
            // Do the actual formatting of the time
            convertedTime = newTime.format(newDateAndTime);
        } catch (ParseException pe) {
            System.out.println("** Error converting Time for TIMESTAMP format **");
        }
        return convertedTime;
    }

    /** END OF FUNCTIONS FROM PROJECT 2 **/

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Fetch arguments passed and call getXMLDataForItemId
        String itemId = request.getParameter("id");
        AuctionSearchClient asc = new AuctionSearchClient();
        String itemResults = asc.getXMLDataForItemId(itemId);

        // Parse the item information
        // Prepare XML File
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		org.w3c.dom.Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource (new StringReader(itemResults)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        if(itemResults != "") {
            // root will get the document
            Element root = doc.getDocumentElement();

            // Gather some item information
            String itemid = root.getAttribute("ItemID");
            String name = getElementTextByTagNameNR(root, "Name");
            String currently = strip(getElementTextByTagNameNR(root, "Currently"));
            String buyPrice = strip(getElementTextByTagNameNR(root, "Buy_Price"));
            buyPrice = buyPrice.isEmpty() ? "" : buyPrice;
            String firstBid = strip(getElementTextByTagNameNR(root, "First_Bid"));
            String numberBids = getElementTextByTagNameNR(root, "Number_of_Bids");
            String started = convertTime(getElementTextByTagNameNR(root, "Started"));
            String ends = convertTime(getElementTextByTagNameNR(root, "Ends"));
            String description = getElementTextByTagNameNR(root, "Description");
            
            // Gather category information
            ArrayList<String> categoryMap = new ArrayList<String>();
            Element[] categories = getElementsByTagNameNR(root, "Category");
            for(int k = 0; k < categories.length; k++) {
                String category = getElementText(categories[k]);
                // If category not listed for specific item, then add its association
                // This accounts for duplicate removal, per the spec
                if(!categoryMap.contains(category)) {
                    categoryMap.add(category);
                }
            }
            
            // Gather bid information
            Element bid = getElementByTagNameNR(root, "Bids");
            Element[] bids = getElementsByTagNameNR(bid, "Bid");
            ArrayList<String> bidId = new ArrayList<String>();
            ArrayList<String> bidRating = new ArrayList<String>();
            ArrayList<String> bidLocation = new ArrayList<String>();
            ArrayList<String> bidCountry = new ArrayList<String>();
            ArrayList<String> bidAmount = new ArrayList<String>();
            ArrayList<String> bidTime = new ArrayList<String>();
            for(int j = 0; j < bids.length; j++) {
                Element bidder = getElementByTagNameNR(bids[j], "Bidder");
                String userID = bidder.getAttribute("UserID");
                String ratingB = bidder.getAttribute("Rating");
                String locationB = getElementTextByTagNameNR(bidder, "Location");
                String countryB = getElementTextByTagNameNR(bidder, "Country");
                String amount = strip(getElementTextByTagNameNR(bids[j], "Amount"));
                // Do time conversion
                String time = convertTime(getElementTextByTagNameNR(bids[j], "Time"));            
    		    bidId.add(userID);
                bidRating.add(ratingB);
                bidLocation.add(locationB);
                bidCountry.add(countryB);
                bidAmount.add(amount);
                bidTime.add(time);
            }

            // Gather more item information
            String location = getElementTextByTagNameNR(root, "Location");
            Element locationCoords = getElementByTagNameNR(root, "Location");
            String latitude = locationCoords.getAttribute("Latitude");
            String longitude = locationCoords.getAttribute("Longitude");                
            latitude = latitude.isEmpty() ? "" : latitude;
            longitude = longitude.isEmpty() ? "" : longitude;
            String country = getElementTextByTagNameNR(root, "Country");
            
            Element seller = getElementByTagNameNR(root, "Seller");
            String sellerID = seller.getAttribute("UserID");
            String sellerRating = seller.getAttribute("Rating");

            // Use setAttribute to create proper request
            request.setAttribute("itemId", itemId);
            request.setAttribute("itemResults", itemResults);
            request.setAttribute("itemName", name);
            request.setAttribute("itemCategories", categoryMap);
            request.setAttribute("itemCurrently", currently);
            request.setAttribute("itemBuyPrice", buyPrice);
            request.setAttribute("itemFirstBid", firstBid);
            request.setAttribute("itemNumberOfBids", numberBids);
            
            // bids (maybe a hashmap): bidder id/rating, location/country?, time, amount
            request.setAttribute("bidId", bidId);
            request.setAttribute("bidRating", bidRating);
            request.setAttribute("bidLocation", bidLocation);
            request.setAttribute("bidCountry", bidCountry);
            request.setAttribute("bidAmount", bidAmount);
            request.setAttribute("bidTime", bidTime);

            request.setAttribute("itemLocation", location);
            request.setAttribute("itemLatitude", latitude);
            request.setAttribute("itemLongitude", longitude);
            request.setAttribute("itemCountry", country);
            request.setAttribute("itemStarted", started);
            request.setAttribute("itemEnds", ends);
            request.setAttribute("itemSellerID", sellerID);
            request.setAttribute("itemSellerRating", sellerRating);
            request.setAttribute("itemDescription", description);
        }
        else {
            request.setAttribute("itemId", itemId);
            request.setAttribute("itemResults", itemResults);
        }
       
        // getRequestDispatcher to forward the query to jsp page (part 3 of A.2)
        request.getRequestDispatcher("/queryItem.jsp").forward(request, response);
    }
}
