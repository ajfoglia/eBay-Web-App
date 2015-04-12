**********************************************************
TEAM: Me_Only
**********************************************************

Andy Foglia
903895596

For the indexer I followed the example online exactly. I made sure to put the index in the proper directory as well.

I wrote a few helper functions as well:
1. getIndexWriter - Retrieves or sets up the index writer object
2. closeIndexWriter - Close the writer
3. indexObjectItems - Since we are searching over the union of the Name, Description and Category fields from Item I index over those three fields following the online example.

In rebuildIndexes, I open a connection and pull all the relevant information I need in a prepared SQL statement. This allows me to index every single item. I then close the writer.

Again this part was per the example word for word almost, and I had limited issues except some minor issues with exception handling.

I did not leave many comments but I think the code is self-explanatory.
