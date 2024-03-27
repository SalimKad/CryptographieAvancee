package projet_crypto.communication;

import projet_crypto.EmailInfo;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;


public class Mailsendreceive {
    public static void sendmessage(String user, String password, String destination) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.ssl.trust", "smtp.outlook.com");//Pour le pc de Salim
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); //Pour le pc de Salim
        properties.put("mail.smtp.port", "587");

       /*Properties properties = new Properties();
       properties.setProperty("mail.smtp.starttls.enable", "true");
       properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");*/
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        System.out.println("session.getProviders():" + session.getProviders()[0].getType());
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(user);
            message.setText("Bonjour, \n ceci est mon premier mail depuis javamail ...");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject("mon premier email ..");
            Transport.send(message);


        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static void sendmessage2(String user, String password, String destination, String subject, String messageContent) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.ssl.trust", "smtp.outlook.com");//Pour le pc de Salim
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); //Pour le pc de Salim
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject(subject);
            message.setText(messageContent);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }


    public static void sendmessagewithattachement(String user, String password, String destination, String attachement_path) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.outlook.com");//Pour le pc de Salim

        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        System.out.println("session.getProviders():" + session.getProviders()[0].getType());
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(user);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject("mon premier email avec piece jointe..");

            Multipart myemailcontent = new MimeMultipart();
            MimeBodyPart bodypart = new MimeBodyPart();
            bodypart.setText("ceci est un test de mail avec piece jointe ...");


            MimeBodyPart attachementfile = new MimeBodyPart();
            attachementfile.attachFile(attachement_path);
            myemailcontent.addBodyPart(bodypart);
            myemailcontent.addBodyPart(attachementfile);
            message.setContent(myemailcontent);
            Transport.send(message);

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(Mailsendreceive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void sendmessagewithattachement2(String user, String password, String destination, String attachement_path, String subject, String messageContent) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.outlook.com");//Pour le pc de Salim

        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject(subject);

            Multipart myemailcontent = new MimeMultipart();
            MimeBodyPart bodypart = new MimeBodyPart();
            bodypart.setText(messageContent);

            MimeBodyPart attachementfile = new MimeBodyPart();
            attachementfile.attachFile(attachement_path);
            myemailcontent.addBodyPart(bodypart);
            myemailcontent.addBodyPart(attachementfile);
            message.setContent(myemailcontent);
            Transport.send(message);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadEmailAttachments(String userName, String password) throws ClassNotFoundException {
        Properties properties = new Properties();

        // server setting (it can be pop3 too
        properties.put("mail.imap.host", "outlook.office365.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.smtp.ssl.trust", "outlook.office365.com");//Pour le pc de Salim
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); //Pour le pc de Salim
        properties.setProperty("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port", "993");


        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store imap or pop3
            //     Store store = session.getStore("pop3");
            Store store = session.getStore("imap");

            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();

            // Lecture de l'objet IBEcipher depuis le fichier
            // Assurez-vous d'avoir le chemin correct du fichier IBEcipher


            for (int i = 0; i < arrayMessages.length; i++) {
                Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = message.getSentDate().toString();
                String contentType = message.getContentType();
                String messageContent = "";
                boolean message_seen = message.getFlags().contains(Flags.Flag.SEEN);
                // store attachment file name, separated by comma
                String attachFiles = "";

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();
                            attachFiles += fileName + ", ";
                            part.saveFile("pieces_jointes" + File.separator + fileName); // le dossier Myfiles à créer dans votre projet


                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("message seen ?:" + message_seen);
                System.out.println("\t From: " + from);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
                System.out.println("\t Attachments: " + attachFiles);
                System.out.println("\t check Myfiles folder to access the attachement file ..");

            }

            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for imap.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<EmailInfo> readInbox(String user, String password) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", "outlook.office365.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.auth", "true");
        properties.put("mail.imap.ssl.trust", "outlook.office365.com");

        Session session = Session.getDefaultInstance(properties);
        List<EmailInfo> emailInfos = new ArrayList<>();
        try {
            Store store = session.getStore();
            store.connect(user, password);
            System.out.println("Connected to the mail server ...");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            System.out.println("Inbox opened ...");

            Message[] messages = inbox.getMessages();
            for (Message msg : messages) {
                String from = ((InternetAddress) msg.getFrom()[0]).getAddress();
                String subject = msg.getSubject();
                String sentDate = msg.getSentDate().toString();
                String content = getTextFromMessage(msg);

                EmailInfo emailInfo = new EmailInfo(from, subject, sentDate, content);

                //Traitement des pièces jointes, très long car on les télécharge toutes
                if (msg.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) msg.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            // Sauvegarde de la pièce jointe
                            String fileName = bodyPart.getFileName();
                            //String filePath = "pieces_jointes" + File.separator + fileName;
                            //((MimeBodyPart) bodyPart).saveFile(filePath);
                            emailInfo.addAttachment(fileName);
                        }
                    }
                }

                emailInfos.add(emailInfo);
            }

            //System.out.println("Liste des contenus de mail : " + emailInfos.get(3).getContent());

            inbox.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emailInfos;
    }
 
    
  
    private static List<byte[]> getAttachmentsFromMessage(Message message) throws MessagingException, IOException {
        List<byte[]> attachments = new ArrayList<>();

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    // Convert attachment to byte array
                    InputStream is = bodyPart.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    attachments.add(baos.toByteArray());
                }
            }
        }

        return attachments;
    }


    /**
     * This method returns the primary text content of the message.
     */
    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return (String) bodyPart.getContent();
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    // Optionnel : convertir le HTML en texte simple pour une meilleure lisibilité
                    // ou le traiter selon les besoins de l'application
                    return html;
                }
            }
        }
        return "";
    }



    public static void main(String[] args) {
        //Fonction pour faire nos tests :

        //String host = "outlook.office365.com";//change accordingly
        String username = "cryptoprojet4A@outlook.com";
        String password = "4nbG4zeT5q66JV";//change accordingly
        //  sendmessage(username, password);

        String path = "pieces_jointes/Photo_Chat_Noir_et_blanc.jpg";

        //sendmessagewithattachement2(username, password, username, path , "test", "test");

        System.out.println("message sent ...");

        /*Scanner sc = new Scanner(System.in);
        System.out.println("type something ....");

        sc.nextLine();*/

        readInbox(username, password);

        //downloadEmailAttachments(username, password);

    }

	
}
