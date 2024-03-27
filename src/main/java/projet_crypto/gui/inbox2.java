package projet_crypto.gui;

import projet_crypto.EmailInfo;
import projet_crypto.IBEBasicIdent;
import projet_crypto.IBEcipher;
import projet_crypto.communication.Mailsendreceive;
import projet_crypto.communication.ServerResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URI;

public class inbox2 extends JFrame implements Serializable {


    private JList<String> emailList;
    private DefaultListModel<String> emailListModel;
    private JTextPane emailDetailsPane;

    private String email;
    private String password;

    private JTextField recipientEmailField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JButton sendButton;
    private JButton attachButton;
    private JButton downloadButton;
    private JButton inboxButton;
    private String selectedFilePath;

    private Pairing pairing;

    private ServerResponse objectResponse;

    private JButton refreshButton;

    private static IBEcipher ibeciphertest;

    public inbox2(String senderEmail, String password, ServerResponse objectResponse) throws IOException {
        this.email = senderEmail;
        this.password = password;
        this.objectResponse = objectResponse;

        pairing = PairingFactory.getPairing("a.properties");


        setTitle("Inbox");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create panel for recipient, subject, message, send button, and attach button
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel recipientPanel = new JPanel(new FlowLayout());
        recipientPanel.add(new JLabel("Recipient Email:"));
        recipientEmailField = new JTextField(20);
        recipientPanel.add(recipientEmailField);

        JPanel subjectPanel = new JPanel(new FlowLayout());
        subjectPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField(20);
        subjectPanel.add(subjectField);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        messageArea = new JTextArea(5, 40);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        sendButton = new JButton("Send Email");
        buttonPanel.add(sendButton);

        // Add the attach button to the button panel
        attachButton = new JButton("Attach File");
        buttonPanel.add(attachButton);

        // Add components to top panel
        topPanel.add(recipientPanel, BorderLayout.NORTH);
        topPanel.add(subjectPanel, BorderLayout.CENTER);
        topPanel.add(messagePanel, BorderLayout.SOUTH);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Add top panel and email display panel to the frame
        add(topPanel, BorderLayout.NORTH);

        // Configuration de emailList et emailDetailsPane comme avant
        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        emailDetailsPane = new JTextPane();
        emailDetailsPane.setContentType("text/html");
        emailDetailsPane.setEditable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(emailList), new JScrollPane(emailDetailsPane));
        splitPane.setDividerLocation(300);

        // Ajoute splitPane au CENTRE de la fenêtre principale
        add(splitPane, BorderLayout.CENTER);

        readAndDisplayEmails2(email, password); // Utilise les bonnes informations d'identification

        refreshButton = new JButton("Refresh Inbox");
        buttonPanel.add(refreshButton);

