import com.mongodb.*;
import java.util.Properties;

/*
    @author Conor Hayes
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
 */
public class DatabaseManager {

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
