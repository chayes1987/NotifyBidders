import com.mongodb.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/*
    @author Conor Hayes
    Code for interacting with MongoDB and sending email
    Java mail -> http://www.tutorialspoint.com/java/java_sending_email.htm
                 http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
 */

public class EmailSender {
    private Properties _config;

    public EmailSender(Properties config){
        this._config = config;
    }

    public void sendEmails(String id, String emails){
        String messageBody = generateMessageBody(id);
        Properties props = getSmtpProperties();
        String[] addresses = emails.split(";");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(_config.getProperty("SENDER_EMAIL"), _config.getProperty("SENDER_PASSWORD")); }
        });

        for(String recipient : addresses){
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(_config.getProperty("SENDER_EMAIL")));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(_config.getProperty("EMAIL_SUBJECT"));
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
        String message = "";
        if (item != null) {
            DBObject itemDetails = (DBObject) item.get("item");
            message = String.format(_config.getProperty("EMAIL_BODY"), itemDetails.get("name"),
                    itemDetails.get("starting_bid"));
        }
        return message;
    }

    private DBObject getItem(String id) {
        MongoClient client;
        DBCollection items;
        try {
            client = new MongoClient(_config.getProperty("SERVER_NAME"),
                    Integer.parseInt(_config.getProperty("PORT_NUMBER")));
            DB db = client.getDB(_config.getProperty("DATABASE_NAME"));
            items = db.getCollection(_config.getProperty("COLLECTION_NAME"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items.findOne(new BasicDBObject("_id", id));
    }

    private Properties getSmtpProperties(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }
}
