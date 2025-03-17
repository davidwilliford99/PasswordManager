package frontend;

import dal.IUserDao;
import dal.IPasswordRecordDao;
import dal.UserDao;
import dal.PasswordRecordDao;
import bal.UserBal;
import bal.PasswordRecordBal;
import dependencies.sql.DatabaseInitializer;
import models.PasswordRecord;
import models.User;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CryptoService;
import services.ICryptoService;

import static utils.AppDataDirectory.isFirstStart;

/**
 * An offline and open-source password manager application.
 *
 * <p>Application Flow:
 * <ul>
 *   <li><b>First Start:</b>
 *     <ol>
 *       <li>User creates a password.</li>
 *       <li>Database is created and tables are set up.</li>
 *       <li>Database is encrypted with the hashed password.</li>
 *     </ol>
 *   </li>
 *   <li><b>Subsequent Starts:</b>
 *     <ol>
 *       <li>User enters their password.</li>
 *       <li>If correct, the user is shown a list of resources with the option to reveal passwords.</li>
 *     </ol>
 *   </li>
 * </ul>
 *
 * @author David Williford
 */
public class Main extends Application {

  private static final Logger logger = LogManager.getLogger(Main.class);

  private boolean isDarkMode = false;
  private ICryptoService cryptoService;
  private UserBal userBal;
  private PasswordRecordBal passwordRecordBal;
  private DatabaseInitializer databaseInitializer;

  @Override
  public void start(Stage primaryStage) throws NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    cryptoService = new CryptoService(cipher, digest);

    IUserDao userDao = new UserDao(cryptoService);
    IPasswordRecordDao passwordRecordDao = new PasswordRecordDao(cryptoService);

    userBal = new UserBal(userDao, cryptoService);
    passwordRecordBal = new PasswordRecordBal(passwordRecordDao);

    databaseInitializer = new DatabaseInitializer(cryptoService);

    VBox layout = new VBox(10);
    Scene scene = new Scene(layout, 600, 400);

    layout.setPadding(new Insets(5));
    layout.getStyleClass().add("root");

    primaryStage.setTitle("Password Manager");

    Button darkModeButton = new Button("Dark Mode");
    darkModeButton.setOnAction(e -> toggleDarkMode(scene, darkModeButton));
    layout.getChildren().add(darkModeButton);

    User user = null;

    if (isFirstStart()) {
      setupFirstStartUI(layout, user);
    } else {
      setupAuthenticationUI(layout, user);
    }

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Toggles dark mode for the application.
   *
   * @param scene The current scene.
   * @param darkModeButton The button to toggle dark mode.
   */
  private void toggleDarkMode(Scene scene, Button darkModeButton) {
    if (isDarkMode) {
      scene.getStylesheets().clear();
      darkModeButton.setText("Dark Mode");
    } else {
      scene.getStylesheets().add(getClass().getClassLoader().getResource("styles/dark-theme.css").toExternalForm());
      darkModeButton.setText("Light Mode");
    }
    isDarkMode = !isDarkMode;
  }

  /**
   * Sets up the UI for the first start of the application.
   *
   * @param layout The layout to add UI elements to.
   * @param user The user object.
   */
  private void setupFirstStartUI(VBox layout, User user) {
    TextField textField = new TextField();
    textField.setPromptText("Create a New Password");

    Button submitButton = new Button("Submit");
    submitButton.setOnAction(e -> handleFirstStart(textField.getText(), layout, user));

    Label label = new Label("Set Up Your Password");

    layout.getChildren().addAll(label, textField, submitButton);
    layout.setAlignment(Pos.CENTER);
    layout.setSpacing(10);
    layout.setFillWidth(false);
  }

  /**
   * Handles the first start of the application, including bootstrapping and user creation.
   *
   * @param password The password entered by the user.
   * @param layout The layout to update.
   * @param user The user object.
   */
  private void handleFirstStart(String password, VBox layout, User user) {
    try {
      databaseInitializer.bootstrap(cryptoService.hash(password));
    } catch (UnsupportedEncodingException e) {
      logger.error("Error occurred while hashing password for database");
    }

    user = userBal.createUser(password);
    mainDashboardUI(layout, user);
  }

  /**
   * Sets up the authentication UI for non-first starts.
   *
   * @param layout The layout to add UI elements to.
   * @param user The user object.
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
   * Handles user authentication.
   *
   * @param password The password entered by the user.
   * @param outputLabel The label to display authentication status.
   * @param layout The layout to update.
   * @param user The user object.
   */
  private void handleAuthentication(String password, Label outputLabel, VBox layout, User user) {
    boolean authStatus = userBal.authenticateUser(password);

    if (authStatus) {
      outputLabel.setText("Authentication successful!");
      try {
        user = userBal.getUser(password, cryptoService.hash(password));
      } catch (UnsupportedEncodingException e) {
        logger.error("Error occurred while hashing password", e);
      }
      mainDashboardUI(layout, user);
    } else {
      outputLabel.setText("Incorrect Password");
    }
  }

