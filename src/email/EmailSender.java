package email;

import database.DatabaseFacade;
import database.IDatabase;
import models.AuctionItem;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/*
    Code for interacting with MongoDB and sending email
    Java mail -> http://www.tutorialspoint.com/java/java_sending_email.htm
                 http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
    MongoDB -> http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
    Coding Standards -> http://www.oracle.com/technetwork/java/codeconvtoc-136057.html
 */

/**
 * @author Conor Hayes
 * Email Sender
 */
public class EmailSender {
    private Properties _config;
    private String _password;

    /**
     * Constructor
     * @param config The configuration file
     * @param password The password
     */
    public EmailSender(Properties config, String password){
        this._config = config;
        this._password = password;
    }

    /**
     * Sends the emails
     * @param id The ID of the auction
     * @param emails The string of semi-colon-separated emails
     */
    public void sendEmails(String id, String emails){
        String messageBody = generateMessageBody(id);
        Properties props = getSmtpProperties();
        String[] addresses = emails.split(";");
        String username = _config.getProperty("SENDER_EMAIL");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, _password); }
        });

        // Send each email one-by-one
        for(String recipient : addresses){
            try {
                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(username));
                email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                email.setSubject(_config.getProperty("EMAIL_SUBJECT"));
                email.setText(messageBody);
                Transport.send(email);
                System.out.println("EMAIL SENT: " + recipient);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Builds the e-mails body
     * @param id The ID of the auction
     * @return The e-mail body
     */
    private String generateMessageBody(String id) {
        IDatabase database = DatabaseFacade.getDatabase();
        AuctionItem item = database.getItem(id, _config);
        String message = "";
        if (item != null) {
            // Build message using item information and configuration file
            message = String.format(_config.getProperty("EMAIL_BODY"), item.getName(), item.getStarting_bid());
        }
        return message;
    }

    /**
     * Generates the required properties for e-mail
     * @return The properties
     */
    private Properties getSmtpProperties(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", _config.getProperty("SMTP_HOST"));
        props.put("mail.smtp.port", _config.getProperty("SMTP_PORT"));
        return props;
    }
}
