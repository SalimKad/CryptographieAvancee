package gui;

import javax.swing.*;

import communication.message;
import communication.MessageStorage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MessageComposerGUI extends JFrame {

    private JTextField recipientField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JButton attachButton;
    private JButton sendButton;
    private JTextArea receivedMessagesArea;
    private static String loggedInEmail = connexion.getEmailFieldText();
    private static String loggedInPassword = connexion.getPasswordFieldText();
    private String selectedFilePath;

    public MessageComposerGUI(String loggedInEmail, String loggedInPassword) {
        this.loggedInEmail = loggedInEmail;
        this.loggedInPassword = loggedInPassword;

        setTitle("Compose and View Messages");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel composePanel = new JPanel();
        composePanel.setLayout(new GridLayout(5, 2));

        composePanel.add(new JLabel("Recipient:"));
        recipientField = new JTextField();
        composePanel.add(recipientField);

        composePanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        composePanel.add(subjectField);

        composePanel.add(new JLabel("Message:"));
        messageArea = new JTextArea();
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        composePanel.add(messageScrollPane);

        attachButton = new JButton("Attach File");
        composePanel.add(attachButton);

        sendButton = new JButton("Send Email");
        composePanel.add(sendButton);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        receivedMessagesArea = new JTextArea();
        receivedMessagesArea.setEditable(false);
        JScrollPane receivedMessagesScrollPane = new JScrollPane(receivedMessagesArea);
        controlPanel.add(receivedMessagesScrollPane, BorderLayout.CENTER);

        add(composePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipientEmail = recipientField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();
                String attachmentName = selectedFilePath != null ? new File(selectedFilePath).getName() : "Attachment";

                // Envoi du message en utilisant l'email et le mot de passe récupérés
                communication.Mailsendreceivetest.sendmessagewithattachement3(loggedInEmail, loggedInPassword, recipientEmail, selectedFilePath, subject, message, attachmentName);

                // Informer l'utilisateur que le message a été envoyé
                JOptionPane.showMessageDialog(MessageComposerGUI.this, "Message sent successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Mise à jour de la zone de réception des messages
                updateReceivedMessagesArea();
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

        // Initial update of received messages area
        updateReceivedMessagesArea();
    }

    private void updateReceivedMessagesArea() {
        List<message> receivedMessages = MessageStorage.getInstance().getMessagesForRecipient(loggedInEmail);
        StringBuilder messageText = new StringBuilder();
        for (message message : receivedMessages) {
            messageText.append("From: ").append(message.getSenderEmail()).append("\n");
            messageText.append("Subject: ").append(message.getSubject()).append("\n");
            messageText.append("Message: ").append(message.getMessage()).append("\n\n");
        }
        receivedMessagesArea.setText(messageText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                MessageComposerGUI(loggedInEmail, loggedInPassword).setVisible(true);
            }
        });
    }


}
