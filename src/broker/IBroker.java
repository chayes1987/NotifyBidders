package broker;

import java.util.Properties;

/**
 * @author Conor Hayes
 * IBroker interface
 */
public interface IBroker {
    void subscribeToHeartbeat(Properties config);
    void subscribeToNotifyBiddersCmd(Properties config, String password);
    void publishAcknowledgement(String message);
}
