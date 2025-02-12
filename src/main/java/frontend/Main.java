package frontend;

import java.util.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.CryptoService;
import services.DatabaseService;
import services.MainService;
import services.UserService;

import models.PasswordRecord;
import models.User;




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



public class Main extends Application {

	
    @Override
    public void start(Stage primaryStage) {
    	
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 300, 200);
        
        primaryStage.setTitle("JavaFX App");
        
        /**
         * Main user object used in application
         */
        User user = null;

        if (MainService.isFirstStart()) {
            setupFirstStartUI(layout, user);
        } else {
            setupAuthenticationUI(layout, user);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
    /**
     * @description  Sets up first start UI
     * 
     * @param        layout
     */
    private void setupFirstStartUI(VBox layout, User user) {
        TextField textField = new TextField();
        textField.setPromptText("Create a New Password");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> handleFirstStart(textField.getText(), layout, user));

        layout.getChildren().addAll(new Label("Set Up Your Password"), textField, submitButton);
    }

    
    
    /**
     * @description  Handle the first start bootstrap
     * 
     * @param        password
     */
    private void handleFirstStart(String password, VBox layout, User user) {
        MainService.bootstrap(CryptoService.hash(password));
        user = new User(password);
        user.save();
        mainDashboardUI(layout, user);
    }

    
    
    /**
     * @description  Authentication Form UI
     * 
     * @param        layout
     */
    private void setupAuthenticationUI(VBox layout, User user) {
        TextField textField = new TextField();
        textField.setPromptText("Password");
        
        Button submitButton = new Button("Submit");
        Label outputLabel = new Label();

        submitButton.setOnAction(e -> handleAuthentication(textField.getText(), outputLabel, layout, user));

        layout.getChildren().addAll(new Label("Enter Your Password"), textField, submitButton, outputLabel);
    }

    
    
    /**
     * @description  Handles authentication methods
     * 
     * @param        password
     * @param        outputLabel
     */
    private void handleAuthentication(String password, Label outputLabel, VBox layout, User user) {
        boolean authStatus = UserService.authenticate(password);
        
        if (authStatus) {
            outputLabel.setText("Authentication successful!");
            user = DatabaseService.getUser(password, CryptoService.hash(password));
            mainDashboardUI(layout, user);
        } 
        else { outputLabel.setText("Incorrect Password"); }
    }
    
    
    
    /**
     * @description  Main table that displays passwords
     * 
     * @param        layout
     */
    @SuppressWarnings("unchecked")
	private void mainDashboardUI(VBox layout, User user) {
    	
    	// Get all Password records 
    	List<PasswordRecord> records = 
			DatabaseService.getAllPasswordRecords(
				user.getId(), 
				user.getEncryptionKey()
			);
    	
    	// Display as a table (resource_name, ********)
    	TableView<PasswordRecord> tableView = new TableView<>();
        TableColumn<PasswordRecord, String> resourceColumn = new TableColumn<>("Resource");
        resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resource"));
        TableColumn<PasswordRecord, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        tableView.getColumns().addAll(resourceColumn, passwordColumn);
        tableView.setItems(FXCollections.observableArrayList(records));
    	
    	// TODO: Configure logic to show CryptoService.decrypt(record.getPassword(), user.getEncryptionKey()) when user clicks 'show'
    	
        
        // have button to open 'Add New Password Form'
        Button addPasswordButton = new Button("Add Password");
        addPasswordButton.setOnAction(e -> AddPaswordForm(layout, user));
    }
    
    
    
    /**
     * @description  Form to add new password records
     * 
     * @param        layout
     * @param        user
     */
    private void AddPaswordForm(VBox layout, User user) {
    	// TODO: display form (ask for resource name and password)
    	// TODO: create new password record and add to database
        
    }

    
    
    /**
     * @description  Main function that runs the program
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
