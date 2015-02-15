import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;

/*
    @author Conor Hayes
    The official documentation was consulted for the third party library 0mq used in this class
    0mq pub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathopub.java
    0mq sub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathosub.java
 */

public class NotifyBidders {
    private Context context = ZMQ.context();
    private Socket ackPublisher = context.socket(ZMQ.PUB);

    public static void main(String[] args){ new NotifyBidders().subscribe(); }

    private void subscribe() {
        Socket notifyBiddersSub = context.socket(ZMQ.SUB);
        notifyBiddersSub.connect(Constants.RECEIVE_ADR);
        notifyBiddersSub.subscribe(Constants.TOPIC.getBytes());
        System.out.println("SUB: " + Constants.TOPIC);
        ackPublisher.bind(Constants.SEND_ADR);

        while (true) {
            String notifyBiddersCmd = new String(notifyBiddersSub.recv());
            System.out.println("REC: " + notifyBiddersCmd);
            publishAcknowledgement(notifyBiddersCmd);
            String id = parseMessage(notifyBiddersCmd, "<id>", "</id>");
            String emails = parseMessage(notifyBiddersCmd, "<params>", "</params>");
            new EmailSender().sendEmails(id, emails);
        }
    }

    private void publishAcknowledgement(String message){
        String msg = "ACK: " + message;
        ackPublisher.send(msg.getBytes());
        System.out.println("ACK SENT...");
    }

    private String parseMessage(String message, String startTag, String endTag){
        int startIndex = message.indexOf(startTag) + startTag.length();
        String substring = message.substring(startIndex);
        return substring.substring(0, substring.lastIndexOf(endTag));
    }
}
