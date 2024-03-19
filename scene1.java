package application;
	
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


 
public class scene1 extends Application  {
	

	 //creation de l'objet conteneur
     GridPane grille = new GridPane();
   //creation de la scene
     Scene scene = new Scene(grille,600,400);
	//composants
	Button btn0 = new Button("Se connecter");
	Button btn1 = new Button("Quitter");
    Text lblNom=new Text("Adresse électronique:");
    Text lblmdp=new Text("Mot de passe:");
	TextField nom = new TextField();

	PasswordField mdp = new PasswordField();
	RadioButton rdAdmin=new RadioButton("membre ?");
	RadioButton rdClient=new RadioButton();
	ToggleGroup groupRadioButton=new ToggleGroup();
	

	@Override
	public void start(Stage primaryStage) {
		
		    rdClient.setText("pas encore un membre ?");
		    rdAdmin.setToggleGroup(groupRadioButton);
		    rdClient.setToggleGroup(groupRadioButton);
		    rdAdmin.setSelected(true);
		    rdAdmin.requestFocus();
		    
		    nom.setPromptText("Saisissez votre adresse électronique");
		    mdp.setPromptText("Saisissez votre mot de passe");
		    
		    

		    


		    Text grandTitre = new Text("MAILITOOO");
		    grandTitre.setFont(Font.font("Copperplate", 24));
		    grille.add(grandTitre, 0, 0, 2, 1); 
            
            primaryStage.setScene(scene);
			primaryStage.setTitle("Mail Box");
			primaryStage.show();
	        grille.setStyle("-fx-background-color: lightblue;");
	        //ajout des composants
            grille.add(lblNom, 0, 1);
            grille.add(nom, 1, 1);
            grille.add(lblmdp, 0, 2);
            grille.add(mdp, 1, 2);
            grille.add(btn0, 2, 4);
            grille.add(btn1, 0, 4);
            //grille.addRow(3, rdAdmin,rdClient);
            grille.setHgap(20);
            grille.setVgap(20);
            grille.setAlignment(Pos.CENTER);
            grille.setPadding(new Insets(20));
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					event.consume();
					fermerProgramme();
					
				}
			});


	        
			//Evenements
			btn0.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					if(nom.getText().equals("0")&& mdp.getText().equals("0")
							&& rdAdmin.isSelected()) {
						scene2 scene2=new scene2();
						try {
							scene2.start(primaryStage);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
						else
						{
							Alert erreur=new Alert(Alert.AlertType.ERROR);
							erreur.setTitle("Authentification échoué");
							erreur.setContentText("Les informations sont erronées");
							erreur.setHeaderText(null);
							erreur.show();
						}}			
			});
			
			//bouton pour quitter 
			btn1.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					fermerProgramme();
					
				}
			});

		
		
	}
	
	public void fermerProgramme() {
		Alert confirmer=new Alert(AlertType.CONFIRMATION);
		confirmer.setTitle("Confirmation");
		confirmer.setContentText("Voulez vous vraiment quitter");
		confirmer.setHeaderText(null);
		Optional<ButtonType> resultat=confirmer.showAndWait();
		if(resultat.get()==ButtonType.OK)
			System.exit(0);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	
}
