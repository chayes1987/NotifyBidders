import com.mongodb.*;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;

public class NotifyBidders {
    private final String SUBSCRIBER_ADDRESS = "tcp://127.0.0.1:1001";

    public static void main(String[] args){ new NotifyBidders().subscribe(); }

    private void subscribe() {
        Context context = ZMQ.context(1);
        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(SUBSCRIBER_ADDRESS);
        subscriber.connect(SUBSCRIBER_ADDRESS);
        subscriber.subscribe("NotifyBidder".getBytes());
        System.out.println("Subscribed to NotifyBidder command...");

        while (true) {
            String message = new String(subscriber.recv());
            System.out.println("Received " + message + " command");
            String id = parseMessage(message, "<id>", "</id>");
            String emails = parseMessage(message, "<params>", "</params>");
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmails(id, emails);
        }
    }

    private String parseMessage(String message, String startTag, String endTag){
        int startIndex = message.indexOf(startTag) + startTag.length();
        String substring = message.substring(startIndex);
        return substring.substring(0, substring.lastIndexOf(endTag));
    }
}
