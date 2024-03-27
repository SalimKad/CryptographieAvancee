package projet_crypto.gui;

import projet_crypto.EmailInfo;
import projet_crypto.communication.Mailsendreceive;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OldInbox extends JFrame {
    private JButton sendEmailButton;

    private JList<String> emailList;
    private DefaultListModel<String> emailListModel;
    private JTextPane emailDetailsPane;

    private String email;
    private String password;

    public OldInbox(String email, String password) {
        this.email = email;
        this.password = password;

        setTitle("Inbox");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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

        // Configuration du bouton d'envoi d'email
        sendEmailButton = new JButton("Send Email");
        sendEmailButton.addActionListener(e -> redirectToEmailSenderGUI(email, password));

        // Crée un nouveau JPanel pour contenir le bouton et ajoute-le au SUD de la fenêtre principale
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendEmailButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void readAndDisplayEmails(String email, String password) {
        try {
            List<EmailInfo> emailsList = Mailsendreceive.readInbox(email, password);

            SwingUtilities.invokeLater(() -> {
                emailListModel.clear();
                // Parcourir la liste des emails en sens inverse pour ajouter les plus récents en haut
                for (int i = emailsList.size() - 1; i >= 0; i--) {
                    EmailInfo emailInfo = emailsList.get(i);
                    // Ajoute chaque email à la fin du modèle pour que les plus récents apparaissent en haut
                    emailListModel.addElement(emailInfo.toString());
                }
            });

            emailList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int index = emailList.getSelectedIndex();
                    if (index >= 0 && index < emailsList.size()) {
                        EmailInfo selectedEmailInfo = emailsList.get(emailsList.size() - 1 - index); // Inverser l'index
                        // Utilise les informations de l'objet EmailInfo sélectionné pour afficher les détails
                        String from = selectedEmailInfo.getFrom();
                        String subject = selectedEmailInfo.getSubject();
                        String sentDate = selectedEmailInfo.getSentDate();
                        String content = selectedEmailInfo.getContent();

                        String detailsHtml = String.format(
                                "<html><body><h2>%s</h2><h3>From: %s</h3><h4>Sent: %s</h4><p>%s</p></body></html>",
                                subject, from, sentDate, content);
                        emailDetailsPane.setText(detailsHtml);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectToEmailSenderGUI(String email, String password) {
        this.setVisible(false); // Cache la fenêtre Inbox actuelle
        // Créer une nouvelle instance de la classe EmailSenderGUI et l'afficher
        EmailSenderGUI emailSenderGUI = new EmailSenderGUI(email, password);
        emailSenderGUI.setVisible(true);
    }

    public static void main(String[] args) {
        // Retrieve email and password from the connexion interface
        connexion connexionInterfaces = new connexion();
        String email = connexionInterfaces.getEmailFieldText();
        String password = connexionInterfaces.getPasswordFieldText();

        SwingUtilities.invokeLater(() -> {
            OldInbox inbox = new OldInbox(email, password);
            inbox.setVisible(true);
        });
    }
}