        // Ajoutez un ActionListener au bouton de rafraîchissement
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Actualisez la boîte de réception
                try {
                    readAndDisplayEmails2(email, password);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Gérez les exceptions si nécessaire
                }
            }
        });



        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String recipientEmail = recipientEmailField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();

                if (selectedFilePath == null || selectedFilePath.isEmpty()) {
                    // Aucun fichier sélectionné : envoyer un email sans pièce jointe
                    try {
                        Mailsendreceive.sendmessage(senderEmail, password, recipientEmail, subject, message);
                        JOptionPane.showMessageDialog(null, "Email sent successfully.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to send email.");
                    }
                } else {

                    //String attachmentName = selectedFilePath != null ? new File(selectedFilePath).getName() : "Attachment";

                    String attachmentName = selectedFilePath != null ?
                            new File(selectedFilePath).getName().replaceAll("\\..*$", "") :
                            "Attachment";

                    // Encrypt the attachment
                    try {
                        //KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), recipientEmail);

                        // Read attachment file
                        File attachmentFile = new File(selectedFilePath);
                        FileInputStream in = new FileInputStream(attachmentFile);
                        byte[] attachmentBytes = new byte[in.available()];
                        in.read(attachmentBytes);

                        String message2 = new String(attachmentBytes);

                        System.out.println("Encryption ....");

                        System.out.println("*****************les parametres en chiffrement ************* : ");
                        System.out.println("le generateur en crypt : " + objectResponse.getP());
                        System.out.println("la P_pub en crypt : " + objectResponse.getP_pub());
                        System.out.println("la Pk en crypt : " + recipientEmail);
                        // Encrypt attachment
                        IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), attachmentBytes, recipientEmail);
                        //ibeciphertest = IBEBasicIdent.IBEencryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), attachmentBytes, objectResponse.getPk());
                        byte[] encryptedAttachment = ibecipher.getAescipher();
                        System.out.println("ibecipher.getAEScipher : " + encryptedAttachment);

                        System.out.println("l'objet ibeCipher U en crypt : " + ibecipher.getU());
                        System.out.println("l'objet ibeCipher V en crypt : " + ibecipher.getV());


                        // Chemin du dossier piece_jointes
                        String attachmentsDirPath = "pieces_jointes";
                        File attachmentsDir = new File(attachmentsDirPath);
                        if (!attachmentsDir.exists()) {
                            attachmentsDir.mkdirs(); // Crée le dossier s'il n'existe pas
                        }

                        // Chemin pour le fichier crypté
                        String encryptedFileName = attachmentsDirPath + File.separator + attachmentName + "_encrypted" + selectedFilePath.substring(selectedFilePath.lastIndexOf("."));
                        System.out.println("le chemin de encryotedFile : " + encryptedFileName);

                        File f = new File(encryptedFileName);
                        f.createNewFile();
                        FileOutputStream fout = new FileOutputStream(f);
                        ObjectOutputStream objectOut = new ObjectOutputStream(fout);
                        //objectOut.writeObject(ibecipher);
                        objectOut.writeObject(ibecipher);
                        objectOut.close();
                        fout.close();


                        System.out.println("To access the resulting file, check the following path: " + f.getAbsolutePath());

                        // Send email with encrypted attachment
                        //Mailsendreceive.sendmessagewithattachement2(senderEmail, password, recipientEmail, attachmentFile.getAbsolutePath(), subject, message);
                        Mailsendreceive.sendmessagewithattachement(senderEmail, password, recipientEmail, f.getAbsolutePath(), subject, message);

                        JOptionPane.showMessageDialog(null, "Email sent successfully.");


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to send email.");
                        // Handle any exceptions
                    }
                }
            }
        });

        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedFilePath = selectedFile.getAbsolutePath();
                }
            }
        });

        downloadButton = new JButton("Download");
        buttonPanel.add(downloadButton); // Make sure you add it to some panel


        downloadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                String encryptedFolderPath = "pieces_jointes";
                String decryptedFolderPath = "dechiffre";

                File encryptedFolder = new File(encryptedFolderPath);
                File[] files = encryptedFolder.listFiles();

                if (files != null && files.length > 0) {
                    File lastFile = files[files.length - 1];
                    String encryptedFilePath = lastFile.getAbsolutePath();

                    System.out.println("le chemin du encrypted fil utilise en dechiffrement : " + encryptedFilePath);

                    try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(encryptedFilePath))) {
                        IBEcipher ibecipher = (IBEcipher) objectInputStream.readObject();

                        System.out.println("******les parametres en dechiffrement ****** : ");
                        System.out.println("le generateur en decrypt : " + objectResponse.getP());
                        System.out.println("la P_pub en decrypt : " + objectResponse.getP_pub());
                        System.out.println("la sk en decrypt : " + objectResponse.getSk());
                        System.out.println("l'objet ibeCipher en decrypt : " + ibeciphertest);
                        System.out.println("l'objet ibeCipher U en crypt : " + ibecipher.getU());
                        System.out.println("l'objet ibeCipher V en crypt : " + ibecipher.getV());

   		             /*   if (ibeciphertest != null) {
   		                	byte[] decryptedAttachment = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibeciphertest);

   	   		             String retrieved_message = new String(decryptedAttachment);
   	   		            System.out.println("the decrypted message is: \n" + retrieved_message);

   	   		            // Write the decrypted message to a file
   	   		            File f = new File("decryptionresult" + encryptedFilePath.substring(encryptedFilePath.lastIndexOf(".")));
   	   		            f.createNewFile();
   	   		            FileOutputStream fout = new FileOutputStream(f);
   	   		            fout.write(decryptedAttachment);
   	   		            System.out.println("To access the resulting file, check the following path: " + f.getAbsolutePath());
   	   		      JOptionPane.showMessageDialog(null, "Attachment downloaded and decrypted successfully. Saved in the 'dechiffre' folder.");
   		                }:*/



                        byte[] decryptedAttachment = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibecipher);

                        String extensionWithDot = encryptedFilePath.substring(encryptedFilePath.lastIndexOf("."));
                        File decryptedFolder = new File(decryptedFolderPath);
                        decryptedFolder.mkdirs(); // Ensure the decrypted folder exists

                        // Extract the filename from the encrypted file path
                        //  String encryptedFileName = new File(encryptedFilePath).getName();
                        String encryptedFileName = encryptedFilePath != null ?
                                new File(encryptedFilePath).getName().replaceAll("\\..*$", "") :
                                "Attachment";

                        File decryptedAttachmentFile = new File(decryptedFolder, encryptedFileName+"_decrypted" + extensionWithDot);

                        try (FileOutputStream fileOutputStream = new FileOutputStream(decryptedAttachmentFile)) {
                            fileOutputStream.write(decryptedAttachment);
                        }


                    } catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException er) {
                        er.printStackTrace();
                        // Handle exceptions appropriately
                    }
                } else {
                    System.err.println("No files found in the folder: " + encryptedFolderPath);
                    // Handle the case when no files are found in the folder
                }
            }
        });

    }





    public void readAndDisplayEmails2(String email, String password) throws IOException {
        try {
            List<EmailInfo> emailsList = Mailsendreceive.readInbox(email, password);

            SwingUtilities.invokeLater(() -> {
                emailListModel.clear();
                for (int i = emailsList.size() - 1; i >= 0; i--) {
                    EmailInfo emailInfo = emailsList.get(i);
                    emailListModel.addElement(emailInfo.toString());
                }
            });

            emailList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int index = emailList.getSelectedIndex();
                    if (index >= 0 && index < emailsList.size()) {
                        EmailInfo selectedEmailInfo = emailsList.get(emailsList.size() - 1 - index);
                        String from = selectedEmailInfo.getFrom();
                        String subject = selectedEmailInfo.getSubject();
                        String sentDate = selectedEmailInfo.getSentDate();
                        String content = selectedEmailInfo.getContent();

                        // Fetch attachments
                        List<String> attachments = selectedEmailInfo.getAttachments();

                        // Display email content including attachments
                        StringBuilder detailsHtml = new StringBuilder("<html><body><h2>")
                                .append(subject)
                                .append("</h2><h3>From: ")
                                .append(from)
                                .append("</h3><h4>Sent: ")
                                .append(sentDate)
                                .append("</h4><p>")
                                .append(content)
                                .append("</p>");

                        // Append attachment details as clickable links
                        if (!attachments.isEmpty()) {
                            detailsHtml.append("<h4>Attachments:</h4><ul>");
                            for (String attachment : attachments) {
                                String fileName = new File(attachment).getName();
                                detailsHtml.append("<li><a href=\"")
                                        .append(attachment)
                                        .append("\">")
                                        .append(fileName)
                                        .append("</a></li>");
                            }
                            detailsHtml.append("</ul>");
                        }

                        detailsHtml.append("</body></html>");

                        emailDetailsPane.setContentType("text/html");
                        emailDetailsPane.setText(detailsHtml.toString());

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void readAndDisplayEmails3(String email, String password) throws IOException {
        try {
            List<EmailInfo> emailsList = Mailsendreceive.readInbox(email, password);

            SwingUtilities.invokeLater(() -> {
                emailListModel.clear();
                for (int i = emailsList.size() - 1; i >= 0; i--) {
                    EmailInfo emailInfo = emailsList.get(i);
                    emailListModel.addElement(emailInfo.toString());
                }
            });

            emailList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int index = emailList.getSelectedIndex();
                    if (index >= 0 && index < emailsList.size()) {
                        EmailInfo selectedEmailInfo = emailsList.get(emailsList.size() - 1 - index);
                        String from = selectedEmailInfo.getFrom();
                        String subject = selectedEmailInfo.getSubject();
                        String sentDate = selectedEmailInfo.getSentDate();
                        String content = selectedEmailInfo.getContent();

                        // Fetch attachments
                        List<String> attachments = selectedEmailInfo.getAttachments();

                        // Display email content including attachments
                        StringBuilder detailsHtml = new StringBuilder("<html><body><h2>")
                                .append(subject)
                                .append("</h2><h3>From: ")
                                .append(from)
                                .append("</h3><h4>Sent: ")
                                .append(sentDate)
                                .append("</h4><p>")
                                .append(content)
                                .append("</p>");

                        // Append attachment details as clickable links
                        if (!attachments.isEmpty()) {
                            detailsHtml.append("<h4>Attachments:</h4><ul>");
                            for (String attachment : attachments) {
                                String fileName = new File(attachment).getName();
                                detailsHtml.append("<li><a href=\"")
                                        .append(attachment)
                                        .append("\">")
                                        .append(fileName)
                                        .append("</a></li>");
                            }
                            detailsHtml.append("</ul>");
                        }

                        detailsHtml.append("</body></html>");

                        emailDetailsPane.setContentType("text/html");
                        emailDetailsPane.setText(detailsHtml.toString());
                        emailDetailsPane.addHyperlinkListener(e1 -> {
                            if (e1.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                URL url = e1.getURL();
                                if (url != null) {
                                    // Decrypt the file and save the decrypted file to another folder
                                    try {
                                        String attachmentLink = url.toString();
                                        File attachmentFile = new File(new URI(attachmentLink));

                                        if (attachmentFile.exists()) {
                                            // Read and decrypt the attachment
                                            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(attachmentFile));
                                            IBEcipher ibeCipher = (IBEcipher) objectInputStream.readObject();
                                            objectInputStream.close();
                                            byte[] decryptedAttachment = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibeCipher);

                                            // Save the decrypted attachment to a file
                                            String originalFileName = attachmentFile.getName(); // Get the original file name
                                            String decryptedFileName = "Decrypted_" + originalFileName;
                                            File destinationFolder = new File("dechiffre");
                                            if (!destinationFolder.exists()) {
                                                destinationFolder.mkdirs(); // Create the destination folder if it doesn't exist
                                            }
                                            File decryptedFile = new File(destinationFolder, decryptedFileName);
                                            FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
                                            fileOutputStream.write(decryptedAttachment);
                                            fileOutputStream.close();

                                            System.out.println("Decrypted file saved successfully to: " + decryptedFile.getAbsolutePath());
                                        } else {
                                            System.out.println("Attachment file not found.");
                                        }
                                    } catch (IOException | ClassNotFoundException | URISyntaxException ex) {
                                        ex.printStackTrace();
                                    } catch (InvalidKeyException e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    } catch (NoSuchAlgorithmException e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    } catch (NoSuchPaddingException e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    } catch (IllegalBlockSizeException e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    } catch (BadPaddingException e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    }
                                }
                            }
                        });

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Retrieve email and password from the connexion interface
        connexion connexionInterfaces = new connexion();
        String email = connexionInterfaces.getEmailFieldText();
        String password = connexionInterfaces.getPasswordFieldText();

    }
}