package cookbook.controller;

import cookbook.handlers.UserHandler;
import cookbook.handlers.ImageHandler;
import cookbook.handlers.ImageHandler.ImageAndPath;
import cookbook.handlers.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Registration controller.
 */
public class RegisterController {

  @FXML
  private TextField usernameInputField;

  @FXML
  private TextField passwordInputField;

  @FXML
  private Button registerButton;

  @FXML
  private Label feedbackText;

  @FXML
  private void initialize() {
  }

  @FXML
  private void handleRegisterButton(ActionEvent event) throws SQLException, ClassNotFoundException {
    String username = usernameInputField.getText();
    String password = passwordInputField.getText();
    ImageAndPath imageAndPath = ImageHandler
        .getImageAndPathFromRelativePath("/src/main/resources/images/profileImages/defaultUserImage.png");

    // If user is valid and password we register
    boolean isRegisterComplete = UserHandler.getInstance().register(username, password, false,
        imageAndPath.getRelativePath());
    if (isRegisterComplete) {
      Stage stage = (Stage) usernameInputField.getScene().getWindow();
      SceneHandler.switchToScene(stage, "login");
    } else {
      feedbackText.setText("Something went wrong! Username may be taken already.");
    }
  }

  @FXML
  private void handleSwitchToLogin() {
    Stage stage = (Stage) usernameInputField.getScene().getWindow();
    SceneHandler.switchToScene(stage, "login");
  }

  @FXML
  public void handleToLoginButton(ActionEvent event) {
    Stage stage = (Stage) usernameInputField.getScene().getWindow();
    SceneHandler.switchToScene(stage, "login");
  }

}