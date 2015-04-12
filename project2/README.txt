**********************************************************
TEAM: Me_Only
**********************************************************

Andy Foglia
903895596

****************************************************************
ACADEMIC HONESTY:
I used provided links for SQL Timestamp and the parse() and format() for time in Java. I also used a few links talking about SQL collation. All of these resources were documentation resources. I used a BufferedWriter and followed its documentation online as well.

http://docs.oracle.com/javase/7/docs/api/java/io/BufferedWriter.html

http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
****************************************************************

Part B:

1. List relations

User (UserID [PRIMARY KEY], SellerRating, BidderRating, IsSeller, IsBidder)
Item (ItemID [PRIMARY KEY], Name, Currently, Buy_Price, First_Bid, Number_Of_Bids, Location, Country, Latitude, Longitude, Started, Ends, Seller, Description)
Bid (ItemID, UserID, Time, Amount, ((ItemID, UserID, Time) [PRIMARY KEY]))
Categories (ItemID, Category)

The User relation will use a unique user ID as the primary key, and will hold information about that specific user's SellerRating, BidderRating, and whether or not the user is a bidder and/or seller.

The Item relation will hold some information about the auction itself. The Seller field will be a user ID that can be found in the User relation, also giving the item's location and country fields. Everything else will be stored in the relation itself.

The Bid relation will store bid(s) for items based on item ID. These bids have user information as well as the time and amount of the bid. It references the item and user relations.

The Categories relation maps an item to each of its categories on eBay.

2. List nontrivial functional dependencies that hold on each relation

(* User relation: For a user ID, the rating/location/country is the same across that same user ID. So each user ID maps to only one thing. *)
User:
UserID -> SellerRating, BidderRating, IsSeller, IsBidder

(* Item relation: ItemID is unique so can only map to one entry in the relation *)
Item:
ItemID -> Name, Currently, Buy_Price, First_Bid, Number_Of_Bids, Location, Country, Latitude, Longitude, Started, Ends, Seller, Description

(* Bid relation: ItemID, UserID and Time combine to map to only one amount since each bid must have a different amount if under same itemID. *)
Bid:
ItemID, UserID, Time -> Amount

(* Categories: ItemID can map to many different categories *)
Categories:
ItemID -> Category

3. BCNF - YES
4. 4NF - YES