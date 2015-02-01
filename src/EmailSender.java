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
    public void sendEmails(String id, String emails){
        String messageBody = generateMessageBody(id);
        Properties props = getProperties();
        String[] addresses = emails.split(";");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(Constants.SENDER_EMAIL, Constants.SENDER_PASSWORD); }
        });

        for(String recipient : addresses){
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Constants.SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(Constants.EMAIL_SUBJECT);
                message.setText(messageBody);
                Transport.send(message);
                System.out.println("EMAIL SENT: " + recipient);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateMessageBody(String id) {
        DBObject item = getItem(id);
        DBObject itemDetails = (DBObject)item.get("item");
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear Bidder,\n\n");
        emailBody.append("The auction which you are registered for is about to begin.\n");
        emailBody.append("The item for auction is '" + itemDetails.get("name"));
        emailBody.append("'. The starting price for this item is €" + itemDetails.get("starting_bid"));
        emailBody.append(". Please login to the Online Auctions App to take part.\n\n");
        emailBody.append("Regards,\nOnline Auctions\n\n");
        emailBody.append("This email was generated automatically. Do not reply.");
        return emailBody.toString();
    }

    private DBObject getItem(String id) {
        MongoClient client = null;
        try {
            client = new MongoClient(Constants.SERVER_NAME , Constants.PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = client.getDB(Constants.DATABASE_NAME);
        DBCollection items = db.getCollection(Constants.COLLECTION_NAME);
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
