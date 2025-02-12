package frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;




/**
 * 
 * @description  An offline and open-source password manager
 * 
 * @author       David Williford
 * 
 * @flow:
			 *   If first start:
			 *   ---------------------------------------------------------------------------
			 *     1. User creates password
			 *     2. Database is created (table setup)
			 *     3. database is encrypted with hashed password
			 *     
			 *   If not first start
			 *   ---------------------------------------------------------------------------
			 *     1. User enters password
			 *     2. If correct, user is shown list of sources they can "show password" for
 *     
 */



public class Main extends Application{
	
	
	@Override
    public void start(Stage primaryStage) {
		
        // Password Input Button
		TextField textField = new TextField();
        textField.setPromptText("Password");
        
        // Output
        Label outputLabel = new Label();
        
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String inputText = textField.getText();
            outputLabel.setText("You entered: " + inputText);
        });

        // Layout
        VBox layout = new VBox(10, textField, submitButton, outputLabel);
        Scene scene = new Scene(layout, 300, 200);

        // Stage Settings
        primaryStage.setTitle("JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
