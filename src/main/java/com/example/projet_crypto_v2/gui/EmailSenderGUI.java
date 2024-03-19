package com.example.projet_crypto_v2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class EmailSenderGUI extends JFrame {

    private JTextField senderEmailField;
    private JPasswordField passwordField;
    private JTextField recipientEmailField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JButton attachButton;
    private JButton sendButton;
    private String selectedFilePath;

    public EmailSenderGUI() {
        setTitle("Email Sender");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        selectedFilePath = null;

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Sender Email:"));
        senderEmailField = new JTextField();
        inputPanel.add(senderEmailField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        inputPanel.add(new JLabel("Recipient Email:"));
        recipientEmailField = new JTextField();
        inputPanel.add(recipientEmailField);

        inputPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        inputPanel.add(new JLabel("Message:"));
        messageArea = new JTextArea();
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        inputPanel.add(messageScrollPane);

        attachButton = new JButton("Attach File");
        inputPanel.add(attachButton);

        sendButton = new JButton("Send Email");
        inputPanel.add(sendButton);

        add(inputPanel, BorderLayout.CENTER);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String senderEmail = senderEmailField.getText();
                String password = new String(passwordField.getPassword());
                String recipientEmail = recipientEmailField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();
               // String path="Photo_Chat_Noir_et_blanc.jpg";
                String attachmentName = selectedFilePath != null ? new File(selectedFilePath).getName() : "Attachment"; // Obtenez le nom du fichier à partir du chemin ou utilisez un nom par défaut
                // Code to send email using your existing method
               // Mailsendreceivetest.sendmessage(senderEmail, password, recipientEmail);
               // Mailsendreceivetest.sendmessagewithattachement(senderEmail, password, recipientEmail,path);
               // Mailsendreceivetest.sendmessage2(senderEmail, password, recipientEmail,subject,message);
                //Mailsendreceivetest.sendmessagewithattachement2(senderEmail, password, recipientEmail,path,subject,message);
                com.example.projet_crypto_v2.communication.Mailsendreceivetest.sendmessagewithattachement3(senderEmail, password, recipientEmail, selectedFilePath, subject, message, attachmentName);
            
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmailSenderGUI().setVisible(true);
            }
        });
    }
}