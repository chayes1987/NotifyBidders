package models;

/**
 * @author Conor Hayes
 * Auction Item
 */
public class AuctionItem {
    private String name;
    private double starting_bid;

    /**
     * Constructor
     * @param name
     * @param starting_bid
     */
    public AuctionItem(String name, double starting_bid){
        this.name = name;
        this.starting_bid = starting_bid;
    }

    /**
     * Get Starting Bid
     * @return The starting bid
     */
    public double getStarting_bid() {
        return starting_bid;
    }

    /**
     * Get Name
     * @return The name
     */
    public String getName() {
        return name;
    }
}
