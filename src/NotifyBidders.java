import broker.BrokerFacade;
import broker.IBroker;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

/*
    Config -> http://www.mkyong.com/java/java-properties-file-examples/
    Coding Standards -> http://www.oracle.com/technetwork/java/codeconvtoc-136057.html
 */

/**
 * @author Conor Hayes
 * Notify Bidders
 */
public class NotifyBidders {

    /**
     * Main Method
     * @param args Command line args
     */
    public static void main(String[] args){
        NotifyBidders nb = new NotifyBidders();
        Properties config = nb.readConfig();

        // Check the configuration
        if(config != null)
            System.out.println("Enter password: ");
            Scanner input = new Scanner(System.in);
            String password = input.nextLine();
            // Create Pub/Sub manager
            IBroker broker = BrokerFacade.getBroker();
            broker.subscribeToHeartbeat(config);
            broker.subscribeToNotifyBiddersCmd(config, password);
    }

    /**
     * Read the configuration file
     * @return The contents of the file in a properties object, null if exception
     */
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
}
