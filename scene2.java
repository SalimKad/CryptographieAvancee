package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class scene2 extends Application {
	
	Button btnBack = new Button("Retour");
	Button btnExit = new Button("Quitter");
	Button btnRefresh = new Button("Actualiser");
	Button btnMail = new Button("Ecrire un nouveau mail");

	
    GridPane grille = new GridPane();
	scene1 scene1 = new scene1();
    scene3 scene3 = new scene3();
    
    ScrollPane scrollPane = new ScrollPane(grille);
    
    Scene scene2 = new Scene(scrollPane, 600, 400);
    BorderPane root = new BorderPane(scrollPane);

    private static String dernierObjet;
    private static String dernierEmetteur;



	
	public void start(Stage primaryStage) throws Exception {
		
		scrollPane.setFitToWidth(true);
	    scrollPane.setFitToHeight(true);
        grille.setStyle("-fx-background-color: lightBlue;");

        
		
		Text grandTitre = new Text("BOITE DE RECEPTION");
	    grandTitre.setFont(Font.font("Copperplate", 24));
		primaryStage.setScene(scene2);

		grille.add(grandTitre, 0, 0);
		grille.add(btnMail, 0, 1);
		grille.add(btnRefresh, 1, 1);
        grille.add(btnExit, 1,12);
        grille.add(btnBack, 0, 12);
        
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20));
        grille.setVgap(10);
        

            Button btnView = new Button("Voir");

            Label Objet = new Label("Objet " );
            Label Emetteur = new Label( "Emetteur" );

            Objet.setStyle("-fx-font-weight: bold;");
            Emetteur.setStyle("-fx-font-style: italic;");
            
            grille.add(Objet, 1, 2);
            grille.add(Emetteur, 0, 2);
            grille.add(btnView, 2, 2);
            
    		scene4 scene4=new scene4();
    		
    		btnView.setOnAction(new EventHandler<ActionEvent>() {
    			@Override
    			public void handle(ActionEvent arg0) {
                      
                      try {
						scene4.start(primaryStage);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		});

        
        Objet.setText("Objet: " + dernierObjet);
        Emetteur.setText("Emetteur: " + dernierEmetteur);
        
        

        
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: lightYellow;");
        sidebar.setMinWidth(100);
        Button starredLabel = new Button("Étoilés");
        Button spamg = new Button("Reçus");
        Button spamBr = new Button("Brouillons");
        Button sentLabel = new Button("Envoyés");
        Button spamLabel = new Button("Spams");
        Button spamCorb = new Button("Corbeille");

        spamLabel.setPrefWidth(100);
        starredLabel.setPrefWidth(100);
        spamBr.setPrefWidth(100);
        sentLabel.setPrefWidth(100);
        spamg.setPrefWidth(100);
        spamCorb.setPrefWidth(100);



        sidebar.getChildren().addAll( starredLabel, sentLabel,spamBr,spamg,spamLabel);
        root.setLeft(sidebar);
        primaryStage.setScene(new Scene(root, 600, 400));


        
		


		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				event.consume();
				scene1.fermerProgramme();
			}
		});
		
		
		
		

		
		btnBack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
                  
                  scene1.start(primaryStage);
			}
		});
		
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
                  scene1.fermerProgramme();							
			}
		});

        btnMail.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
						scene3 scene3=new scene3();
						try {
							scene3.start(primaryStage);
						} catch (Exception e) {
							e.printStackTrace();
						}
					

            }
        });
        

       

        

		primaryStage.setTitle("BOITE DE RECEPTION");
		primaryStage.show();
    }
	 // Méthode pour mettre à jour l'objet et l'émetteur du dernier mail reçu
    public static void mettreAJourDernierMail(String objet, String emetteur) {
        dernierObjet = objet;
        dernierEmetteur = emetteur;
    }

    // Méthode pour récupérer l'objet du dernier mail reçu
    public static String getDernierObjet() {
        return dernierObjet;
    }

    // Méthode pour récupérer l'émetteur du dernier mail reçu
    public static String getDernierEmetteur() {
        return dernierEmetteur;
    }

        


	
		
	}


