package cookbook.controller;

import cookbook.Recipe;
import cookbook.RecipeList;
import cookbook.handlers.SceneHandler;
import cookbook.handlers.UserHandler;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Home page controller.
 */
public class HomeController {

  @FXML
  private Label usernameLabel;

  @FXML
  private BorderPane borderPane;

  @FXML
  ImageView homeUserImage;

  Recipe currentRecipe;

  @FXML
  HBox adminHbox;

  @FXML
  VBox sideButtonsContainer;

  private RecipeList recipeList;

  @FXML
  private void initialize() {

    // Load the default view (Recently Added etc...)
    loadPage("recommended");

    Platform.runLater(() -> {
      try {
        usernameLabel.setText(UserHandler.getInstance().currentUser.getUsername());
        homeUserImage.setImage(UserHandler.getInstance().currentUser.getImage());
      } catch (SQLException | ClassNotFoundException e) {
        System.out.println("error in home controller.");
      }
      homeUserImage.setFitWidth(50);
      homeUserImage.setFitHeight(50);
      try {
        if (!UserHandler.getInstance().currentUser.isAdmin()) {
          sideButtonsContainer.getChildren().remove(adminHbox);
        }
      } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
      }

      // Set this controller instance as user data
      Scene scene = borderPane.getScene();
      if (scene != null) {
        scene.getRoot().setUserData(this);
      }
    });
  }

  public void setCurrentRecipe(Recipe r) {
    this.currentRecipe = r;
  }

  public Recipe getCurrentRecipe() {
    return this.currentRecipe;
  }

  // Method to load a page from an FXML file
  public void loadPage(String fxmlFileName) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/"
          + fxmlFileName + ".fxml"));

      Node page = loader.load();
      // Replace the content of the center area with the loaded page
      borderPane.setCenter(page);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void updateProfileImage() throws SQLException, ClassNotFoundException {
    homeUserImage.setImage(UserHandler.getInstance().currentUser.getImage());
  }

  @FXML
  public void handleHelpButtonClick() {
    loadPage("help");
  }

  @FXML
  private void handleProfileButtonClick() {
    loadPage("profile");
  }
  @FXML
  private void handleLogoutButtonClick() throws ClassNotFoundException, SQLException {
    UserHandler.getInstance().logout();
    Stage stage = (Stage) borderPane.getScene().getWindow();
    SceneHandler.switchToScene(stage, "login");
  }

  @FXML
  private void handleCreatedRecipesButtonClick() {
    loadPage("createdRecipes");
  }

  @FXML
  private void handleCreateRecipeButtonClick() {
    loadPage("createRecipePageVer2");
  }

  @FXML
  private void handleCreateRecipeListButtonClick() {
    loadPage("createRecipeList");
  }

  @FXML
  private void handleRecommendedButtonClick() {
    loadPage("recommended");
  }

  @FXML
  private void handleRecipeListsButtonClick() {
    loadPage("MyLists");
  }

  @FXML
  private void handleFavoritesButtonClick() {
    loadPage("favorites");
  }

  public void updateUsername() throws ClassNotFoundException, SQLException {
    usernameLabel.setText(UserHandler.getInstance().currentUser.getUsername());
  }

  @FXML
  private void handleAdminButtonClick() {
    loadPage("admin");
  }

  public void setCurrentRecipeList(RecipeList currentList) {
    this.recipeList = currentList;
  }

  public RecipeList getCurrentRecipeList() {
    return this.recipeList;
  }

}
