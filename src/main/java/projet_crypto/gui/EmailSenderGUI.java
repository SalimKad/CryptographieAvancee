package com.example.projet_crypto_v2.gui;


import javax.swing.*;

import com.example.projet_crypto_v2.IBEBasicIdent;
import com.example.projet_crypto_v2.IBEcipher;
import com.example.projet_crypto_v2.KeyPair;
import chiffrement.SettingParameters;

import com.example.projet_crypto_v2.communication.Mailsendreceivetest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class EmailSenderGUI extends JFrame {

    private JTextField senderEmailField;
    private JPasswordField passwordField;
    private JTextField recipientEmailField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JButton attachButton;
    private JButton sendButton;
    private String selectedFilePath;
    private JButton downloadButton;
    Pairing pairing = PairingFactory.getPairing("a.properties");
    SettingParameters sp = IBEBasicIdent.setup(pairing);
    
    private String senderEmail;
    private String password;
  
    public EmailSenderGUI(String senderEmail, String password) {
    	
    	this.senderEmail = senderEmail;
        this.password = password;
        
        setTitle("Email Sender");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        selectedFilePath = null;

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

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
                
                String recipientEmail = recipientEmailField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();
                String attachmentName = selectedFilePath != null ? new File(selectedFilePath).getName() : "Attachment";

                // Encrypt the attachment
                try {
                KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), recipientEmail);

                    // Read attachment file
                    File attachmentFile = new File(selectedFilePath);
                    FileInputStream attachmentStream = new FileInputStream(attachmentFile);
                    byte[] attachmentBytes = new byte[(int) attachmentFile.length()];
                    attachmentStream.read(attachmentBytes);
                   
                    String message2 = new String(attachmentBytes);
                   // attachmentStream.close();
                    

                    // Encrypt attachment
                    IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, sp.getP(), sp.getP_pub(), attachmentBytes, keys.getPk());
                    byte[] encryptedAttachment = ibecipher.getAescipher();
                    byte[] V = ibecipher.getV();
                    byte[] U = ibecipher.getU();
                   
                    // Write the encryption instance
                    File f = new File("Encrypted instance" + selectedFilePath.substring(selectedFilePath.lastIndexOf(".")));
                    f.createNewFile();
                    FileOutputStream fout = new FileOutputStream(f);
                    ObjectOutputStream objectOut = new ObjectOutputStream(fout);
                    objectOut.writeObject(ibecipher);
                    objectOut.close();
                    fout.close();
                   

                    System.out.println("To access the resulting file, check the following path: " + f.getAbsolutePath());
                   

                    // Send email with encrypted attachment
                    Mailsendreceivetest.sendmessagewithattachement2(senderEmail, password, recipientEmail, f.getAbsolutePath(),subject,message);
                   

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
       
     // Création du bouton de téléchargement
        downloadButton = new JButton("Download Attachments");
        inputPanel.add(downloadButton);

        // ActionListener pour le bouton de téléchargement
     // ActionListener pour le bouton de téléchargement
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer le nom du fichier de l'objet IBEcipher généré lors de l'envoi de l'email chiffré
                String attachmentFileName = "Encrypted instance" + selectedFilePath.substring(selectedFilePath.lastIndexOf("."));
                String recipientEmail = recipientEmailField.getText();

                try {
               
                KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), recipientEmail);
                    // Lecture de l'objet IBEcipher depuis le fichier
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(attachmentFileName));
                    IBEcipher ibeCipher = (IBEcipher) objectInputStream.readObject();
                    objectInputStream.close();

                    // Déchiffrement de la pièce jointe
                    byte[] decryptedAttachment = IBEBasicIdent.IBEdecryption(pairing, sp.getP(), sp.getP_pub(), keys.getSk(), ibeCipher);
                   
                    // Récupérer l'extension du fichier chiffré d'origine
                    String extension = selectedFilePath.substring(selectedFilePath.lastIndexOf("."));

                    // Écriture du fichier déchiffré
                    File decryptedAttachmentFile = new File("DecryptedAttachment" + extension);
                    FileOutputStream fileOutputStream = new FileOutputStream(decryptedAttachmentFile);
                    fileOutputStream.write(decryptedAttachment);
                    fileOutputStream.close();

                    JOptionPane.showMessageDialog(null, "Attachment downloaded and decrypted successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error downloading or decrypting attachment.");
                }
            }
        });
        
        

    }

    public static void main(String[] args) {
        // Retrieve email and password from the connexion interface
        connexion connexionInterface = new connexion();
        String email = connexionInterface.getEmailFieldText();
        String password = connexionInterface.getPasswordFieldText();

        // Create and display the EmailSenderGUI interface with the retrieved email and password
        EmailSenderGUI emailSenderGUI = new EmailSenderGUI(email, password);
        emailSenderGUI.setVisible(true);
    }

    }

