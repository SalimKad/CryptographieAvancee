package projet_crypto.gui;

import projet_crypto.communication.Client;
import projet_crypto.communication.ServerResponse;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;


public class connexion extends JFrame implements Serializable {

    private JTextField emailField;
    private JPasswordField passwordField;

    public connexion() {
        setTitle("Login");
        setSize(350, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
        /*System.out.println("Email: " + email);
        System.out.println("Password: " + password);*/

        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imap");
            properties.put("mail.imap.host", "outlook.office365.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imap.ssl.trust", "outlook.office365.com");

            Session session = Session.getDefaultInstance(properties);

            Store store = session.getStore();
            store.connect(email, password);

            // Si cette ligne est atteinte, la connexion a réussi
            store.close(); // Fermeture immédiate de la connexion*/

            // Continuez avec l'action de succès (comme avant)
            ServerResponse objectsResponse = projet_crypto.communication.Client.sendInitialRequest(email);
            redirectToComposeAndView(email, password, objectsResponse);

         }catch (NoSuchProviderException ex) {
            JOptionPane.showMessageDialog(this, "Provider error. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            //sex.printStackTrace();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private void redirectToComposeAndView(String email, String password, ServerResponse objectResponse) throws IOException {
        //System.out.println("Redirected to compose and view screen with email: " + email + " and password: " + password);
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