  /**
   * Displays the main dashboard UI with a table of password records.
   *
   * @param layout The layout to update.
   * @param user The user object.
   */
  @SuppressWarnings("unchecked")
  private void mainDashboardUI(VBox layout, User user) {
    layout.getChildren().clear();

    List<PasswordRecord> records = passwordRecordBal.getAllPasswordRecords(user.getId(), user.getEncryptionKey());

    TableView<PasswordRecord> tableView = new TableView<>();

    TableColumn<PasswordRecord, String> resourceColumn = new TableColumn<>("Resource");
    resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resource"));

    TableColumn<PasswordRecord, Void> showPasswordColumn = new TableColumn<>("Show Password");
    showPasswordColumn.setPrefWidth(400);
    showPasswordColumn.setCellFactory(param ->
        new PasswordVisibilityCell(user, cryptoService));

    TableColumn<PasswordRecord, Void> deletePasswordColumn = new TableColumn<>("");
    deletePasswordColumn.setCellFactory(
        param -> new DeletePasswordCell(user, tableView, passwordRecordBal));

    tableView.getColumns().addAll(resourceColumn, showPasswordColumn, deletePasswordColumn);
    tableView.setItems(FXCollections.observableArrayList(records));

    Button addPasswordButton = new Button("Add Password");
    addPasswordButton.setOnAction(e -> showAddPasswordForm(layout, user));

    layout.getChildren().addAll(tableView, addPasswordButton);
  }

  /**
   * Displays the form to add a new password record.
   *
   * @param layout The layout to update.
   * @param user The user object.
   */
  private void showAddPasswordForm(VBox layout, User user) {
    TextField resourceField = new TextField();
    resourceField.setPromptText("Resource (Gmail, Reddit, etc)");

    TextField passwordField = new TextField();
    passwordField.setPromptText("Password");

    Button addPasswordButton = new Button("Add new password record");
    addPasswordButton.setOnAction(e -> {
      try {
        passwordRecordBal.addPasswordRecord(user.getId(), resourceField.getText(), passwordField.getText(), user.getEncryptionKey());
        mainDashboardUI(layout, user);
      } catch (Exception e1) {
        logger.error("Error occurred while adding new password record", e1);
      }
    });

    Button cancelPasswordButton = new Button("Cancel");
    cancelPasswordButton.setOnAction(e -> mainDashboardUI(layout, user));

    HBox formButtons = new HBox(5, addPasswordButton, cancelPasswordButton);

    layout.getChildren().remove(1);
    layout.getChildren().addAll(resourceField, passwordField, formButtons);
  }

  /**
   * Main method to launch the application.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Custom table cell for toggling password visibility.
   */
  private static class PasswordVisibilityCell extends TableCell<PasswordRecord, Void> {
    private final Button showButton = new Button("Show Password");
    private final TextField passwordLabel = new TextField("**********");
    private boolean isPasswordVisible = false;
    private final User user;
    private final ICryptoService cryptoService;

    PasswordVisibilityCell(User user, ICryptoService cryptoService) {
      this.user = user;
      this.cryptoService = cryptoService;
      passwordLabel.setEditable(false);
      passwordLabel.setFocusTraversable(false);
      showButton.setPrefWidth(120);

      showButton.setOnAction(event -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
      PasswordRecord record = getTableView().getItems().get(getIndex());
      if (isPasswordVisible) {
        passwordLabel.setText("**********");
        showButton.setText("Show Password");
      } else {
        try {
          String decryptedPassword = cryptoService.decrypt(record.getPassword(), user.getEncryptionKey());
          passwordLabel.setText(decryptedPassword);
        } catch (Exception e) {
          logger.error("Error occurred while decrypting password", e);
        }
        showButton.setText("Hide Password");
      }
      isPasswordVisible = !isPasswordVisible;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
        setGraphic(null);
      } else {
        setGraphic(new HBox(10, showButton, passwordLabel));
      }
    }
  }

  /**
   * Custom table cell for deleting password records.
   */
  private static class DeletePasswordCell extends TableCell<PasswordRecord, Void> {
    private final Button deleteButton = new Button("Delete");
    private final User user;
    private final TableView<PasswordRecord> tableView;
    private final PasswordRecordBal passwordRecordBal;

    DeletePasswordCell(User user, TableView<PasswordRecord> tableView, PasswordRecordBal passwordRecordBal) {
      this.user = user;
      this.tableView = tableView;
      this.passwordRecordBal = passwordRecordBal;
      deleteButton.setPrefWidth(80);

      deleteButton.setOnAction(event -> confirmAndDeletePassword());
    }

    private void confirmAndDeletePassword() {
      PasswordRecord record = getTableView().getItems().get(getIndex());

      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Confirmation");
      alert.setHeaderText("Are you sure you want to delete this password?");
      alert.setContentText("This action cannot be undone.");

      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        passwordRecordBal.deletePasswordRecord(record.getResource(), user.getEncryptionKey());
        tableView.getItems().remove(record);
      }
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
        setGraphic(null);
      } else {
        setGraphic(new HBox(10, deleteButton));
      }
    }
  }
}