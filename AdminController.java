package cookbook.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.User;
import cookbook.handlers.ImageHandler;
import cookbook.handlers.ImageHandler.ImageAndPath;
import cookbook.handlers.UserHandler;
import cookbook.handlers.UserInterfaceHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AdminController {

  @FXML
  private TextField createPasswordField;

  @FXML
  private TextField createUsernameField;

  @FXML
  private ImageView feedbackImage;

  @FXML
  private Label feedbackText;

  @FXML
  private CheckBox isAdminCheckbox;

  @FXML
  private TextField newPassField;

  @FXML
  private TextField newUsernameField;

  @FXML
  private ComboBox<String> userResults;

  @FXML
  private TextField usernameSearchField;

  private ImageAndPath imageAndPath;

  private boolean imageChosen = false;

  @FXML
  private ImageView profileImageView;

  private User selectedUser;

  @FXML
  void initialize() throws ClassNotFoundException, SQLException {
    usernameSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        searchForUsers();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    searchForUsers();

  }

  private void searchForUsers() throws SQLException, ClassNotFoundException {
    ArrayList<User> users = UserHandler.getInstance().getAllUsers(usernameSearchField.getText());
    ArrayList<String> usernames = new ArrayList<>();

    for (User u : users) {
      usernames.add(u.getUsername());
    }
    userResults.getItems().clear();
    userResults.getItems().addAll(usernames);
    userResults.getSelectionModel().selectFirst();

    selectUser();

  }

  @FXML
  void addNewUser(ActionEvent event) throws ClassNotFoundException, SQLException {
    String username = createUsernameField.getText();
    String password = createPasswordField.getText();
    Boolean isAdmin = isAdminCheckbox.isSelected();
    if(username.isEmpty() && password.isEmpty())
    {
      UserInterfaceHandler.getInstance().setFeedbackText("Please choose username and password before adding a new user!", "error", feedbackText, feedbackImage);
      return;
    }
    if (!imageChosen) {
      imageAndPath = ImageHandler
          .getImageAndPathFromRelativePath("/src/main/resources/images/profileImages/defaultUserImage.png");
    }
    boolean isRegisterComplete = UserHandler.getInstance().register(username, password, isAdmin,
        imageAndPath.getRelativePath());
    if (isRegisterComplete) {
      UserInterfaceHandler.getInstance().setFeedbackText("User successfully registered!", "success", feedbackText,
          feedbackImage);
      usernameSearchField.clear();
      searchForUsers();
    } else {
      UserInterfaceHandler.getInstance().setFeedbackText("Something went wrong! Username may be taken already.",
          "error", feedbackText, feedbackImage);
    }

  }

  @FXML
  void chooseUserImage() throws ClassNotFoundException, SQLException {
    Stage stage = (Stage) profileImageView.getScene().getWindow();
    imageAndPath = ImageHandler.chooseImage("Choose Profile Image", System.getProperty("user.home"),
        "src/main/resources/images/profileImages/%s/".formatted(UserHandler.getInstance().currentUser.getUsername()), stage);
    if (imageAndPath.getImage() != null) {
      profileImageView.setImage(imageAndPath.getImage());
      profileImageView.setFitWidth(200);
      profileImageView.setFitHeight(150);
      imageChosen = true;
    }

  }

  @FXML
  void deleteUser(ActionEvent event) throws ClassNotFoundException, SQLException {
    String username = userResults.getSelectionModel().getSelectedItem();
    int user_id = UserHandler.getInstance().getUser(username).getUserId();
    if (UserHandler.getInstance().deleteUser(user_id)) {
      UserInterfaceHandler.getInstance().setFeedbackText("User account deleted!", "success", feedbackText,
          feedbackImage);
      usernameSearchField.clear();
      searchForUsers();
    } else {
      UserInterfaceHandler.getInstance().setFeedbackText("User account could not be deleted!", "error", feedbackText,
          feedbackImage);
    }
  }

  @FXML
  void saveChanges(ActionEvent event) throws ClassNotFoundException, SQLException {
    String newUsername = newUsernameField.getText();
    boolean success = false;

    if (!newPassField.getText().isEmpty()) {
      handleChangePassword();
    }
    
    if (!newUsername.isEmpty()) {
      if (UserHandler.getInstance().doesUsernameExist(newUsername)) {
        UserInterfaceHandler.getInstance().setFeedbackText("This username already exists! Please choose another one.",
            "error", feedbackText, feedbackImage);
      } else {

        try {
          success = UserHandler.getInstance().updateUsername(selectedUser, newUsername, selectedUser.getPassword());

        } catch (SQLException e) {
          System.out.println(e);
          UserInterfaceHandler.getInstance().setFeedbackText("Error changing the username!", "error", feedbackText,
              feedbackImage);
          return;
        }
        if (success) {
          UserInterfaceHandler.getInstance().setFeedbackText("Username changed successfully!", "success",
              feedbackText, feedbackImage);
              selectedUser = UserHandler.getInstance().getUser(newUsername);
          
          usernameSearchField.clear();
          searchForUsers();

        } else {
          UserInterfaceHandler.getInstance().setFeedbackText("Could not change the username!", "error",
              feedbackText, feedbackImage);
        }
      }
    } else {
      UserInterfaceHandler.getInstance().setFeedbackText("Username cannot be empty!", "error", feedbackText,
          feedbackImage);
    }


  }

  void handleChangePassword() throws SQLException, ClassNotFoundException {
    String newPassword = newPassField.getText();
    boolean success = false;

    try {
      success = UserHandler.getInstance().updateUserPassword(selectedUser, newPassword);
    } catch (SQLException e) {
      UserInterfaceHandler.getInstance().setFeedbackText("An error occured with changing password!", "error",
          feedbackText, feedbackImage);
      return;
    }
    if (success) {
      UserInterfaceHandler.getInstance().setFeedbackText("Password has been changed!", "success", feedbackText,
          feedbackImage);
      this.selectedUser.setPassword(newPassword);
    }

  }
  @FXML
  private void selectUser() throws SQLException, ClassNotFoundException
  {
    this.selectedUser = UserHandler.getInstance().getUser(userResults.getSelectionModel().getSelectedItem());
  }

}
