package cookbook.controller;

import java.util.ArrayList;

import cookbook.Recipe;
import cookbook.User;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.UserHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class FavoritesController {

  private ArrayList<Recipe> recipes;

  @FXML
  private VBox recipesContainer;

  @FXML
  private void initialize() throws SQLException, ClassNotFoundException, IOException {
    User user = UserHandler.getInstance().currentUser;
    recipes = RecipeHandler.getInstance().getAllFavoriteRecipes(user.getUserId());
    loadRecipes();
  }

  private void loadRecipes() throws IOException {
    recipesContainer.getChildren().clear();
    for (Recipe recipe : recipes) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/recipe.fxml"));
      HBox recipeView = loader.load();
      RecipeController recipeController = loader.getController();
      recipeController.setRecipeData(recipe, "Favorites Controller");
      recipesContainer.getChildren().add(recipeView);
    }
  }
}
