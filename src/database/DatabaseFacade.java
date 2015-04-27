package database;

import utils.Constants;

/**
 * @author Conor Hayes
 * Database Facade
 */
public class DatabaseFacade {
    private static IDatabase database;

    /**
     * Initializes the database
     * @return The database to be used
     */
    public static IDatabase getDatabase(){
        if (Constants.DATABASE == DATABASE_TYPE.MongoDB){
            database = new MongoDatabase();
        }
        // Other Databases may be added
        return database;
    }
}
