package projet_crypto.gui;

import projet_crypto.communication.ServerResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import projet_crypto.gui.inbox2;

public class connexion extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private Map<String, String> userCredentials;

    public connexion() {
        setTitle("Connexion");
        setSize(350, 180); // Légèrement plus grand pour une meilleure mise en page
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userCredentials = new HashMap<>();
        userCredentials.put("cryptoprojet4A@outlook.com", "4nbG4zeT5q66JV");
        userCredentials.put("maryas2002@outlook.com", "password2002");

        initUI();
    }

    private void initUI() {
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
    }

    private boolean validateLogin(String email, String password) {
        return userCredentials.containsKey(email) && userCredentials.get(email).equals(password);
    }

    private void redirectToComposeAndView(String email, String password, ServerResponse objectResponse) throws IOException {
        System.out.println("Redirigé vers l'écran de composition et de visualisation avec email : " + email + " et mot de passe : " + password);
        dispose();
        inbox2 inbox = new inbox2(email, password, objectResponse);
        inbox.setVisible(true);
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
        setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 14));
        SwingUtilities.invokeLater(() -> new connexion().setVisible(true));
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}
