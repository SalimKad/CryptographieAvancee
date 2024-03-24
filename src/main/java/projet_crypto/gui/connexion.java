package projet_crypto.gui;

import javax.swing.*;

import projet_crypto.communication.ServerResponse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class connexion extends JFrame implements Serializable{

    private JTextField emailField;
    private JPasswordField passwordField;
    private Map<String, String> userCredentials;

   // ServerResponse objectsResponse = new ServerResponse();
    public connexion() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userCredentials = new HashMap<>();
        userCredentials.put("cryptoprojet4A@outlook.com", "4nbG4zeT5q66JV"); // Example credentials
        userCredentials.put("maryas2002@outlook.com", "password2002"); // Example credentials

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));

        loginPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        loginPanel.add(emailField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(email, password)) {
                    try {
                    	ServerResponse objectsResponse = projet_crypto.communication.Client.sendInitialRequest(email); 
                    	
						redirectToComposeAndView(email, password, objectsResponse);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // Pass the password
                } else {
                    JOptionPane.showMessageDialog(connexion.this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean validateLogin(String email, String password) {
        return userCredentials.containsKey(email) && userCredentials.get(email).equals(password);
    }

    /*private void redirectToComposeAndView(String email, String password) {
        dispose();
        EmailSenderGUI emailsenderGUI = new EmailSenderGUI(email, password); // Pass the password
        emailsenderGUI.setVisible(true);
    }*/
    private void redirectToComposeAndView(String email, String password, ServerResponse objectResponse) throws IOException {
    	System.out.println("Redirected to compose and view screen with email: " + email + " and password: " + password);
        System.out.println("Received server response: " + objectResponse);
        //System.out.println("le sk du client : ",objectsResponse.getSk());
    	dispose();
        inbox2 inbox = new inbox2(email, password, objectResponse); // Créer une instance de la classe Inbox
        inbox.setVisible(true); // Afficher la fenêtre Inbox
    }

    // Méthode pour récupérer l'email depuis l'interface
    public String getEmailFieldText() {
        return emailField.getText(); // Supposons que emailField est votre champ de texte pour l'email
    }

    // Méthode pour récupérer le mot de passe depuis l'interface
    public String getPasswordFieldText() {
        return new String(passwordField.getPassword()); // Supposons que passwordField est votre champ de texte pour le mot de passe
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new connexion().setVisible(true);
            }
        });
    }
}
