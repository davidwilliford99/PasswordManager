package frontend;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
	
	boolean isDarkMode = false;

	
    @Override
    public void start(Stage primaryStage) {
    	
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 600, 400);
        
        layout.setPadding(new Insets(5)); // padding
        layout.getStyleClass().add("root");
        
        primaryStage.setTitle("Password Manager");
        
        /**
         * Dark mode logic
         */
        Button darkModeButton = new Button("Dark Mode");
        darkModeButton.setOnAction(e -> {
            if (isDarkMode) {
                scene.getStylesheets().clear(); // Remove dark mode
                darkModeButton.setText("Dark Mode");
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
                darkModeButton.setText("Light Mode");
            }
            isDarkMode = !isDarkMode;
        });
        layout.getChildren().addAll(darkModeButton);
        
        
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

        Label label = new Label("Set Up Your Password");

        // Set layout properties
        layout.getChildren().addAll(label, textField, submitButton);
        layout.setAlignment(Pos.CENTER); // Center elements
        layout.setSpacing(10); // Space between elements
        layout.setFillWidth(false); // Prevent full width filling
    }

    
    
    /**
     * @description  Handle the first start bootstrap
     * 
     * @param        password
     */
    private void handleFirstStart(String password, VBox layout, User user) {
    	
    	// bootstrap application
        try {MainService.bootstrap(CryptoService.hash(password));} 
        catch (UnsupportedEncodingException e) {e.printStackTrace();}
        
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
            
            try {user = DatabaseService.getUser(password, CryptoService.hash(password));} 
            catch (UnsupportedEncodingException e) {e.printStackTrace();}
            
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
        
        layout.getChildren().clear();
        
        List<PasswordRecord> records = 
            DatabaseService.getAllPasswordRecords(
                user.getId(), 
                user.getEncryptionKey()
            );
        
        TableView<PasswordRecord> tableView = new TableView<>();
        
        TableColumn<PasswordRecord, String> resourceColumn = new TableColumn<>("Resource");
        resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resource"));

        TableColumn<PasswordRecord, Void> showPasswordColumn = new TableColumn<>("Show Password");
        showPasswordColumn.setPrefWidth(400);
  
        // Add a button to show the password in each row
        showPasswordColumn.setCellFactory(param -> 
	        new TableCell<PasswordRecord, Void>() {
	            private final Button showButton = new Button("Show Password");	            
	            private final TextField passwordLabel = new TextField("**********");
	            private boolean isPasswordVisible = false;
	            
	            {
		            passwordLabel.setEditable(false);
		            passwordLabel.setFocusTraversable(false);
		            showButton.setPrefWidth(120);
	            	
	                // Button click event to toggle password visibility
	                showButton.setOnAction(event -> {
	                    PasswordRecord record = getTableView().getItems().get(getIndex());
	                    
	                    if (isPasswordVisible) {
	                        passwordLabel.setText("**********");
	                        showButton.setText("Show Password");
	                    } else {
	                        try {
	                            String decryptedPassword = CryptoService.decrypt(record.getPassword(), user.getEncryptionKey());
	                            passwordLabel.setText(decryptedPassword); 
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                        showButton.setText("Hide Password");
	                    }
	                    
	                    isPasswordVisible = !isPasswordVisible; // Toggle state
	                });
	            }
	
	            @Override
	            protected void updateItem(Void item, boolean empty) {
	                super.updateItem(item, empty);
	                if (empty) {
	                    setGraphic(null);
	                } else {
	                    HBox container = new HBox(10, showButton, passwordLabel); // Layout to hold button & label
	                    setGraphic(container);
	                }
	            }
	        }
	    );
        
        
        // Delete password column 
        TableColumn<PasswordRecord, Void> deletePasswordColumn = new TableColumn<>("");

        deletePasswordColumn.setCellFactory(param -> 
            new TableCell<PasswordRecord, Void>() {
                private final Button deleteButton = new Button("Delete");            

                {
                    deleteButton.setPrefWidth(80);

                    deleteButton.setOnAction(event -> {
                        PasswordRecord record = getTableView().getItems().get(getIndex());

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete Confirmation");
                        alert.setHeaderText("Are you sure you want to delete this password?");
                        alert.setContentText("This action cannot be undone.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            DatabaseService.deletePasswordRecord(record.getResource(), user.getEncryptionKey());

                            // Remove from TableView
                            getTableView().getItems().remove(record);
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox container = new HBox(10, deleteButton); // Use HBox for proper alignment
                        setGraphic(container);
                    }
                }
            }
        );



        tableView.getColumns().addAll(resourceColumn, showPasswordColumn, deletePasswordColumn);
        tableView.setItems(FXCollections.observableArrayList(records));

        Button addPasswordButton = new Button("Add Password");
        addPasswordButton.setOnAction(e -> {
        	AddPaswordForm(layout, user);
        	
        });

        // Render table and button to UI
        layout.getChildren().addAll(tableView, addPasswordButton);
    }
    
    
    
    /**
     * @description  Form to add new password records
     * 
     * @param        layout
     * @param        user
     */
    private void AddPaswordForm(VBox layout, User user) {

    	TextField resourceField = new TextField();
    	resourceField.setPromptText("Resource (Gmail, Reddit, etc)");
    	
    	TextField passwordField = new TextField();
    	passwordField.setPromptText("Password");
    	
    	Button addPasswordButton = new Button("Add new password record");
    	addPasswordButton.setOnAction(e -> {
    		try {
				DatabaseService.addNewPasswordRecord(
					user.getId(), 
					resourceField.getText(), 
					passwordField.getText(), 
					user.getEncryptionKey()
				);
				// refresh view
				mainDashboardUI(layout, user);
			} 
    		catch (Exception e1) {e1.printStackTrace();}
    	});
    	
    	Button cancelPasswordButton = new Button("Cancel");
    	cancelPasswordButton.setOnAction(e -> {
			mainDashboardUI(layout, user);
    	});
    	
    	HBox formButtons = new HBox(5, addPasswordButton, cancelPasswordButton);
    	
        layout.getChildren().remove(1); // remove "add button"
    	layout.getChildren().addAll(resourceField, passwordField, formButtons);
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
