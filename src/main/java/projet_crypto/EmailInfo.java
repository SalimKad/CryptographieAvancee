package projet_crypto;

public class EmailInfo {
    String from;
    String subject;
    String sentDate;
    String content; // Ce champ peut être simplifié ou étendu selon le type de contenu que tu veux gérer

    public EmailInfo(String from, String subject, String sentDate, String content) {
        this.from = from;
        this.subject = subject;
        this.sentDate = sentDate;
        this.content = content;
    }

    // Getters
    public String getFrom() { return from; }
    public String getSubject() { return subject; }
    public String getSentDate() { return sentDate; }
    public String getContent() { return content; }

    // Utilisé pour l'affichage dans la JList
    @Override
    public String toString() {
        return subject;
    }
}
