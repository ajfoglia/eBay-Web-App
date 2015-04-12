<%@ page import="edu.ucla.cs.cs144.*" %>
<%@ page import="java.util.ArrayList" %>
<%
	String itemResults = (String)request.getAttribute("itemResults");
	// request is an object, so need to do some casting
	String itemId = (String)request.getAttribute("itemId");
	String itemName = (String)request.getAttribute("itemName");
	String itemCurrently = (String)request.getAttribute("itemCurrently");
	String itemBuyPrice = (String)request.getAttribute("itemBuyPrice");
	String itemFirstBid = (String)request.getAttribute("itemFirstBid");
	String itemNumberBids = (String)request.getAttribute("itemNumberOfBids");
	String itemLocation = (String)request.getAttribute("itemLocation");
	String itemLatitude = (String)request.getAttribute("itemLatitude");
	String itemLongitude = (String)request.getAttribute("itemLongitude");
	String itemCountry = (String)request.getAttribute("itemCountry");
	String itemStarted = (String)request.getAttribute("itemStarted");
	String itemEnds = (String)request.getAttribute("itemEnds");
	String itemSellerID = (String)request.getAttribute("itemSellerID");
	String itemSellerRating = (String)request.getAttribute("itemSellerRating");
	String itemDescription = (String)request.getAttribute("itemDescription");
	ArrayList<String> itemCategories = new ArrayList<String>();
	ArrayList<String> bidId = new ArrayList<String>();
	ArrayList<String> bidRating = new ArrayList<String>();
	ArrayList<String> bidLocation = new ArrayList<String>();
	ArrayList<String> bidCountry = new ArrayList<String>();
	ArrayList<String> bidAmount = new ArrayList<String>();
	ArrayList<String> bidTime = new ArrayList<String>();
	itemCategories = (ArrayList<String>)request.getAttribute("itemCategories");
	bidId = (ArrayList<String>)request.getAttribute("bidId");
	bidRating = (ArrayList<String>)request.getAttribute("bidRating");
	bidCountry = (ArrayList<String>)request.getAttribute("bidCountry");
	bidLocation = (ArrayList<String>)request.getAttribute("bidLocation");
	bidAmount = (ArrayList<String>)request.getAttribute("bidAmount");
	bidTime = (ArrayList<String>)request.getAttribute("bidTime");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset = "utf-8">
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		<script type="text/javascript"
	      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBngbZSQQTYas0bXT_Ili2ReJoY6oODjys">
	    </script>
	    <script type="text/javascript">
	    var geocoder;
	    var map;
	    function initialize() {
	    	geocoder = new google.maps.Geocoder();
			var latlng = new google.maps.LatLng(0.0, 0.0);
			var myOptions = {
				zoom: 8,
				center: latlng,
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
		}
		function codeAddress() {
			/*if(<%= itemLatitude %> != "") {
				var latlng = new google.maps.LatLng(<%=itemLatitude%>,<%=itemLongitude%>); 
	    		var myOptions = {  
	    			zoom: 8,
	    			center: latlng, 
	    			mapTypeId: google.maps.MapTypeId.ROADMAP 
	    		}; 
	    		map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	    		var marker = new google.maps.Marker({
			    	map: map,
			        position: latlng,
			        title: "<%=itemLocation%>\n<%=itemLatitude%>,<%=itemLongitude%>"
			    });
			}
			else {*/
				initialize();
				var address = "<%= itemLocation %>, <%= itemCountry %>";
				geocoder.geocode( { 'address': address}, function(results, status) {
			    	if (status == google.maps.GeocoderStatus.OK) {
			    		map.setCenter(results[0].geometry.location);
			        	var marker = new google.maps.Marker({
			          		map: map,
			          		position: results[0].geometry.location
			      		});
			    	} else {
			    		geocoder.geocode(
			    			{'address': "USA"},
			    			function(results, status) {
			    				if (status == google.maps.GeocoderStatus.OK) {
						      		map.setCenter(results[0].geometry.location);
						      		map.setZoom(4);
						    	}
			    			}
			    		);
			    	}
			    });
			//}
		}
		</script>
		<link rel="stylesheet" type="text/css" href="basic.css">
		<!-- Twitter Boostrap -->
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-responsive.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-responsive.min.css">
		<link rel="stylesheet" type="img/png" href="bootstrap/img/glyphicons-halflings.png">
		<link rel="stylesheet" type="img/png" href="bootstrap/img/glyphicons-halflings-white.png">
		<link rel="stylesheet" type="text/js" href="bootstrap/js/bootstrap.js">
		<link rel="stylesheet" type="text/js" href="bootstrap/js/bootstrap.min.js">
		<title>Item Search Results</title>
	</head>
	<body onload="codeAddress()">
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<h1>eBay Item Search</h1>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6 offset3">
				<h3>Enter an Item ID that corresponds to an eBay auction item. The query will return the item's basic information.</h3>
				<form action="./item" method="get" class="form-search form-inline center">
					<div class="input-append">
						<input type="text" placeholder="Search..." name="id" class="input-xlarge search-query">
						<input type="submit" name="submitButton" class="btn btn-success" value="Search!">
					</div>
				</form>
			</div>
		</div>	
		<% if(itemResults == "") { %>
			<div class="row-fluid">
				<div class="span6 offset3 center">
					<div class="alert alert-error">
						An item with this ID (<em><%= itemId %></em>) does not exist! Please try again above.
					</div>
				</div>
			</div>
		<% } else { %>
			<div class="row-fluid">
				<div class="span6 offset3 center">
					<div class="alert alert-success">
						<h4>Your search for <em><%= itemId %></em> matched with the following information:</h4>
					</div>
					<h3><%= itemName %></h3>
				</div>
			</div>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Seller&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th><div class="center">Seller ID</div></th>
									<th><div class="center">Seller Rating</div></th>
								</tr>
							</thead>
							<tbody>
								<tr>
								<td><div class="center"><%= itemSellerID %></div></td>
								<td><div class="center"><%= itemSellerRating%></div></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="span4 information1">
						<strong>Seller ID</strong>: A unique ID identifying each user on eBay
						<br>
						<strong>Seller Rating</strong>: Each user has a rating (which may be different as a seller and as a bidder) on eBay
					</div>
				</div>
			</div>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Auction Information&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th><div class="center">Auction Begins</div></th>
									<th><div class="center">Auction Ends</div></th>
								</tr>
							</thead>
							<tbody>
								<tr>
								<td><div class="center"><%= itemStarted %></div></td>
								<td><div class="center"><%= itemEnds %></div></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="span4 information2">
						<strong>Auction Begins</strong>: Auction start time
						<br>
						<strong>Auction Ends</strong>: Auction end time
						<br>
						<em>Please note that this data was collected on Dec 20, 2001 at 12:00:01 AM.</em>
					</div>
				</div>
			</div>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Categories&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
							<tbody>
								<% for(int i = 0; i < itemCategories.size(); i++) { %>
								<tr><td><div class="center"><%= itemCategories.get(i) %></div></td></tr>
								<% } %>
							</tbody>
						</table>
					</div>
					<div class="span4 information3">
						<strong>Category</strong>: Any object may identify with 1+ categories set by eBay
					</div>
				</div>
			</div>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Pricing Information&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
							<tbody>
								<tr>
								<td><strong>Current Price</strong></td>
								<td>$<%= itemCurrently %></td>
								</tr>
								<% if(itemBuyPrice != "") { %>
									<tr>
									<td><strong>Buy Price</strong></td>
									<td>$<%= itemBuyPrice %></td>
									</tr>
								<% } %>
								<tr>
									<td><strong>First Bid</strong></td>
									<td>$<%= itemFirstBid %></td>
								</tr>
								<tr>
									<td><strong>Number of Bids</strong></td>
									<td><%= itemNumberBids %></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="span4 information4">
						<strong>Current Price</strong>: The current highest bid in $USD (again, note that "current" would refer to Dec 20, 2001 at 12:00:01 AM)
						<br>
						<% if(itemBuyPrice != "") { %>
						<strong>Buy Price</strong>: Chosen by the seller as the price (in $USD) where a bidder can win immediately with this bid amount (not always listed)
						<br>
						<% } %>
						<strong>First Bid</strong>: The minimum qualifying first-bid amount (in $USD), as determined by the seller before the auction starts. Does NOT guarantee a bid has been placed.
						<br>
						<strong>Number Of Bids</strong>: Total bids in this auction
						<br>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<br>
			</div>
			<% if(bidId.size() != 0) { %>
				<div class="container">
					<div class="row-fluid <% if(bidId.size() == 1) { %> vcenter <% } %>">
						<div class="span6 offset1 center">
							<h4>Bid History&nbsp;<i class="icon-info-sign"></i></h4>
							<% for(int i = bidId.size() - 1; i >= 0; i--) { %>
							<h5>Bid #<%= i+1 %></h5>
							<table class="table table-striped table-bordered table-hover">
								<tbody>
									<tr>
									<td><strong>Bidder ID</strong></td>
									<td><strong>Bidder Rating</strong></td>
									</tr>
									<tr>
									<td><%= bidId.get(i) %></td>
									<td><%= bidRating.get(i) %></td>
									</tr>
									<tr>
									<td><strong>Time</strong></td>
									<td><strong>Amount</strong></td>
									</tr>
									<tr>
									<td><%= bidTime.get(i) %></td>
									<td>$<%= bidAmount.get(i) %></td>
									</tr>
									<% if(bidLocation.get(i) != "" && bidCountry.get(i) != "") { %>
										<tr>
										<td><strong>Location</strong></td>
										<td><strong>Country</strong></td>
										</tr>
										<tr>
										<td><%= bidLocation.get(i) %></td>
										<td><%= bidCountry.get(i) %></td>
										</tr>
									<% } %>
									<% if(bidLocation.get(i) != "" && bidCountry.get(i) == "") { %>
										<tr>
										<td><strong>Location</strong></td>
										<td><strong>Country</strong></td>
										</tr>
										<tr>
										<td><%= bidLocation.get(i) %></td>
										<td>N/A</td>
										</tr>
									<% } %>
									<% if(bidLocation.get(i) == "" && bidCountry.get(i) != "") { %>
										<tr>
										<td><strong>Location</strong></td>
										<td><strong>Country</strong></td>
										</tr>
										<tr>
										<td>N/A</td>
										<td><%= bidCountry.get(i) %></td>
										</tr>
									<% } %>
								</tbody>
							</table>
							<% } %>
						</div>
						<div class="span4 information1">
							<strong>Bidder ID</strong>: Like Seller ID, the unique ID of the eBay user
							<br>
							<strong>Bidder Rating</strong>: May differ from Seller Rating
							<br>
							<strong>Time</strong>: The time the bid was placed. Must be placed after the auction starts and before it ends. A user may bid on an item multiple times, but not at the same time. 
							<br>
							<strong>Amount</strong>: In $USD
							<br>
							<strong>Location/Country</strong>: May not be listed
							<br>
						</div>
					</div>
				</div>
			<% } %>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Item Location&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
							<tbody>
								<tr>
								<td><strong>Location</strong></td>
								<td><%= itemLocation %></td>
								</tr>
								<tr>
								<td><strong>Country</strong></td>
								<td><%= itemCountry %></td>
								</tr>
								<% if(itemLatitude != "") { %>
									<tr>
									<td><strong>Latitude</strong></td>
									<td><%= itemLatitude %></td>
									</tr>
								<% } %>
								<% if(itemLongitude != "") { %>
									<tr>
									<td><strong>Longitude</strong></td>
									<td><%= itemLongitude %></td>
									</tr>
								<% } %>
							</tbody>
						</table>
					</div>
					<div class="span4 information2">
						<strong>Location</strong>: The item's location, which may also be indiciated on a map above
						<br>
						<strong>Country</strong>: More item information
						<% if(itemLatitude != "") { %>
						<br>
						<strong>Latitude/Longitude</strong>: Additional Optional Location Information
						<% } %>
						<br><em>Note the map below corresponds to the above info, unless the location/country info is not valid - in which case a map of the USA is displayed with no markers.</em>
					</div>
				</div>
				</div>
				<div class="row-fluid vcenter">
					<br>
					<div class="span8 offset2">
						<div id="map_canvas" style="width: 100%; height: 475px"></div>
					</div>
					<br>
				</div>
			<div class="container">
				<div class="row-fluid vcenter">
					<div class="span6 offset1 center">
						<h4>Item Description&nbsp;<i class="icon-info-sign"></i></h4>
						<table class="table table-striped table-bordered table-hover">
						<tbody><tr><td><%= itemDescription %></td></tr></tbody>
						</table>
					</div>
					<div class="span4 information3">
						<strong>Description</strong>: The item's full description
					</div>
				</div>
			</div>
		<% } %>
	</body>
</html>