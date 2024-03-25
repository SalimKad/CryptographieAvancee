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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;

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
    private String selectedFilePath;

    private JPanel attachmentsPanel;

    Pairing pairing = PairingFactory.getPairing("a.properties");
    //SettingParameters sp = IBEBasicIdent.setup(pairing);

    private ServerResponse objectResponse;


    public inbox2(String senderEmail, String password, ServerResponse objectResponse) throws IOException {
        this.email = senderEmail;
        this.password = password;
        this.objectResponse = objectResponse;


        setTitle("Inbox");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Create panel for recipient, subject, message, send button, and attach button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout()); // Utilisation de GridBagLayout pour un placement plus flexible
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2); // Marge autour des composants

        // Champ Email destinataire
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recipientPanel.add(new JLabel("Recipient Email:"));
        recipientEmailField = new JTextField(20);
        recipientPanel.add(recipientEmailField);
        topPanel.add(recipientPanel, gbc);

        // Champ sujet
        JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subjectPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField(20);
        subjectPanel.add(subjectField);
        topPanel.add(subjectPanel, gbc);

        // Zone de message
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        messageArea = new JTextArea(5, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);
        topPanel.add(messagePanel, gbc);

        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sendButton = new JButton("Send Email");
        attachButton = new JButton("Attach File");
        buttonPanel.add(sendButton);
        buttonPanel.add(attachButton);
        topPanel.add(buttonPanel, gbc);

        // Amélioration visuelle des boutons et champs
        sendButton.setBackground(new Color(100, 149, 237)); // Couleur de fond
        sendButton.setForeground(Color.WHITE); // Couleur du texte
        attachButton.setBackground(new Color(105, 105, 105));
        attachButton.setForeground(Color.WHITE);

        // Add top panel and email display panel to the frame
        add(topPanel, BorderLayout.NORTH);

        // Configuration de emailList et emailDetailsPane
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

        readAndDisplayEmails3(email, password); // Utilise les bonnes informations d'identification


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String recipientEmail = recipientEmailField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();
                String attachmentName = selectedFilePath != null ? new File(selectedFilePath).getName() : "Attachment";

                // Encrypt the attachment
                try {
                    //KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), recipientEmail);

                    // Read attachment file
                    File attachmentFile = new File(selectedFilePath);
                    FileInputStream attachmentStream = new FileInputStream(attachmentFile);
                    byte[] attachmentBytes = new byte[(int) attachmentFile.length()];
                    attachmentStream.read(attachmentBytes);

                    String message2 = new String(attachmentBytes);
                    // attachmentStream.close();


                    // Encrypt attachment
                    IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), attachmentBytes, objectResponse.getPk());
                    byte[] encryptedAttachment = ibecipher.getAescipher();


                    // Chemin du dossier piece_jointes
                    String attachmentsDirPath = "pieces_jointes";
                    File attachmentsDir = new File(attachmentsDirPath);
                    if (!attachmentsDir.exists()) {
                        attachmentsDir.mkdirs(); // Crée le dossier s'il n'existe pas
                    }

                    // Chemin pour le fichier crypté
                    String encryptedFileName = attachmentsDirPath + File.separator + attachmentName + "_encrypted" + selectedFilePath.substring(selectedFilePath.lastIndexOf("."));
                    File f = new File(encryptedFileName);
                    f.createNewFile();
                    FileOutputStream fout = new FileOutputStream(f);
                    ObjectOutputStream objectOut = new ObjectOutputStream(fout);
                    objectOut.writeObject(ibecipher);
                    objectOut.close();
                    fout.close();

                    System.out.println("To access the resulting file, check the following path: " + f.getAbsolutePath());


                    // Send email with encrypted attachment
                    //Mailsendreceive.sendmessagewithattachement2(senderEmail, password, recipientEmail, attachmentFile.getAbsolutePath(), subject, message);
                    Mailsendreceive.sendmessagewithattachement2(senderEmail, password, recipientEmail, f.getAbsolutePath(), subject, message);


                } catch (Exception ex) {
                    ex.printStackTrace();
                    // Handle any exceptions
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
        attachmentsPanel = new JPanel();
        attachmentsPanel.setLayout(new FlowLayout()); // Ou un autre layout qui vous convient
        add(attachmentsPanel, BorderLayout.SOUTH);
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
                                System.out.println("Attachment: " + attachment);
                                String fileName = "Télécharger " + new File(attachment).getName();
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
                                System.out.println("URL du fichier à télécharger: " + url);
                                if (url != null) { // Vérifiez si l'URL n'est pas nulle
                                    try {
                                        // Supposons que l'URL contient un chemin vers le fichier crypté spécifique
                                        File encryptedFile = new File(url.toURI());

                                        // Déchiffrement de la pièce jointe sélectionnée uniquement
                                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(encryptedFile));
                                        IBEcipher ibeCipher = (IBEcipher) objectInputStream.readObject();
                                        objectInputStream.close();
                                        byte[] decryptedAttachment = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibeCipher);

                                        // Sauvegardez la pièce jointe déchiffrée dans un fichier
                                        String originalFileName = encryptedFile.getName().replace("_encrypted", ""); // Enlever le suffixe "_encrypted"
                                        File decryptedFile = new File(encryptedFile.getParent(), originalFileName);
                                        FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
                                        fileOutputStream.write(decryptedAttachment);
                                        fileOutputStream.close();

                                        // Ouvrir le fichier déchiffré
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(decryptedFile);
                                        } else {
                                            System.err.println("Desktop is not supported on this platform.");
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Erreur lors du déchiffrement ou de l'ouverture de la pièce jointe.");
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
                            attachmentsPanel.removeAll(); // Nettoyez le panneau avant d'ajouter de nouveaux éléments
                            for (String attachmentPath : selectedEmailInfo.getAttachments()) {
                                JLabel attachmentLink = new JLabel(attachmentPath); // Utilisez un format plus convivial si possible
                                attachmentLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                attachmentLink.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        // Logique pour télécharger et déchiffrer la pièce jointe ici
                                        decryptAndOpenAttachment(attachmentPath); // Méthode à implémenter
                                    }
                                });
                                attachmentsPanel.add(attachmentLink);
                            }
                            attachmentsPanel.revalidate();
                            attachmentsPanel.repaint();
                        }
                        detailsHtml.append("</ul>");

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


    private void decryptAndOpenAttachment(String encryptedFilePath) {
        try {
            // Supposons que vous avez une façon de récupérer votre clé privée et d'autres informations nécessaires pour le déchiffrement
            File encryptedFile = new File(encryptedFilePath);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(encryptedFile));
            IBEcipher ibeCipher = (IBEcipher) ois.readObject();
            ois.close();

            // Déchiffrez la pièce jointe (ajustez avec votre logique réelle de déchiffrement)
            byte[] decryptedData = IBEBasicIdent.IBEdecryption(pairing, objectResponse.getP(), objectResponse.getP_pub(), objectResponse.getSk(), ibeCipher);

            // Sauvegardez le fichier déchiffré
            String decryptedFilePath = encryptedFilePath.replace("_encrypted", "");
            try (FileOutputStream fos = new FileOutputStream(decryptedFilePath)) {
                fos.write(decryptedData);
            }

            // Ouvrez le fichier déchiffré
            Desktop.getDesktop().open(new File(decryptedFilePath));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du déchiffrement ou de l'ouverture du fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        // Retrieve email and password from the connexion interface
        connexion connexionInterfaces = new connexion();
        //String email = connexionInterfaces.getEmailFieldText();
        //String password = connexionInterfaces.getPasswordFieldText();

    }
}