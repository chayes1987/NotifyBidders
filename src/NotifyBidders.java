import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;

/*
    @author Conor Hayes
    The official documentation was consulted for the third party library 0mq used in this class
    0mq pub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathopub.java
    0mq sub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathosub.java
 */

public class NotifyBidders {
    private final String SUBSCRIBER_ADDRESS = "tcp://127.0.0.1:1001",
            PUBLISHER_ADDRESS = "tcp://127.0.0.1:1111";
    private Context context;

    public static void main(String[] args){ new NotifyBidders().subscribe(); }

    private void subscribe() {
        context = ZMQ.context(1);
        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(SUBSCRIBER_ADDRESS);
        subscriber.subscribe("NotifyBidder".getBytes());
        System.out.println("Subscribed to NotifyBidder command...");

        while (true) {
            String message = new String(subscriber.recv());
            System.out.println("Received " + message + " command");
            publishAcknowledgement(message);
            String id = parseMessage(message, "<id>", "</id>");
            String emails = parseMessage(message, "<params>", "</params>");
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmails(id, emails);
        }
    }

    private void publishAcknowledgement(String message){
        Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind(PUBLISHER_ADDRESS);
        String msg = "ACK: " + message;
        publisher.send(msg.getBytes());
        System.out.println("Acknowledgement sent...");
    }

    private String parseMessage(String message, String startTag, String endTag){
        int startIndex = message.indexOf(startTag) + startTag.length();
        String substring = message.substring(startIndex);
        return substring.substring(0, substring.lastIndexOf(endTag));
    }
}
