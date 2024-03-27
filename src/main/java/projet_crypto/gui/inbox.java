package projet_crypto.gui;

import projet_crypto.EmailInfo;
import projet_crypto.IBEBasicIdent;
import projet_crypto.IBEcipher;
import projet_crypto.communication.Mailsendreceive;
import projet_crypto.communication.ServerResponse;

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
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URLDecoder;

public class inbox extends JFrame implements Serializable {


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
    private JButton inboxButton;
    private String selectedFilePath;

    private Pairing pairing;

    private ServerResponse objectResponse;

    private JButton refreshButton;

    private static IBEcipher ibeciphertest;

    public inbox(String senderEmail, String password, ServerResponse objectResponse) throws IOException {
        this.email = senderEmail;
        this.password = password;
        this.objectResponse = objectResponse;

        pairing = PairingFactory.getPairing("a.properties");


        setTitle("Inbox");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Utilisation de GridBagLayout pour plus de flexibilité dans l'agencement des composants
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Ajout du champ de texte du destinataire avec étiquette
        JPanel recipientPanel = new JPanel(new BorderLayout());
        recipientPanel.add(new JLabel("Email to:  "), BorderLayout.WEST);
        recipientEmailField = new JTextField();
        recipientPanel.add(recipientEmailField, BorderLayout.CENTER);
        topPanel.add(recipientPanel, gbc);

        gbc.insets = new Insets(10, 0, 0, 0); // 10 pixels d'espace au-dessus du composant suivant

        // Ajout du champ de texte du sujet avec étiquette
        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.add(new JLabel("Subject:  "), BorderLayout.WEST);
        subjectField = new JTextField();
        subjectPanel.add(subjectField, BorderLayout.CENTER);
        topPanel.add(subjectPanel, gbc);

        gbc.insets = new Insets(10, 0, 0, 0); // 10 pixels d'espace au-dessus du composant suivant

        // Ajout de la zone de message
        gbc.weighty = 1; // Donne un poids vertical à la zone de message pour qu'elle prenne l'espace restant
        gbc.fill = GridBagConstraints.BOTH; // Permet à la zone de message de s'étendre verticalement
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        messageArea = new JTextArea(5, 20);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);
        topPanel.add(messagePanel, gbc);

        gbc.weighty = 0; // Réinitialise le poids vertical pour les boutons
        gbc.fill = GridBagConstraints.HORIZONTAL; // Les boutons ne s'étendent que horizontalement

        // Ajout des boutons dans un nouveau panel pour contrôler leur positionnement
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        sendButton = new JButton("Send Email");
        sendButton.setBackground(new Color(100, 149, 237)); // Couleur de fond
        sendButton.setForeground(Color.WHITE); // Couleur du texte
        buttonPanel.add(sendButton);

        // Add the attach button to the button panel
        attachButton = new JButton("Attach File");
        attachButton.setBackground(new Color(184, 181, 173)); // Couleur de fond
        attachButton.setForeground(Color.WHITE); // Couleur du texte
        buttonPanel.add(attachButton);

        gbc.gridwidth = GridBagConstraints.REMAINDER; // S'assure que le panel des boutons est ajouté à la fin
        topPanel.add(buttonPanel, gbc);

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

        readAndDisplayEmails(email, password); // Utilise les bonnes informations d'identification

        refreshButton = new JButton("Refresh Inbox");
        refreshButton.setBackground(new Color(72, 171, 117)); // Couleur de fond
        refreshButton.setForeground(Color.WHITE); // Couleur du texte
        buttonPanel.add(refreshButton);

