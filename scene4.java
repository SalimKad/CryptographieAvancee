package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class scene4 extends Application{
	
	Button btnBack = new Button("Retour");
	Button btnExit = new Button("Quitter");
	Label Objet = new Label("Objet " );
    Label Emetteur = new Label( "Emetteur");
    Label Contenu = new Label( "contenu du mail");

    



	@Override
	public void start(Stage primaryStage) throws Exception {
	     GridPane grille = new GridPane();
	     Scene scene = new Scene(grille,600,400);
	     
	     Text grandTitre = new Text("Mail");
		 grandTitre.setFont(Font.font("Copperplate", 24));
		 grille.add(grandTitre, 0, 0);
		 grille.add(Objet, 1, 0);
		 grille.add(Emetteur, 1, 1);
		 grille.add(Contenu, 2, 1);

		 grille.add(btnBack, 0, 3);
		 grille.add(btnExit, 1, 3);
		 
		 grille.setHgap(10);
	       grille.setVgap(10);
	        grille.setAlignment(Pos.CENTER);


		primaryStage.setScene(scene);
		primaryStage.setTitle("Mail Box");
		primaryStage.show();
		
		btnBack.setOnAction(new EventHandler<ActionEvent>() {
			scene2 scene2=new scene2();
			@Override
			public void handle(ActionEvent arg0) {
                  
                  try {
					scene2.start(primaryStage);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
        btnExit.setOnAction(new EventHandler<ActionEvent>() {

        	
			scene1 scene1=new scene1();

			@Override
			public void handle(ActionEvent arg0) {
                  scene1.fermerProgramme();							
			}
		});


       

	}
	
	

}
