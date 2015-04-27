package database;

import java.util.Properties;
import models.AuctionItem;

/**
 * @author Conor Hayes
 * IDatabase
 */
public interface IDatabase {

    /**
     * Get Item
     * @param id The auction ID
     * @param config The configuration file
     * @return The item matching the ID
     */
    AuctionItem getItem(String id, Properties config);
}
