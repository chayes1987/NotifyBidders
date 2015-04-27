package broker;

import java.util.Properties;

/**
 * @author Conor Hayes
 * IBroker interface
 */
public interface IBroker {
    /**
     * Subscribe To Heartbeat
     * @param config The configuration file
     */
    void subscribeToHeartbeat(Properties config);

    /**
     * Subscribe To Notify Bidders Command
     * @param config The configuration file
     * @param password The password
     */
    void subscribeToNotifyBiddersCmd(Properties config, String password);

    /**
     * Publish Acknowledgement
     * @param message The message to publish
     */
    void publishAcknowledgement(String message);
}
