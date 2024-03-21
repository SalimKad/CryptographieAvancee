package projet_crypto;

import java.util.ArrayList;
import java.util.List;

public class EmailInfo {
    private String from;
    private String subject;
    private String sentDate;
    private String content;
    private List<String> attachments; // Chemins des pièces jointes

    public EmailInfo(String from, String subject, String sentDate, String content) {
        this.from = from;
        this.subject = subject;
        this.sentDate = sentDate;
        this.content = content;
        this.attachments = new ArrayList<>();
    }


    // Getters
    public String getFrom() { return from; }
    public String getSubject() { return subject; }
    public String getSentDate() { return sentDate; }
    public String getContent() { return content; }

    public void addAttachment(String attachmentPath) {
        attachments.add(attachmentPath);
    }

    public List<String> getAttachments() {
        return attachments;
    }

    // Utilisé pour l'affichage dans la JList
    @Override
    public String toString() {
        return subject;
    }
}
