package cookbook.controller;

import java.util.ArrayList;

import cookbook.Recipe;
import cookbook.handlers.RecipeHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class RecommendedController {

  private ArrayList<Recipe> recipes;

  @FXML
  private VBox recipesContainer;

  @FXML
  TextField searchBar;

  @FXML
  private void initialize() throws SQLException, ClassNotFoundException, IOException {
    recipes = RecipeHandler.getInstance().getAllRecipes();
    loadRecipes();

    searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        searchForRecipes();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void searchForRecipes() throws SQLException, ClassNotFoundException, IOException {
    recipes = RecipeHandler.getInstance().searchForRecipes(searchBar.getText());
    loadRecipes();
  }

  private void loadRecipes() throws IOException {
    recipesContainer.getChildren().clear();
    for (Recipe recipe : recipes) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/recipe.fxml"));
      HBox recipeView = loader.load();
      RecipeController recipeController = loader.getController();
      recipeController.setRecipeData(recipe, "");
      recipesContainer.getChildren().add(recipeView);
    }
  }
}
