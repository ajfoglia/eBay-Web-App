/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
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

class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    // Use a hash map for users to denote who is a seller, who is a bidder, and who is both
    // sellerOrBidder will be:
    // 0 - Seller only
    // 1 - Bidder only
    // 2 - Both seller and bidder
    static Map<String, String> userMap = new HashMap<String, String>();
    static Map<String, Integer> sellerOrBidder = new HashMap<String, Integer>();
    // Keep track of ratings for sellers and bidders
    static Map<String, String> sellerRatings = new HashMap<String, String>();
    static Map<String, String> bidderRatings = new HashMap<String, String>();

    // Time Constants
    static String oldTimeFormat = "MMM-dd-yy HH:mm:ss";
    static String newTimeFormat = "yyyy-MM-dd HH:mm:ss";

    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
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
            // Change the format of the time to TIMESTAMP style
            newTime.applyPattern(newTimeFormat);
            // Do the actual formatting of the time
            convertedTime = newTime.format(newDateAndTime);
        } catch (ParseException pe) {
            System.out.println("** Error converting Time for TIMESTAMP format **");
        }
        return convertedTime;
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);

        /* Fill in code here (you will probably need to write auxiliary
            methods). */

        // Set up bufferedwriters, cannot be initialized in try block so set null first
        BufferedWriter writerItems = null;
        BufferedWriter writerBids = null;
        BufferedWriter writerCategories = null;
        
        try {

            // true is a boolean value telling filewriter to append, not overwrite file name
            writerItems = new BufferedWriter(new FileWriter("items.csv", true));
            writerBids = new BufferedWriter(new FileWriter("bids.csv", true));
            writerCategories = new BufferedWriter(new FileWriter("categories.csv", true));

            // root will get the document
            Element root = doc.getDocumentElement();

            // items is an array of Elements with the tag <Item> per the spec
            Element[] items = getElementsByTagNameNR(root, "Item");

            // Iterate through all items one by one
            for(int i = 0; i < items.length; i++) {
                
                // Gather some item information
                String itemid = items[i].getAttribute("ItemID");
                String name = getElementTextByTagNameNR(items[i], "Name");
                String currently = strip(getElementTextByTagNameNR(items[i], "Currently"));
                String buyPrice = strip(getElementTextByTagNameNR(items[i], "Buy_Price"));
                buyPrice = buyPrice.isEmpty() ? "" : buyPrice;
                String firstBid = strip(getElementTextByTagNameNR(items[i], "First_Bid"));
                String numberBids = getElementTextByTagNameNR(items[i], "Number_of_Bids");
                String started = convertTime(getElementTextByTagNameNR(items[i], "Started"));
                String ends = convertTime(getElementTextByTagNameNR(items[i], "Ends"));
                String description = getElementTextByTagNameNR(items[i], "Description");
                
                // Truncate to 4000, per the spec
                description = description.substring(0, Math.min(description.length(), 4000));

                // Gather category information
                ArrayList<String> categoryMap = new ArrayList<String>();
                Element[] categories = getElementsByTagNameNR(items[i], "Category");
                for(int k = 0; k < categories.length; k++) {
                    String category = getElementText(categories[k]);
                    // If category not listed for specific item, then add its association
                    // This accounts for duplicate removal, per the spec
                    if(!categoryMap.contains(category)) {
                        categoryMap.add(category);
                        writerCategories.write(itemid + columnSeparator + category + '\n');
                    }
                }
                
                // Gather bid information
                Element bid = getElementByTagNameNR(items[i], "Bids");
                Element[] bids = getElementsByTagNameNR(bid, "Bid");
                for(int j = 0; j < bids.length; j++) {
                    Element bidder = getElementByTagNameNR(bids[j], "Bidder");
                    String userID = bidder.getAttribute("UserID");
                    String ratingB = bidder.getAttribute("Rating");
                    String locationB = getElementTextByTagNameNR(bidder, "Location");
                    String countryB = getElementTextByTagNameNR(bidder, "Country");
                    String amount = strip(getElementTextByTagNameNR(bids[j], "Amount"));

                    // Do time conversion
                    String time = convertTime(getElementTextByTagNameNR(bids[j], "Time"));            
                    
                    // Code below accounts for duplicate removal
                    // Bidder, so set up string as if only a bidder
                    String userBInfo = userID + columnSeparator + "" + columnSeparator + ratingB + columnSeparator + '0' + columnSeparator + '1' + '\n';
                    // If user not in the system, then add the user
                    if(!sellerOrBidder.containsKey(userID)) {
                        sellerOrBidder.put(userID, 1);
                        bidderRatings.put(userID, ratingB);
                        userMap.put(userID, userBInfo);
                    }
                    // User is in the system, so user is either already a bidder or a seller
                    else {
                        int userType = sellerOrBidder.get(userID);
                        // If already a bidder, then user is still a '1' (bidder only)
                        // If already both, then do nothing ('2' means both)
                        // If already a seller, then user is now a '2' (both)
                        if(userType == 0) {
                            userBInfo = userID + columnSeparator + sellerRatings.get(userID) + columnSeparator + ratingB + columnSeparator + '1' + columnSeparator + '1' + '\n';
                            userMap.put(userID, userBInfo);
                        }
                    }

                    // save bid info
                    writerBids.write(itemid + columnSeparator + userID + columnSeparator + locationB + columnSeparator + countryB + columnSeparator + time + columnSeparator + amount + '\n');
                }

                // Gather more item information
                String location = getElementTextByTagNameNR(items[i], "Location");
                Element locationCoords = getElementByTagNameNR(items[i], "Location");
                String latitude = locationCoords.getAttribute("Latitude");
                String longitude = locationCoords.getAttribute("Longitude");                
                latitude = latitude.isEmpty() ? "" : latitude;
                longitude = longitude.isEmpty() ? "" : longitude;
                String country = getElementTextByTagNameNR(items[i], "Country");
                
                Element seller = getElementByTagNameNR(items[i], "Seller");
                String sellerID = seller.getAttribute("UserID");
                String sellerRating = seller.getAttribute("Rating");

                // Seller, so denote with '0' at the end of the string
                String userSInfo = sellerID + columnSeparator + sellerRating + columnSeparator + "" + columnSeparator + '1' + columnSeparator + '0' + '\n';
                // If user not in the system, then add the user
                if(!sellerOrBidder.containsKey(sellerID)) {
                    sellerOrBidder.put(sellerID, 0);
                    sellerRatings.put(sellerID, sellerRating);
                    userMap.put(sellerID, userSInfo);
                }
                // User is in the system, so user is either already a bidder or a seller
                else {
                    int userType = sellerOrBidder.get(sellerID);
                    // If already a seller, then user is still a '0' (bidder only)
                    // If already both, then do nothing ('2' means both)
                    // If already a bidder, then user is now a '2' (both)
                    if(userType == 1) {
                        userSInfo = sellerID + columnSeparator + sellerRating + columnSeparator + bidderRatings.get(sellerID) + columnSeparator + '1' + columnSeparator + '1' + '\n';
                        userMap.put(sellerID, userSInfo);
                    }
                }

                // save the item information
                String toItemCSV = itemid + columnSeparator + name + columnSeparator + currently + columnSeparator + buyPrice + columnSeparator + firstBid + columnSeparator + numberBids + columnSeparator + location + columnSeparator + country + columnSeparator + latitude + columnSeparator + longitude + columnSeparator + started + columnSeparator + ends + columnSeparator + sellerID + columnSeparator + description + '\n';
                writerItems.write(toItemCSV);
            }

            // Final file operations
            writerItems.flush();
            writerBids.flush();
            writerCategories.flush();
            writerItems.close();
            writerBids.close();
            writerCategories.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
        /**************************************************************/
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }

        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        /* Write user information to file using hash maps */
        String mapEntry = "";
        try {
            FileWriter writerUsers = new FileWriter("users.csv");
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                mapEntry = entry.getValue();
                writerUsers.append(mapEntry);
            }
            writerUsers.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
