package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class AuctionSearch implements IAuctionSearch {

	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private ArrayList<String> itemIds;
	private Map<String, String> itemIdsAndNames;

	// Convert from timestamp to XML desired format
	static String oldTimeFormat = "yyyy-MM-dd HH:mm:ss";
    static String newTimeFormat = "MMM-dd-yy HH:mm:ss";

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		SearchResult[] results = new SearchResult[0];
		try {
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index1"))));
			parser = new QueryParser("content", new StandardAnalyzer());

			Query qResults = parser.parse(query);
			TopDocs topDocs = searcher.search(qResults, numResultsToReturn);
			
			// Errors
			if (numResultsToReturn + numResultsToSkip < topDocs.totalHits) {
				results = new SearchResult[numResultsToReturn];
			}
			else {
				results = new SearchResult[topDocs.totalHits];
			}

			//results = new SearchResult[topDocs.totalHits];

			// Get size of search result array
			int index = 0;
			int resultIndex = 0;
			/*for(int i = 0; i < topDocs.totalHits; i++) {
				if(i > numResultsToSkip - 1 && i < numResultsToReturn + numResultsToSkip) {
					resultIndex++;
				}
				index++;
			}
			System.out.println(resultIndex);
			results = new SearchResult[resultIndex];*/

			itemIds = new ArrayList<String>();
			itemIdsAndNames = new HashMap<String, String>();

			// Start off indexes at 0
			index = 0;
			resultIndex = 0;
			// Debugging purposes
			//System.out.println(topDocs.totalHits);
			// For each hit
			for(ScoreDoc scoredoc : topDocs.scoreDocs) {
				// Get document
				Document d = searcher.doc(scoredoc.doc);
				// Store itemId and name for spatial search if called
				itemIds.add(d.get("ItemID"));
				itemIdsAndNames.put(d.get("ItemID"), d.get("ItemName"));
				// If this hit not skipped and we have more to retrieve
				if(index > numResultsToSkip - 1) {
					// Store search result
					results[resultIndex] = new SearchResult(d.get("ItemID"), d.get("ItemName"));
					// We have stored a result, so increment pointer
					resultIndex++;
				}
				// keep going
				index++;
			}
		} catch (Exception e) {
			System.out.println("Done");
		}
		return results;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		
		// Create region
		String lx = Double.toString(region.getLx());
		String ly = Double.toString(region.getLy());
		String rx = Double.toString(region.getRx());
		String ry = Double.toString(region.getRy());
		
		// Run keyword search
		SearchResult[] results = basicSearch(query, 0, 20000);
		
		// Set up a db connection
		Connection conn = null;		

		// List to store id's within region
		ArrayList<String> itemSpatialIds = new ArrayList<String>();

		//String sqlPolygon = "\'POLYGON((" + lx + " " + ly + "," + rx + " " + ly + "," + rx + " " + ry + "," + lx + " " + ry + "," + lx + " " + ly + ")\'";

		// Find all items in the rectangle and store
		try {
			conn = DbManager.getConnection(true);
			Statement spatialStatement = conn.createStatement();
			String prepareRegionQuery = "SELECT * FROM SearchRegion WHERE X(coord) < " + rx + " AND X(coord) > " + lx + " AND Y(coord) > " + ly + " AND Y(coord) < " + ry;
			ResultSet regionrs = spatialStatement.executeQuery(prepareRegionQuery);
			while(regionrs.next()) {
				itemSpatialIds.add(regionrs.getString("ItemID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Get count of matches in constrained search space
		int count = 0;
		for(int i = 0; i < itemIds.size(); i++) {
			if(itemSpatialIds.contains(itemIds.get(i))) {
				count++;
			}
		}

		// Get size of search result array
		int index = 0;
		int resultIndex = 0;
		for(int i = 0; i < count; i++) {
			if(i > numResultsToSkip - 1 && i < numResultsToReturn + numResultsToSkip) {
				resultIndex++;
			}
			index++;
		}

		// Set up returned values
		SearchResult[] newResults = new SearchResult[resultIndex];
		index = 0;
		resultIndex = 0;
		// Truncate based on numResultsSkip and return
		for(int i = 0; i < count; i++) {
			if(i > numResultsToSkip - 1 && i < numResultsToReturn + numResultsToSkip) {
				newResults[resultIndex] = new SearchResult(itemIds.get(i), itemIdsAndNames.get(itemIds.get(i)));
				resultIndex++;
			}
			index++;
		}

		// return results
		return newResults;
	}

	public String getXMLDataForItemId(String itemId) {
	
		//String completeXML = "<Item ItemID=\"" + itemId + "\">\n";
		
		String completeXML = "";

		try {

			Connection conn = null;

	        // create a connection to the database to retrieve Items from MySQL
	    	try {
	    	    conn = DbManager.getConnection(true);
	    	} catch (SQLException ex) {
	    	    System.out.println(ex);
	    	}

	    	// Retrieve valid Item info
	    	Statement itemStatement = conn.createStatement();
			String prepareItemQuery = "SELECT * FROM Item WHERE ItemID = " + itemId;
			ResultSet itemrs = itemStatement.executeQuery(prepareItemQuery);
			
			// Make sure an item exists, if not return empty string
			if(!itemrs.isBeforeFirst()) {
				return "";
			}

			// Prepare XML File
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Specify proper document library
			org.w3c.dom.Document doc = db.newDocument();

			Element root = doc.createElement("Item");
			root.setAttribute("ItemID", itemId);
			doc.appendChild(root);

			//String itemXML = "";
			String itemName = "";
			String itemCurrently = "";
			String itemBuyPrice = "";
			String itemFirstBid = "";
			String itemNumberBids = "";
			String itemLocation = "";
			String itemCountry = "";
			String itemLatitude = "";
			String itemLongitude = "";
			String itemStarted = "";
			String itemEnds = "";
			String itemSellerID = "";
			String itemDescription = "";
			while(itemrs.next()) {
				//itemName = escapeCharacters(itemrs.getString("Name"));
				itemName = (itemrs.getString("Name"));	
				itemCurrently = "$" + String.valueOf(itemrs.getString("Currently"));
				itemBuyPrice = "$" + String.valueOf(itemrs.getString("BuyPrice"));
				itemFirstBid = "$" + String.valueOf(itemrs.getString("FirstBid"));
				itemNumberBids = String.valueOf(itemrs.getString("NumberBids"));
				//itemLocation = escapeCharacters(itemrs.getString("Location"));
				itemLocation = (itemrs.getString("Location"));
				//itemCountry = escapeCharacters(itemrs.getString("Country"));
				itemCountry = (itemrs.getString("Country"));
				itemLatitude = itemrs.getString("Latitude");
				itemLongitude = itemrs.getString("Longitude");
				itemStarted = convertTime(itemrs.getString("Started"));
				itemEnds = convertTime(itemrs.getString("Ends"));
				//itemSellerID = escapeCharacters(itemrs.getString("SellerID"));
				itemSellerID = (itemrs.getString("SellerID"));
				//itemDescription = escapeCharacters(itemrs.getString("Description"));
				itemDescription = (itemrs.getString("Description"));
			}      	
			
			// Name
			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(itemName));
			root.appendChild(name);

			// Get relevant category information
			Statement categoryStatement = conn.createStatement();
			String prepareCategoryQuery = "SELECT Category FROM Categories WHERE ItemID = " + itemId;
			ResultSet categoryrs = itemStatement.executeQuery(prepareCategoryQuery);
			//String categoryXML = "";
			while(categoryrs.next()) {
				//categoryXML += "<Category>";
				String categoryInstance = categoryrs.getString("Category");
				//categoryInstance = escapeCharacters(categoryInstance);
				Element category = doc.createElement("Category");
				category.appendChild(doc.createTextNode(categoryInstance));
				root.appendChild(category);
				//categoryXML += categoryInstance;
				//categoryXML += "</Category>\n";
			}

			// Currently
			Element currently = doc.createElement("Currently");
			currently.appendChild(doc.createTextNode(itemCurrently));
			root.appendChild(currently);

			// Buy_Price
			if(!itemBuyPrice.equals("$0.00")) {
				Element buyprice = doc.createElement("Buy_Price");
				buyprice.appendChild(doc.createTextNode(itemBuyPrice));
				root.appendChild(buyprice);
			}

			// First_Bid
			Element firstbid = doc.createElement("First_Bid");
			firstbid.appendChild(doc.createTextNode(itemFirstBid));
			root.appendChild(firstbid);

			// Number_of_Bids
			Element numberbids = doc.createElement("Number_of_Bids");
			numberbids.appendChild(doc.createTextNode(itemNumberBids));
			root.appendChild(numberbids);

			// Get bid info
			Statement bidsStatement = conn.createStatement();
			String prepareBidsQuery = "SELECT * FROM Bid WHERE ItemID = " + itemId + " ORDER BY Time ASC";
			ResultSet bidsrs = bidsStatement.executeQuery(prepareBidsQuery);
			Element bidsInfo = doc.createElement("Bids");
			//String bidsXML = "<Bids>\n";
			String bidUserID = "";
			while(bidsrs.next()) {
				//bidUserID = escapeCharacters(bidsrs.getString("UserID"));
				Element bidInfo = doc.createElement("Bid");
				Element bidderInfo = doc.createElement("Bidder");
				bidUserID = (bidsrs.getString("UserID"));
				Statement bidsIDStatement = conn.createStatement();
				String prepareBidsRatingQuery = "SELECT * FROM User WHERE UserID = \"" + bidUserID + "\"";
				ResultSet bidsratingrs = bidsIDStatement.executeQuery(prepareBidsRatingQuery);
				String bidderRatingString = "";
				while(bidsratingrs.next()) {
					bidderRatingString = bidsratingrs.getString("BidderRating");
				}
				bidderInfo.setAttribute("UserID", bidUserID);
				bidderInfo.setAttribute("Rating", bidderRatingString);
				bidInfo.appendChild(bidderInfo);
				String bidLocation = bidsrs.getString("Location");
				String bidsCountry = bidsrs.getString("Country");
				if(!bidLocation.equals("")) {
					Element bidderLocation = doc.createElement("Location");
					bidderLocation.appendChild(doc.createTextNode(bidLocation));
					bidderInfo.appendChild(bidderLocation);
				}
				if(!bidsCountry.equals("")) {
					Element bidderCountry = doc.createElement("Country");
					bidderCountry.appendChild(doc.createTextNode(bidsCountry));
					bidderInfo.appendChild(bidderCountry);
				}
				Element bidderTime = doc.createElement("Time");
				bidderTime.appendChild(doc.createTextNode(convertTime(bidsrs.getString("Time"))));
				bidInfo.appendChild(bidderTime);
				Element bidderAmount = doc.createElement("Amount");
				bidderAmount.appendChild(doc.createTextNode("$" + bidsrs.getString("Amount")));
				bidInfo.appendChild(bidderAmount);


				bidsInfo.appendChild(bidInfo);
				/*bidsXML += "<Bid>\n<Bidder Rating=\"" + bidderRatingString + "\" UserID=\"" + bidUserID + "\">\n";
				bidsXML += "<Location>";
				bidsXML += escapeCharacters(bidsrs.getString("Location"));
				bidsXML += "</Location>\n<Country>";
				bidsXML += escapeCharacters(bidsrs.getString("Country"));
				bidsXML += "</Country>\n</Bidder>\n<Time>";
				bidsXML += convertTime(bidsrs.getString("Time"));
				bidsXML += "</Time>\n<Amount>$";
				bidsXML += bidsrs.getString("Amount");
				bidsXML += "</Amount>\n</Bid>\n";*/
			}
			/*if(bidsXML.equals("<Bids>\n")) {
				bidsXML = "<Bids />\n";
			}
			else {
				bidsXML += "</Bids>\n";
			}*/

			// Bids
			root.appendChild(bidsInfo);

			// Process XML
			//completeXML += ("<Name>" + itemName + "</Name>\n");
			//completeXML += categoryXML;
			//completeXML += ("<Currently>$" + itemCurrently + "</Currently>\n");
			/*if(!itemBuyPrice.equals("0.00")) {
				completeXML += ("<Buy_Price>$" + itemBuyPrice + "</Buy_Price>\n");
			}*/
			//completeXML += ("<First_Bid>$" + itemFirstBid + "</First_Bid>\n");
			//completeXML += ("<Number_of_Bids>" + itemNumberBids + "</Number_of_Bids>\n");
			//completeXML += bidsXML;
			/*if(itemLongitude.equals("")) {
				completeXML += ("<Location>" + itemLocation + "</Location>\n");
			}
			else {
				completeXML += ("<Location Latitude=\"" + itemLatitude + "\" Longitude=\"" + itemLongitude +"\">" + itemLocation + "</Location>\n");
			}*/
			//completeXML += ("<Country>" + itemCountry + "</Country>\n");
			//completeXML += ("<Started>" + itemStarted + "</Started>\n");
			//completeXML += ("<Ends>" + itemEnds + "</Ends>\n");
			//completeXML += sellerXML;
			//completeXML += ("<Description>" + itemDescription + "</Description>\n</Item>\n");
			
			// Location
			Element itemlocation = doc.createElement("Location");
			// Latitude, Longtiude
			if(!itemLongitude.equals("")) {
				itemlocation.setAttribute("Latitude", itemLatitude);
				itemlocation.setAttribute("Longitude", itemLongitude);
			}
			itemlocation.appendChild(doc.createTextNode(itemLocation));
			root.appendChild(itemlocation);

			// Country
			Element country = doc.createElement("Country");
			country.appendChild(doc.createTextNode(itemCountry));
			root.appendChild(country);

			// Started
			Element started = doc.createElement("Started");
			started.appendChild(doc.createTextNode(itemStarted));
			root.appendChild(started);

			// Ends
			Element ends = doc.createElement("Ends");
			ends.appendChild(doc.createTextNode(itemEnds));
			root.appendChild(ends);

			// Get relevant seller information
			Statement sellerStatement = conn.createStatement();
			String prepareSellerQuery = "SELECT * FROM User WHERE UserID = '" + itemSellerID + "'";
			ResultSet sellerrs = sellerStatement.executeQuery(prepareSellerQuery);
			/*String sellerXML = "";
			while(sellerrs.next()) {
				sellerXML += "<Seller Rating=\"";
				sellerXML += sellerrs.getString("SellerRating");
				sellerXML += "\" UserID=\"";
				sellerXML += itemSellerID;
				sellerXML += "\" />\n";
			}*/

			// Seller
			Element sellerinfo = doc.createElement("Seller");
			while(sellerrs.next()) {
				sellerinfo.setAttribute("UserID", itemSellerID);
				sellerinfo.setAttribute("Rating", sellerrs.getString("SellerRating"));
			}
			root.appendChild(sellerinfo);

			// Description
			Element description = doc.createElement("Description");
			description.appendChild(doc.createTextNode(itemDescription));
			root.appendChild(description);

			// See Academic Honesty section, found below structure online
			TransformerFactory ntf = TransformerFactory.newInstance();
            Transformer transform = ntf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult res = new StreamResult(writer);

            // Indent the XML
            transform.setOutputProperty(OutputKeys.INDENT, "yes");
            transform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            // Do not print XML top line
            transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            
            // Run final transformation
            transform.transform(source, res);

            // Write final string
            completeXML = writer.toString();
            
			// close the database connection
	    	try {
	    	    conn.close();
	    	} catch (SQLException ex) {
	    	    System.out.println(ex);
	    	}

    	} catch (SQLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
        	e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        // Return XML
		return completeXML;
	}
	
	/* Function to escape < > & from strings when converting to XML */
	public String escapeCharacters(String toEscape) {
		toEscape = toEscape.replaceAll("&", "&amp;");
		toEscape = toEscape.replaceAll("<", "&lt;");
		toEscape = toEscape.replaceAll(">", "&rt;");
		return toEscape;
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
        } catch (Exception pe) {
            System.out.println("** Error converting Time for XML format **");
        }
        return convertedTime;
    }

	public String echo(String message) {
		return message;
	}

}
