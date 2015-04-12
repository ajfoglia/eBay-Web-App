package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    private IndexWriter indexWriter = null;

    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    /** Retrieves or sets up the index writer object **/
    public IndexWriter getIndexWriter(boolean create) throws IOException {
        // Followed online tutorial
        if(indexWriter == null) {
            try {
                // Make sure and store to /var/lib/lucene/ per the spec
                Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
                IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
                indexWriter = new IndexWriter(indexDir, config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return indexWriter;
    }

    /** Close the index writer **/
    public void closeIndexWriter() throws IOException {
        if(indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void indexObjectItems(int itemID, String itemName, String itemDescription, String itemCategory) throws IOException {
        try {
            IndexWriter writer = getIndexWriter(false);
            Document doc = new Document();
            // Create an index on the ItemID, Name, Description, and Category
            doc.add(new StringField("ItemID", String.valueOf(itemID), Field.Store.YES));
            doc.add(new TextField("ItemName", itemName, Field.Store.YES));
            doc.add(new TextField("ItemDescription", itemDescription, Field.Store.YES));
            doc.add(new TextField("ItemCategory", itemCategory, Field.Store.YES));
            String fullSearchableText = String.valueOf(itemID) + " " + itemName + " " + itemDescription + " " + itemCategory;
            doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
            writer.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rebuildIndexes() {
        try {

            Connection conn = null;

            // create a connection to the database to retrieve Items from MySQL
            try {
                conn = DbManager.getConnection(true);
            } catch (SQLException ex) {
                System.out.println(ex);
            }


        /*
         * Add your code here to retrieve Items using the connection
         * and add corresponding entries to your Lucene inverted indexes.
             *
             * You will have to use JDBC API to retrieve MySQL data from Java.
             * Read our tutorial on JDBC if you do not know how to use JDBC.
             *
             * You will also have to use Lucene IndexWriter and Document
             * classes to create an index and populate it with Items data.
             * Read our tutorial on Lucene as well if you don't know how.
             *
             * As part of this development, you may want to add 
             * new methods and create additional Java classes. 
             * If you create new classes, make sure that
             * the classes become part of "edu.ucla.cs.cs144" package
             * and place your class source files at src/edu/ucla/cs/cs144/.
         * 
         */

            getIndexWriter(true);

            Statement sqlStatement = conn.createStatement();

            // Get everything out of the database
            String preparedSQL = "SELECT I.ItemID, I.Name, I.Description, C.ConcatenatedCategories FROM Item I INNER JOIN (SELECT ItemID, GROUP_CONCAT(Categories.Category SEPARATOR ' ') AS ConcatenatedCategories FROM Categories GROUP BY ItemID) AS C ON I.ItemID = C.ItemID";

            // Execute the query
            ResultSet allItems = sqlStatement.executeQuery(preparedSQL);

            // Index every Item returned into Lucene Index
            while(allItems.next()) {
                indexObjectItems(allItems.getInt("ItemID"), allItems.getString("Name"), allItems.getString("Description"), allItems.getString("ConcatenatedCategories"));
            }

            // Close index writer
            closeIndexWriter();

            // close the database connection
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }

        // Catch exceptions
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}