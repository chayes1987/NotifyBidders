import com.mongodb.*;
import java.util.Properties;

/**
 * @author Conor Hayes
 */

/*
    Coding Standards -> http://www.oracle.com/technetwork/java/codeconvtoc-136057.html
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
 */

/**
 * This class handles all database interaction
 */
public class DatabaseManager {

    /**
     * Retrieve the item from the database
     * @param id The ID of the auction
     * @param config The configuration file
     * @return The item
     */
    public static DBObject getItem(String id, Properties config) {
        MongoClient client;
        DBCollection items;
        try {
            client = new MongoClient(config.getProperty("SERVER_NAME"),
                    Integer.parseInt(config.getProperty("PORT_NUMBER")));
            DB db = client.getDB(config.getProperty("DATABASE_NAME"));
            items = db.getCollection(config.getProperty("COLLECTION_NAME"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items.findOne(new BasicDBObject("_id", id));
    }
}
