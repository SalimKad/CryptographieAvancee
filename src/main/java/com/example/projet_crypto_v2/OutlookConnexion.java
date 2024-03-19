package com.example.projet_crypto_v2;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class OutlookConnexion {
    //fonction verifyOutlookCredentials, mets en place les propreties et tente de se connecter au serveur de outlook :
    public static boolean verifyOutlookCredentials(String email, String password) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.trust", "smtp.office365.com");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.office365.com", email, password);
            //transport.connect("outlook.office365.com", email, password);
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void sendmessage(String user, String password, String destination, String subject, String messageText){
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.trust", "smtp.office365.com");

       /*Properties properties = new Properties();
       properties.setProperty("mail.smtp.starttls.enable", "true");
       properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");*/
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user,password);
            }
        });
        System.out.println("session.getProviders():"+session.getProviders()[0].getType());
        try{
            MimeMessage message=new MimeMessage(session);
            message.setFrom(user);
            message.setText(messageText);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject(subject);
            Transport.send(message);


        } catch (NoSuchProviderException e) {e.printStackTrace();}
        catch (MessagingException e) {e.printStackTrace();}

    }
}
