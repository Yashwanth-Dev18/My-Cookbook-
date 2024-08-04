package cookbook.controller;

import cookbook.Recipe;
import cookbook.User;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.IOException;

public class MyRecipesController {
  @FXML
  private ImageView feedbackImage;

  @FXML
  private Label feedbackText;

  @FXML
  private VBox recipesContainer;

  private ArrayList<Recipe> recipes;

  @FXML
  private void initialize() throws SQLException, ClassNotFoundException, IOException {
    User user = UserHandler.getInstance().currentUser;
    recipes = RecipeHandler.getInstance().getAllRecipesFromUserId(user.getUserId());
    loadRecipes();
  }

  private void loadRecipes() throws IOException {
    recipesContainer.getChildren().clear();
    for (Recipe recipe : recipes) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/recipe.fxml"));
      HBox recipeView = loader.load();
      RecipeController recipeController = loader.getController();
      recipeController.setRecipeData(recipe, "myrecipes");
      recipesContainer.getChildren().add(recipeView);
    }
  }

}
