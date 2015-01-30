import com.mongodb.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.net.UnknownHostException;
import java.util.Properties;

/*
    @author Conor Hayes
    Code for interacting with MongoDB and sending email
    Java mail -> http://www.tutorialspoint.com/java/java_sending_email.htm
                 http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
 */

public class EmailSender {
    private final String SERVER_NAME = "localhost";
    private final int PORT_NUMBER = 27018;

    public void sendEmails(String id, String emails){
        final String sender = "online.web.auctions@gmail.com";
        final String senderPassword = "online.auctions";
        String messageBody = generateMessageBody(id);
        Properties props = getProperties();
        String[] addresses = emails.split(";");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(sender, senderPassword); }
        });

        for(String recipient : addresses){
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Auction Starting");
                message.setText(messageBody);
                Transport.send(message);
                System.out.println("Email Sent to " + recipient);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateMessageBody(String id) {
        DBObject item = getItem(id);
        DBObject itemDetails = (DBObject)item.get("item");
        StringBuilder email = new StringBuilder();
        email.append("Dear Bidder,\n\n");
        email.append("The auction which you are registered for is about to begin.\n");
        email.append("The item for auction is '" + itemDetails.get("name"));
        email.append("'. The starting price for this item is €" + itemDetails.get("starting_bid"));
        email.append(". Please login to the Online Auctions App to take part.\n\n");
        email.append("Regards,\nOnline Auctions\n\n");
        email.append("This email was generated automatically. Do not reply.");
        return email.toString();
    }

    private DBObject getItem(String id) {
        MongoClient client = null;
        try {
            client = new MongoClient(SERVER_NAME , PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = client.getDB("AuctionItems");
        DBCollection items = db.getCollection("items");
        return items.findOne(new BasicDBObject("_id", id));
    }

    private Properties getProperties(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }
}
