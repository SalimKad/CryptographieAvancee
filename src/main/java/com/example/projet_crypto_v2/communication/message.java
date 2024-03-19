package com.example.projet_crypto_v2.communication;

public class message {

    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String message;

    public message(String senderEmail, String recipientEmail, String subject, String message) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
