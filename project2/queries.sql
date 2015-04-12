SELECT COUNT(*) FROM User;
SELECT Count(*) FROM Item WHERE Location COLLATE latin1_general_cs = "New York";
SELECT COUNT(*) FROM(SELECT COUNT(*) as cnt FROM Categories GROUP BY ItemID HAVING cnt = 4) a;
SELECT ItemID FROM Item WHERE Currently = (SELECT MAX(Currently) FROM Item WHERE Ends > "2001-12-20 00:00:01" AND Started < "2001-12-20 00:00:00" AND NumberBids > 0) AND Ends > "2001-12-20 00:00:01" AND Started < "2001-12-20 00:00:00" AND NumberBids > 0;
SELECT COUNT(*) FROM User WHERE IsSeller = 1 AND SellerRating > 1000;
SELECT COUNT(*) FROM User WHERE IsSeller = 1 AND IsBidder = 1;
SELECT COUNT(*) FROM (SELECT B.ItemID FROM Bid B, Categories C WHERE B.Amount > 100 AND B.ItemID = C.ItemID GROUP BY C.Category) a;
