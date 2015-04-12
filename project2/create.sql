CREATE TABLE User (UserID varchar(400) PRIMARY KEY, SellerRating integer, BidderRating integer,  IsSeller integer, IsBidder integer);
CREATE TABLE Item (ItemID int PRIMARY KEY, Name varchar(400), Currently DECIMAL(8,2), BuyPrice DECIMAL(8,2), FirstBid DECIMAL(8,2), NumberBids integer, Location varchar(400), Country varchar(400), Latitude varchar(400), Longitude varchar(400), Started TIMESTAMP, Ends TIMESTAMP, SellerID varchar(400), Description varchar(4000)); 
CREATE TABLE Bid (ItemID int, UserID varchar(400), Location varchar(400), Country varchar(400), Time TIMESTAMP, Amount varchar(400), PRIMARY KEY (ItemID, UserID, Time));
CREATE TABLE Categories (ItemID int, Category varchar(400));
