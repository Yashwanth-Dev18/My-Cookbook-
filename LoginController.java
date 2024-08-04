package cookbook.controller;

import java.sql.SQLException;

import cookbook.handlers.UserHandler;
import cookbook.handlers.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The login page controller.
 */
public class LoginController {

  @FXML
  private TextField usernameInputField;

  @FXML
  private PasswordField passwordInputField;

  @FXML
  private Button loginButton;

  @FXML
  private Button toRegisterButton;

  @FXML
  private Label feedbackText;

  @FXML
  private void initialize() {
  }

  @FXML
  private void handleLoginButton(ActionEvent event) throws SQLException, ClassNotFoundException {
    String username = usernameInputField.getText();
    String password = passwordInputField.getText();

    boolean isLoggedIn = UserHandler.getInstance().login(username, password);

    if (isLoggedIn) {
      Stage stage = (Stage) usernameInputField.getScene().getWindow();
      SceneHandler.switchToScene(stage, "welcome");
    } else {
      feedbackText.setText("Incorrect login! Please check your username and password!");
    }

  }

  @FXML
  public void handletoRegisterButton(ActionEvent event) {
    Stage stage = (Stage) usernameInputField.getScene().getWindow();
    SceneHandler.switchToScene(stage, "register");
  }
}