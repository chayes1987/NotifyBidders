import org.jeromq.ZMQ;
import org.jeromq.ZMQ.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

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
    private static String _password;

    public static void main(String[] args){
        NotifyBidders nb = new NotifyBidders();
        _config = nb.readConfig();

        if(_config != null)
            System.out.println("Enter password: ");
            Scanner input = new Scanner(System.in);
            _password = input.nextLine();
            nb.subToHeartbeat();
            nb.subToNotifyBiddersCmd();
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

    private void subToNotifyBiddersCmd() {
        Socket notifyBiddersSub = _context.socket(ZMQ.SUB);
        notifyBiddersSub.connect(_config.getProperty("SUB_ADR"));
        String notifyBiddersTopic = _config.getProperty("TOPIC");
        notifyBiddersSub.subscribe(notifyBiddersTopic.getBytes());
        System.out.println("SUB: " + notifyBiddersTopic);
        _publisher.bind(_config.getProperty("ACK_ADR"));

        while (true) {
            String notifyBiddersCmd = new String(notifyBiddersSub.recv());
            System.out.println("REC: " + notifyBiddersCmd);
            publishAcknowledgement(notifyBiddersCmd);
            String id = parseMessage(notifyBiddersCmd, "<id>", "</id>");
            String emails = parseMessage(notifyBiddersCmd, "<params>", "</params>");
            EmailSender sender = new EmailSender(_config, _password);
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

    private void subToHeartbeat(){
        new Thread(
                () -> {
                    Socket heartbeatSub = _context.socket(ZMQ.SUB);
                    heartbeatSub.connect(_config.getProperty("HEARTBEAT_ADR"));
                    String heartbeatTopic = _config.getProperty("CHECK_HEARTBEAT_TOPIC");
                    heartbeatSub.subscribe(heartbeatTopic.getBytes());

                    while(true){
                        String checkHeartbeatEvt = new String(heartbeatSub.recv());
                        System.out.println("REC: " + checkHeartbeatEvt);
                        String heartbeatResponse = _config.getProperty("CHECK_HEARTBEAT_TOPIC_RESPONSE") +
                                " <params>" + _config.getProperty("SERVICE_NAME") + "</params>";
                        _publisher.send(heartbeatResponse.getBytes());
                        System.out.println("PUB: " + heartbeatResponse);
                    }
                }
        ).start();
    }
}