        // Ajoutez un ActionListener au bouton de rafraîchissement
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    // Actualisez la boîte de réception
                    readAndDisplayEmails(email, password);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors de la lecture des e-mails.");
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

    }

    public void readAndDisplayEmails(String email, String password) throws IOException {
        try {
            List<EmailInfo> emailsList = Mailsendreceive.readInbox(email, password);

            SwingUtilities.invokeLater(() -> {
                emailListModel.clear();
                emailsList.forEach(emailInfo -> emailListModel.addElement(emailInfo.getSubject()));
                /*for (int i = emailsList.size() - 1; i >= 0; i--) {
                    EmailInfo emailInfo = emailsList.get(i);
                    emailListModel.addElement(emailInfo.toString());
                }*/
            });

            emailList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && emailList.getSelectedIndex() != -1) {
                    int index = emailList.getSelectedIndex();
                    if (index >= 0 && index < emailsList.size()) {
                        //EmailInfo selectedEmailInfo = emailsList.get(emailsList.size() - 1 - index);
                        EmailInfo selectedEmailInfo = emailsList.get(emailList.getSelectedIndex());
                        String from = selectedEmailInfo.getFrom();
                        String subject = selectedEmailInfo.getSubject();
                        String sentDate = selectedEmailInfo.getSentDate();
                        String content = selectedEmailInfo.getContent();

                        // Fetch attachments
                        List<String> attachments = selectedEmailInfo.getAttachments();

                        // Display email content including attachments
                        StringBuilder detailsHtml = new StringBuilder("<html><head>")
                                .append("<style type='text/css'>")
                                .append("body { font-family: 'Arial', sans-serif; background-color: #f8f9fa; color: #212529; margin: 10px; }")
                                .append("h2 { color: #193d63; margin-bottom: 0; }")
                                .append("h3 { color: #17a2b8; margin-top: 5px; }")
                                .append("h4 { color: #6c757d; margin-top: 2px; }")
                                .append("p { font-size: 14pt; line-height: 1.5; }")
                                .append("a { color: #007bff; }")
                                .append("</style></head><body>")
                                .append("<h2>").append(subject).append("</h2>")
                                .append("<h3>From: ").append(from).append("</h3>")
                                .append("<h4>Sent: ").append(sentDate).append("</h4>")
                                .append("<p>").append(content).append("</p>");

                        // Append attachment details as clickable links
                        if (!attachments.isEmpty()) {
                            detailsHtml.append("<h4>Attachments:</h4><ul>");
                            for (String attachment : attachments) {
                                // Assume you have a method to convert the file path to a URL or directly use file paths
                                String filePath = new File(attachment).getAbsolutePath().replace("\\", "/");
                                String htmlLink = "<a href='file:///" + filePath + "'>" + new File(attachment).getName() + "</a>";
                                detailsHtml.append("<li>").append(htmlLink).append("</li>");
                            }
                            detailsHtml.append("</ul>");
                        }

                        detailsHtml.append("</body></html>");

                        emailDetailsPane.setText(detailsHtml.toString());
                    }
                }
            });

            emailDetailsPane.addHyperlinkListener(eHyperlink -> {
                if (eHyperlink.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        String encryptedFolderPath = "pieces_jointes";
                        String decryptedFolderPath = "dechiffre";

                        // Convertir URL en chemin de fichier en tenant compte des espaces et des caractères spéciaux
                        String path = eHyperlink.getURL().toURI().getPath();
                        String decodedPath = URLDecoder.decode(path, "UTF-8");
                        File encryptedFile = new File(encryptedFolderPath, new File(decodedPath).getName());

                        // Vérifie si le fichier chiffré existe
                        if (!encryptedFile.exists()) {
                            JOptionPane.showMessageDialog(null, "Fichier chiffré non trouvé: " + encryptedFile.getName());
                            return;
                        }

                        // Logique de déchiffrement
                        byte[] decryptedData;
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(encryptedFile))) {
                            IBEcipher ibecipher = (IBEcipher) objectInputStream.readObject();
                            decryptedData = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibecipher);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erreur lors du déchiffrement: " + ex.getMessage());
                            return;
                        }

                        // Enregistrement du fichier déchiffré
                        File decryptedFolder = new File(decryptedFolderPath);
                        if (!decryptedFolder.exists()) decryptedFolder.mkdirs();
                        File decryptedFile = new File(decryptedFolder, encryptedFile.getName().replace("_encrypted", ""));
                        try (FileOutputStream out = new FileOutputStream(decryptedFile)) {
                            out.write(decryptedData);
                        }

                        // Ouverture du fichier déchiffré
                        Desktop.getDesktop().open(decryptedFile);

                    } catch (URISyntaxException | IOException ex) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture de la pièce jointe: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error displaying emails: " + e.getMessage());
        }
    }



    public void oldreadAndDisplayEmails(String email, String password) throws IOException {
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
                        StringBuilder detailsHtml = new StringBuilder("\"<html><head>\"\n" +
                                "            + \"<style type='text/css'>\"\n" +
                                "            + \"body { font-family: 'Arial', sans-serif; background-color: #f8f9fa; color: #212529; margin: 10px; }\"\n" +
                                "            + \"h2 { color: #007bff; margin-bottom: 0; }\"\n" +
                                "            + \"h3 { color: #17a2b8; margin-top: 5px; }\"\n" +
                                "            + \"h4 { color: #6c757d; margin-top: 5px; }\"\n" +
                                "            + \"p { font-size: 14pt; line-height: 1.5; }\"\n" +
                                "            + \"a { color: #007bff; text-decoration: none; }\"\n" +
                                "            + \"a:hover { text-decoration: underline; }\"\n" +
                                "            + \"</style></head><body>\";<h2>")
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

}