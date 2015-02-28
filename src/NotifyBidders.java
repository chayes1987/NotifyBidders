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
    private Socket _ackPublisher = _context.socket(ZMQ.PUB);
    private static Properties _config;

    public static void main(String[] args){
        NotifyBidders nb = new NotifyBidders();
        _config = nb.readConfig();

        if(_config != null)
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
        _ackPublisher.bind(_config.getProperty("ACK_ADR"));

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
        String msg = "ACK " + message;
        _ackPublisher.send(msg.getBytes());
        System.out.println("PUB: " + msg);
    }

    private String parseMessage(String message, String startTag, String endTag){
        int startIndex = message.indexOf(startTag) + startTag.length();
        String substring = message.substring(startIndex);
        return substring.substring(0, substring.lastIndexOf(endTag));
    }
}
