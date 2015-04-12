CREATE TABLE SearchRegion (ItemID integer, coord POINT NOT NULL, Latitude varchar(400) NOT NULL, Longitude varchar(400) NOT NULL) ENGINE=MyIsam;
INSERT INTO SearchRegion (ItemID, Latitude, Longitude) SELECT ItemID, Latitude, Longitude FROM Item WHERE Latitude != "";
UPDATE SearchRegion SET coord = POINT(Latitude,Longitude);
CREATE SPATIAL INDEX sp_index ON SearchRegion(coord);