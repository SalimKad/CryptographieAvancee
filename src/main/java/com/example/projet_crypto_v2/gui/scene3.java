package com.example.projet_crypto_v2.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static com.example.projet_crypto_v2.Mailsendreceivetest.sendmessage;

public class scene3 extends Application {

    //creation de l'objet conteneur
    GridPane grille = new GridPane();
    //creation de la scene
    Scene scene = new Scene(grille,600,400);
    //composants
    Button btn0 = new Button("Retour");
    Button btn1 = new Button("Envoyer");
    Button btn2 = new Button("Ajouter une pièce jointe");
    Button btn3 = new Button("Quitter");

    Text lbldst=new Text("destinataire:");
    Text lblobj=new Text("Objet:");
    Text Mailo=new Text("Contenu du Mail:");
    TextField nom = new TextField();
    TextField Objet = new TextField();
    TextField Mail = new TextField();



    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(scene);
        primaryStage.setTitle("Mail Box");
        primaryStage.show();
        grille.setStyle("-fx-background-color: lightBlue;");
        //ajout des composants
        grille.add(lbldst, 0, 0);
        grille.add(nom, 1, 0);
        grille.add(lblobj, 0, 1);
        grille.add(Objet, 1, 1);
        grille.add(Mailo, 0, 2);
        grille.add(Mail, 1, 2);
        Mail.setPrefWidth(500);
        Mail.setPrefHeight(500);

        grille.add(btn0, 0, 4);
        grille.add(btn1, 1, 5);
        grille.add(btn3, 1, 4);

        grille.add(btn2, 0, 3);
        grille.setHgap(10);
        grille.setVgap(10);
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));

        //evenement sur le bouton d'envoie de mail
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                //envoie de mail en appelant la methode sendMessage
                sendmessage("cryptoprojet4A@outlook.com","4nbG4zeT5q66JV","cryptoprojet4A@outlook.com");
            }
        });
        btn0.setOnAction(new EventHandler<ActionEvent>() {

            scene1 scene1=new scene1();
            public void handle(ActionEvent arg0) {

                try {
                    scene1.start(primaryStage);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btn2.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une pièce jointe");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers", "*.*")
            );
            // Afficher la fenêtre de sélection de fichier
            java.io.File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                btn2.setText(selectedFile.getAbsolutePath());
            }
        });

        btn3.setOnAction(new EventHandler<ActionEvent>() {
            scene1 scene1 =new scene1();
            @Override
            public void handle(ActionEvent arg0) {
                // scene1.fermerProgramme();
            }
        });



    }
}
