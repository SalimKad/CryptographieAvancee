package communication;

import java.util.ArrayList;
import java.util.List;

public class MessageStorage {

    private static MessageStorage instance;
    private List<message> messages;

    private MessageStorage() {
        messages = new ArrayList<>();
    }

    public static MessageStorage getInstance() {
        if (instance == null) {
            instance = new MessageStorage();
        }
        return instance;
    }

    public void saveMessage(String senderEmail, String recipientEmail, String subject, String message) {
        messages.add(new message(senderEmail, recipientEmail, subject, message));
    }

    public List<message> getMessagesForRecipient(String recipientEmail) {
        List<message> recipientMessages = new ArrayList<>();
        for (message message : messages) {
            if (message.getRecipientEmail().equals(recipientEmail)) {
                recipientMessages.add(message);
            }
        }
        return recipientMessages;
    }
}
