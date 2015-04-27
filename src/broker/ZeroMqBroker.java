package broker;

import org.jeromq.ZMQ;
import java.util.Properties;
import utils.MessageParser;
import email.EmailSender;

/*
    The official documentation was consulted for the third party library 0mq used in this class
    0mq pub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathopub.java
    0mq sub -> https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/pathosub.java
 */

/**
 * @author Conor Hayes
 * 0mq Broker
 */
public class ZeroMqBroker implements IBroker {
    private ZMQ.Context _context = ZMQ.context();
    private ZMQ.Socket _publisher = _context.socket(ZMQ.PUB);

    /**
     * Subscribes to the CheckHeartbeat command and publishes an acknowledgement
     * @param config The configuration file
     */
    @Override
    public void subscribeToHeartbeat(Properties config) {
        new Thread(
                () -> {
                    ZMQ.Socket heartbeatSub = _context.socket(ZMQ.SUB);
                    // Connect and subscribe to the topic - CheckHeartbeat
                    heartbeatSub.connect(config.getProperty("HEARTBEAT_ADR"));
                    String heartbeatTopic = config.getProperty("CHECK_HEARTBEAT_TOPIC");
                    heartbeatSub.subscribe(heartbeatTopic.getBytes());

                    while(true){
                        String checkHeartbeatEvt = new String(heartbeatSub.recv());
                        System.out.println("REC: " + checkHeartbeatEvt);
                        // Build and send the response
                        String heartbeatResponse = config.getProperty("CHECK_HEARTBEAT_TOPIC_RESPONSE") +
                                " <params>" + config.getProperty("SERVICE_NAME") + "</params>";
                        _publisher.send(heartbeatResponse.getBytes());
                        System.out.println("PUB: " + heartbeatResponse);
                    }
                }
        ).start();
    }

    /**
     * Subscribes to the NotifyBidders command
     * @param config The configuration file
     * @param password The password
     */
    @Override
    public void subscribeToNotifyBiddersCmd(Properties config, String password) {
        ZMQ.Socket notifyBiddersSub = _context.socket(ZMQ.SUB);
        // Connect and subscribe to the topic - NotifyBidders
        notifyBiddersSub.connect(config.getProperty("SUB_ADR"));
        String notifyBiddersTopic = config.getProperty("TOPIC");
        notifyBiddersSub.subscribe(notifyBiddersTopic.getBytes());
        System.out.println("SUB: " + notifyBiddersTopic);
        // Bind the publisher for acknowledgements
        _publisher.bind(config.getProperty("ACK_ADR"));

        while (true) {
            String notifyBiddersCmd = new String(notifyBiddersSub.recv());
            System.out.println("REC: " + notifyBiddersCmd);
            publishAcknowledgement(notifyBiddersCmd);
            // Extract the details and send the emails
            String id = MessageParser.parseMessage(notifyBiddersCmd, "<id>", "</id>");
            String emails = MessageParser.parseMessage(notifyBiddersCmd, "<params>", "</params>");
            EmailSender sender = new EmailSender(config, password);
            sender.sendEmails(id, emails);
        }
    }

    /**
     * Publishes the acknowledgement of the NotifyBidders command
     * @param message The received message
     */
    @Override
    public void publishAcknowledgement(String message) {
        String acknowledgment = "ACK " + message;
        _publisher.send(acknowledgment.getBytes());
        System.out.println("PUB: " + acknowledgment);
    }
}
