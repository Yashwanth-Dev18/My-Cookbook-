package cookbook.controller;

import cookbook.handlers.ImageHandler;
import cookbook.handlers.UserHandler;
import cookbook.handlers.UserInterfaceHandler;
import cookbook.handlers.ImageHandler.ImageAndPath;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ProfileController {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField currentPassField;

  @FXML
  private PasswordField newPassField;

  @FXML
  private PasswordField repeatNewPassField;

  @FXML
  ImageView profileImageView;

  @FXML
  String profileImagePath;

  @FXML
  Label feedbackText;

  @FXML
  ImageView feedbackImage;


  @FXML
  private void initialize() throws ClassNotFoundException, SQLException {
    String currentUserName = UserHandler.getInstance().currentUser.getUsername();
    usernameField.setText(currentUserName);
    profileImageView.setImage(UserHandler.getInstance().currentUser.getImage());
    profileImageView.setFitWidth(200);
    profileImageView.setFitHeight(150);

  }

  @FXML
  public void handleChooseImageButton() throws SQLException, ClassNotFoundException {
    Stage stage = (Stage) profileImageView.getScene().getWindow();
    boolean success = false;
    ImageAndPath imageAndPath = ImageHandler.chooseImage("Choose Profile Image", System.getProperty("user.home"),
        "src/main/resources/images/profileImages/%s/".formatted(UserHandler.getInstance().currentUser.getUsername()), stage);
    if (imageAndPath != null) {
      profileImageView.setImage(imageAndPath.getImage());
      profileImageView.setFitWidth(200);
      profileImageView.setFitHeight(150);  
      profileImagePath = imageAndPath.getRelativePath();
      UserHandler.getInstance().currentUser.setImage(imageAndPath.getImage());
      try {
        success = UserHandler.getInstance().updateUserImage(UserHandler.getInstance().currentUser, profileImagePath);

      } catch (SQLException e) {
        System.out.println(e);
        return;
      }
      if (success) {
        UserInterfaceHandler.getInstance().setFeedbackText("Profile Image changed!", "success", feedbackText, feedbackImage);
        // Get home controller and update profile pic
        Scene scene = usernameField.getScene();
        Parent root = scene.getRoot();
        HomeController homeController = (HomeController) root.getUserData();    
        homeController.updateProfileImage();
      } else {
        UserInterfaceHandler.getInstance().setFeedbackText("Profile Image could not be changed!", "error", feedbackText, feedbackImage);
      }
  
    }

  }

  @FXML
  public void handleSaveButton() throws SQLException, ClassNotFoundException {
    String newUsername = usernameField.getText();
    boolean success = false;

    if (!newUsername.equals(UserHandler.getInstance().currentUser.getUsername())) {
      if (!newUsername.isEmpty()) {
        if (UserHandler.getInstance().doesUsernameExist(newUsername)) {
          UserInterfaceHandler.getInstance().setFeedbackText("This username already exists! Please choose another one.", "error", feedbackText, feedbackImage);
          return;
        } else {
          if (currentPassField.getText().isEmpty()) {
            UserInterfaceHandler.getInstance().setFeedbackText("Please enter current password before saving!", "error", feedbackText, feedbackImage);
            return;
          } else {

            try {
              success = UserHandler.instance.updateUsername(UserHandler.getInstance().currentUser, newUsername, currentPassField.getText());

            } catch (SQLException e) {
              System.out.println(e);
              UserInterfaceHandler.getInstance().setFeedbackText("Error changing the username!", "error", feedbackText, feedbackImage);
              return;
            }
            if (success) {
              UserInterfaceHandler.getInstance().setFeedbackText("Username changed successfully!", "success", feedbackText, feedbackImage);
              UserHandler.getInstance().currentUser.setUsername(newUsername);
              Scene scene = usernameField.getScene();
              Parent root = scene.getRoot();
              HomeController homeController = (HomeController) root.getUserData();          
              homeController.updateUsername();
            } else {
              UserInterfaceHandler.getInstance().setFeedbackText("Could not change the username!", "error", feedbackText, feedbackImage);
            }
          }
        }
      } else {
        UserInterfaceHandler.getInstance().setFeedbackText("Username cannot be empty!", "error", feedbackText, feedbackImage);
      }
    }
    String currentPassword = currentPassField.getText();
    String newPassword = newPassField.getText();
    String repeatNewPassword = repeatNewPassField.getText();
    if(!currentPassword.isEmpty() && !newPassword.isEmpty() && !repeatNewPassword.isEmpty())
    {
      handleChangePassword();
    }

  }

  void handleChangePassword() throws SQLException, ClassNotFoundException {
    String correctPassword = UserHandler.getInstance().currentUser.getPassword();
    String currentPassword = currentPassField.getText();
    String newPassword = newPassField.getText();
    String repeatNewPassword = repeatNewPassField.getText();
    boolean success = false;

    if (currentPassword.isEmpty() || newPassword.isEmpty() || repeatNewPassword.isEmpty()) {
      UserInterfaceHandler.getInstance().setFeedbackText("Please fill all the fields to change your password!", "error", feedbackText, feedbackImage);
    } else if (!currentPassword.equals(correctPassword)) {
      UserInterfaceHandler.getInstance().setFeedbackText("Current password is wrong!", "error", feedbackText, feedbackImage);
    } else if (currentPassword.equals(correctPassword)) {
      if (newPassword.equals(repeatNewPassword)) {
        try {
          success = UserHandler.getInstance().updateUserPassword(UserHandler.getInstance().currentUser, newPassword);
        } catch (SQLException e) {
          UserInterfaceHandler.getInstance().setFeedbackText("An error occured with changing password!", "error", feedbackText, feedbackImage);
          return;
        }
        if (success) {
          UserInterfaceHandler.getInstance().setFeedbackText("Password has been changed!", "success", feedbackText, feedbackImage);
          UserHandler.getInstance().currentUser.setPassword(newPassword);
        }
      } else {
        UserInterfaceHandler.getInstance().setFeedbackText("Passwords do not match!", "error", feedbackText, feedbackImage);
      }
    }

  }
}
