package cookbook.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.Recipe;
import cookbook.RecipeList;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.RecipeListsHandler;
import cookbook.handlers.UserInterfaceHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class RecipeListViewPageContoller {
  @FXML
  private Button addRecipe;

  @FXML
  private ImageView feedbackImageView;

  @FXML
  private Label feedbackText;

  @FXML
  private Label recipeListName;

  @FXML
  private ComboBox<String> recipesComboBox;

  @FXML
  private VBox vbox;

  private RecipeList recipeList;

  @FXML
  private VBox recipesContainer;

  ArrayList<Recipe> recipes = new ArrayList<>();
  ArrayList<Recipe> allRecipes = new ArrayList<>();

  private Stage confirmStage;

  @FXML
  private void initialize() {
    Platform.runLater(() -> {
      Scene scene = recipeListName.getScene();
      Parent root = scene.getRoot();
      HomeController homeController = (HomeController) root.getUserData();
      this.recipeList = homeController.getCurrentRecipeList();
      try {
        allRecipes = RecipeHandler.getInstance().getAllRecipes();
      } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
      }
      ArrayList<String> recipeNames = new ArrayList<String>();
      for (Recipe r : allRecipes) {
        recipeNames.add(r.getName());
      }

      recipesComboBox.getItems().setAll(recipeNames);

      try {

        setData();
      } catch (ClassNotFoundException | SQLException | IOException e) {
        System.out.println("ERROR IN RECIPE LIST VIEW PAGE: " + e);
      }
    });

  }

  void setData() throws ClassNotFoundException, SQLException, IOException {
    recipeListName.setText(this.recipeList.getListName());

    recipes = RecipeListsHandler.getInstance().getAllRecipesFromList(recipeList);
    recipesContainer.getChildren().clear();
    for (Recipe recipe : recipes) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/recipe.fxml"));
      HBox recipeView = loader.load();
      RecipeController recipeController = loader.getController();
      recipeController.setRecipeData(recipe, "");
      recipesContainer.getChildren().add(recipeView);
      recipeController.makeDeleteVisible();
      this.recipeList.recipeInsertion(recipe);
    }

  }

  @FXML
  private void addRecipeToList() throws ClassNotFoundException, SQLException, IOException {
    Recipe selectedRecipe = null;
    for (Recipe r : allRecipes) {
      if (r.getName() == recipesComboBox.getSelectionModel().getSelectedItem()) {
        selectedRecipe = r;
      }
    }
    if (selectedRecipe != null) {
      if (RecipeListsHandler.getInstance().addRecipeToList(selectedRecipe, recipeList.getListId())) {
        recipes.add(selectedRecipe);
        this.recipeList.recipeInsertion(selectedRecipe);
        setData();
      }
    }
  }

  @FXML
  private void deleteRecipeListUI() throws ClassNotFoundException, SQLException, IOException {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/confirmTemplate.fxml"));
    Parent root = loader.load();
    ConfirmController confirmController = loader.getController();
    confirmController.setup("Are you sure you want to delete this list?", this);
    confirmStage = new Stage();
    Scene scene = new Scene(root);
    confirmStage.setScene(scene);
    confirmStage.show();
  }

  private void deleteRecipeList() throws ClassNotFoundException, SQLException {
    if (RecipeListsHandler.getInstance().deleteList(recipeList)) {
      UserInterfaceHandler.getInstance().setFeedbackText("List deleted successfully!", "success", feedbackText,
          feedbackImageView);
      this.recipeList.clear();
      Scene scene = recipeListName.getScene();
      Parent root = scene.getRoot();
      HomeController homeController = (HomeController) root.getUserData();
      homeController.loadPage("MyLists");

    } else {
      UserInterfaceHandler.getInstance().setFeedbackText("List could not be deleted!", "error", feedbackText,
          feedbackImageView);

    }

  }
  public void ConfirmAction(String action, boolean b) throws ClassNotFoundException, SQLException {
    if(action == "Delete Recipe List" && b == true)
    {
      deleteRecipeList();
    }
    closeConfirmUI();
  }

  private void closeConfirmUI() {
    confirmStage.close();
  }

}
