import com.mongodb.*;
import java.net.UnknownHostException;

public class NotifyBidders {
    private final String SERVER_NAME = "localhost";
    private final int PORT_NUMBER = 27018;

    public static void main(String[] args){ new NotifyBidders().connect(); }

    private void connect(){
        MongoClient client = null;
        try {
            client = new MongoClient(SERVER_NAME , PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = client.getDB("AuctionItems");
        System.out.println(db.getName());
    }
}
