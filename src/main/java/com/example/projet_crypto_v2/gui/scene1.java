package com.example.projet_crypto_v2.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import static com.example.projet_crypto_v2.OutlookConnexion.verifyOutlookCredentials;

public class scene1 extends Application {

    private Map<String, String> userCredentials = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des identifiants d'utilisateur pour la démonstration
        userCredentials.put("cryptoprojet4A@outlook.com", "4nbG4zeT5q66JV");
        userCredentials.put("user2@example.com", "password2");

        GridPane grille = new GridPane();
        Scene scene = new Scene(grille, 600, 400);

        Button btnLogin = new Button("Se connecter");
        Button btnQuit = new Button("Quitter");
        Text lblEmail = new Text("Adresse électronique:");
        Text lblPassword = new Text("Mot de passe:");
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        RadioButton rdAdmin = new RadioButton("membre ?");
        RadioButton rdClient = new RadioButton("pas encore un membre ?");
        ToggleGroup groupRadioButton = new ToggleGroup();

        rdAdmin.setToggleGroup(groupRadioButton);
        rdClient.setToggleGroup(groupRadioButton);
        rdAdmin.setSelected(true);

        emailField.setPromptText("Saisissez votre adresse électronique");
        passwordField.setPromptText("Saisissez votre mot de passe");

        Text grandTitre = new Text("MAILITOOO");
        grandTitre.setFont(Font.font("Copperplate", 24));
        grille.add(grandTitre, 0, 0, 2, 1);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Mail Box");
        grille.setStyle("-fx-background-color: lightblue;");
        grille.add(lblEmail, 0, 1);
        grille.add(emailField, 1, 1);
        grille.add(lblPassword, 0, 2);
        grille.add(passwordField, 1, 2);
        grille.add(btnLogin, 2, 4);
        grille.add(btnQuit, 0, 4);
        grille.setHgap(20);
        grille.setVgap(20);
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));

        btnLogin.setOnAction(e -> {
            if (validateLogin(emailField.getText(), passwordField.getText())) {
                if (verifyOutlookCredentials(emailField.getText(), passwordField.getText())) {
                    // Si l'authentification réussit, rediriger l'utilisateur ou afficher succès.
                    System.out.println("Authentification réussie avec Outlook !");
                    //redirection vers une autre page
                    primaryStage.close();
                    scene3 scene3 = new scene3();
                    try {
                        scene3.start(primaryStage);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur d'authentification Outlook");
                    alert.setContentText("Impossible de se connecter à Outlook avec ces identifiants.");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            } else {
                Alert erreur = new Alert(Alert.AlertType.ERROR);
                erreur.setTitle("Authentification échouée");
                erreur.setContentText("Les informations sont erronées");
                erreur.setHeaderText(null);
                erreur.show();
            }
        });

        btnQuit.setOnAction(e -> primaryStage.close());

        primaryStage.show();
    }

    private boolean validateLogin(String email, String password) {
        return userCredentials.containsKey(email) && userCredentials.get(email).equals(password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
