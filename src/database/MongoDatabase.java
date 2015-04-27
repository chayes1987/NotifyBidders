package database;

import com.mongodb.*;
import java.util.Properties;
import models.AuctionItem;

/*
    Coding Standards -> http://www.oracle.com/technetwork/java/codeconvtoc-136057.html
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
 */

/**
 * @author Conor Hayes
 * Mongo Database
 */
public class MongoDatabase implements IDatabase{

    /**
     * Get Item
     * @param id The ID of the auction
     * @param config The configuration file
     * @return The item
     */
    public AuctionItem getItem(String id, Properties config) {
        MongoClient client;
        DBObject itemDetails;
        try {
            client = new MongoClient(config.getProperty("SERVER_NAME"),
                    Integer.parseInt(config.getProperty("PORT_NUMBER")));
            DB db = client.getDB(config.getProperty("DATABASE_NAME"));
            DBCollection items = db.getCollection(config.getProperty("COLLECTION_NAME"));
            DBObject item = items.findOne(new BasicDBObject("_id", id));
            itemDetails = (DBObject) item.get("item");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String name = (String) itemDetails.get("name");
        double starting_bid = (double) itemDetails.get("starting_bid");

        return new AuctionItem(name, starting_bid);
    }
}
