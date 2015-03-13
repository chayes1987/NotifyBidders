import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;
import java.io.FileInputStream;
import java.util.Properties;

/*
    @author Conor Hayes
    The official documentation was consulted for the third party library 0mq used in this class
    0mq pub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathopub.java
    0mq sub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathosub.java
    Config -> http://www.mkyong.com/java/java-properties-file-examples/
 */

public class NotifyBidders {
    private Context _context = ZMQ.context();
    private Socket _publisher = _context.socket(ZMQ.PUB);
    private static Properties _config;

    public static void main(String[] args){
        NotifyBidders nb = new NotifyBidders();
        _config = nb.readConfig();

        if(_config != null)
            nb.subscribeToHeartbeat();
            nb.subscribe();
    }

    private Properties readConfig() {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("properties/config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

    private void subscribe() {
        Socket notifyBiddersSub = _context.socket(ZMQ.SUB);
        notifyBiddersSub.connect(_config.getProperty("SUB_ADR"));
        String topic = _config.getProperty("TOPIC");
        notifyBiddersSub.subscribe(topic.getBytes());
        System.out.println("SUB: " + topic);
        _publisher.bind(_config.getProperty("ACK_ADR"));

        while (true) {
            String notifyBiddersCmd = new String(notifyBiddersSub.recv());
            System.out.println("REC: " + notifyBiddersCmd);
            publishAcknowledgement(notifyBiddersCmd);
            String id = parseMessage(notifyBiddersCmd, "<id>", "</id>");
            String emails = parseMessage(notifyBiddersCmd, "<params>", "</params>");
            EmailSender sender = new EmailSender(_config);
            sender.sendEmails(id, emails);
        }
    }

    private void publishAcknowledgement(String message){
        String acknowledgment = "ACK " + message;
        _publisher.send(acknowledgment.getBytes());
        System.out.println("PUB: " + acknowledgment);
    }

    private String parseMessage(String message, String startTag, String endTag){
        int startIndex = message.indexOf(startTag) + startTag.length();
        String substring = message.substring(startIndex);
        return substring.substring(0, substring.lastIndexOf(endTag));
    }

    private void subscribeToHeartbeat(){
        new Thread(
                () -> {
                    Socket subscriber = _context.socket(ZMQ.SUB);
                    subscriber.connect(_config.getProperty("HEARTBEAT_ADR"));
                    String topic = _config.getProperty("CHECK_HEARTBEAT_TOPIC");
                    subscriber.subscribe(topic.getBytes());

                    while(true){
                        String checkHeartbeatEvt = new String(subscriber.recv());
                        System.out.println("REC: " + checkHeartbeatEvt);
                        String message = _config.getProperty("CHECK_HEARTBEAT_TOPIC_RESPONSE") +
                                " <params>" + _config.getProperty("SERVICE_NAME") + "</params>";
                        _publisher.send(message.getBytes());
                        System.out.println("PUB: " + message);
                    }
                }
        ).start();
    }
}
