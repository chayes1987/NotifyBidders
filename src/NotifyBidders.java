import com.mongodb.*;
import java.net.UnknownHostException;
import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;

public class NotifyBidders {
    private final String SUBSCRIBER_ADDRESS = "tcp://127.0.0.1:1001", SERVER_NAME = "localhost";
    private final int PORT_NUMBER = 27018;

    public static void main(String[] args){ new NotifyBidders().subscribe(); }

    private void subscribe() {
        Context context = ZMQ.context(1);
        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(SUBSCRIBER_ADDRESS);
        System.out.println("Connected");
    }

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
