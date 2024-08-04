package cookbook.controller;

import java.sql.SQLException;

import cookbook.Recipe;
import cookbook.Tag;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.RecipeListsHandler;
import cookbook.handlers.UserInterfaceHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RecipeController {

  @FXML
  private ImageView recipeImage;

  @FXML
  private Label recipeName;

  @FXML
  private Text recipeDescription;

  @FXML
  private Text recipeTags;

  @FXML
  private Label recipeServings;

  @FXML
  ImageView favImageView;

  @FXML
  private ImageView deleteImageView;

  private Recipe recipe;

  @FXML
  HBox recipeTemplate;

  String currentController;

  public void setRecipeData(Recipe recipe, String controllerName) {
    currentController = controllerName;
    if (controllerName == "recipeList") {
      deleteImageView.setVisible(true);
      deleteImageView.setDisable(false);
    } else {
      deleteImageView.setVisible(false);
      deleteImageView.setDisable(true);
    }
    this.recipe = recipe;

    recipeName.setText(recipe.getName());

    recipeImage.setFitWidth(200);
    recipeImage.setFitHeight(150);
    recipeImage.setImage(recipe.getImageAndPath().getImage());

    String description = recipe.getDescription();

    recipeDescription.setText(description);

    String servings = Integer.toString(recipe.getServings());
    recipeServings.setText("Servings: " + servings);

    String tagsString = "";
    for (Tag tag : recipe.getTags()) {
      tagsString += tag.getName() + ", ";
    }
    if (!tagsString.isEmpty()) {
      recipeTags.setText("Tags: " + tagsString);
    }

    try {
      if (RecipeHandler.getInstance().checkIfRecipeIsFavorite(recipe)) {
        favImageView.setImage(UserInterfaceHandler.getInstance().favImage);
      } else {
        favImageView.setImage(UserInterfaceHandler.getInstance().notFavImage);
      }
    } catch (SQLException e) {
      System.out.println(e);
    } catch (ClassNotFoundException e) {
      System.out.println(e);
    }

  }

  @FXML
  private void openRecipe() {
    Scene scene = recipeName.getScene();
    Parent root = scene.getRoot();
    HomeController homeController = (HomeController) root.getUserData();
    homeController.loadPage("recipeViewPage");
    homeController.setCurrentRecipe(recipe);

  }

  @FXML
  private void favoriteButtonClicked() throws SQLException, ClassNotFoundException {
    boolean success = false;
    try {
      success = RecipeHandler.getInstance().setRecipeFavorite(recipe);

    } catch (SQLException e) {
      System.out.println(e);
      return;
    }

    if (success) {
      if (RecipeHandler.getInstance().checkIfRecipeIsFavorite(recipe)) {
        favImageView.setImage(UserInterfaceHandler.getInstance().favImage);
      } else {
        favImageView.setImage(UserInterfaceHandler.getInstance().notFavImage);
        if (currentController == "Favorites Controller") {
          VBox recipeParent = (VBox) recipeTemplate.getParent();
          recipeParent.getChildren().remove(recipeTemplate);
        }
      }
    }
  }

  public void makeDeleteVisible() {
    deleteImageView.setVisible(true);
    deleteImageView.setDisable(false);
  }

  @FXML
  private void removeRecipeFromList() throws ClassNotFoundException, SQLException {
    if (RecipeListsHandler.getInstance().removeRecipeFromList(recipe)) {
      VBox recipesContainer = (VBox) recipeTemplate.getParent();
      recipesContainer.getChildren().remove(recipeTemplate);
    } else {

    }
  }
}
