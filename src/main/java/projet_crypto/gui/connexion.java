package projet_crypto.gui;

import projet_crypto.communication.Client;
import projet_crypto.communication.ServerResponse;

import javax.mail.AuthenticationFailedException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class connexion extends JFrame implements Serializable{

    private JTextField emailField;
    private JPasswordField passwordField;
    private Map<String, String> userCredentials;

   // ServerResponse objectsResponse = new ServerResponse();
    public connexion() {
        setTitle("Login");
        setSize(350, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        /*userCredentials = new HashMap<>();
        userCredentials.put("idkuser2002@outlook.com", "password2002"); // Example credentials
        userCredentials.put("maryas2002@outlook.com", "password2002"); // Example credentials
        userCredentials.put("cryptoprojet4A@outlook.com", "4nbG4zeT5q66JV"); // Example credentials*/
        //userCredentials.put("cryptoprojet4A@outlook.com", "haha"); // Example credentials

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajout de marges

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Ajout d'espacement entre les éléments

        loginPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        loginPanel.add(emailField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::loginAction);
        loginButton.setBackground(new Color(100, 149, 237)); // Couleur de fond
        loginButton.setForeground(Color.WHITE); // Couleur du texte
        loginPanel.add(new JLabel()); // Pour aligner le bouton à droite
        loginPanel.add(loginButton);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loginAction(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        try {
            // Tentative de connexion à Outlook pour vérifier les identifiants
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
                    return new PasswordAuthentication(email, password);
                }
            });

            /*Session emailSession = Session.getInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(email, password);

            // Si cette ligne est atteinte, la connexion a réussi
            store.close(); // Fermeture immédiate de la connexion*/

            // Continuez avec l'action de succès (comme avant)
            ServerResponse objectsResponse = projet_crypto.communication.Client.sendInitialRequest(email);
            redirectToComposeAndView(email, password, objectsResponse);
        /* }catch (AuthenticationFailedException ex) {
            JOptionPane.showMessageDialog(this, "Email ou mot de passe invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.out.println("Authentication failed: " + ex.getMessage());*/
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    /*private void oldloginAction(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (validateLogin(email, password)) {
            try {
                ServerResponse objectsResponse = projet_crypto.communication.Client.sendInitialRequest(email);
                redirectToComposeAndView(email, password, objectsResponse);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de connexion: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email ou mot de passe invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }*/


    private boolean validateLogin(String email, String password) {
        return userCredentials.containsKey(email) && userCredentials.get(email).equals(password);
    }

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
