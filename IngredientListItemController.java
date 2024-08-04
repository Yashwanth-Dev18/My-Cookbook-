package cookbook.controller;

import java.sql.SQLException;

import cookbook.Ingredient;
import cookbook.handlers.IngredientsHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class IngredientListItemController {

  private Ingredient currentIngredient;

  @FXML
  Label ingredientName;

  @FXML
  Label ingredientQuantity;

  @FXML
  HBox ingredientTemplate;

  String currentController;

  @FXML
  private ImageView deleteImage;

  RecipeViewPageController recipeViewPageController;

  public void setIngredientData(Ingredient ingredient, String currentController, RecipeViewPageController recipeViewPageController) {
    this.currentController = currentController;
    this.recipeViewPageController = recipeViewPageController;
    if (currentController == "Recipe View Page") {
      deleteImage.setDisable(true);
      deleteImage.setVisible(false);
    } else if (currentController == "Recipe Edit Page") {
      deleteImage.setDisable(false);
      deleteImage.setVisible(true);
    }

    this.currentIngredient = ingredient;
    ingredientName.setText(ingredient.getName());
    ingredientQuantity.setText(ingredient.getQuantity());

  }

  @FXML
  private void deleteIngredient() throws SQLException, ClassNotFoundException {
    if (currentIngredient.getId() != -1) {
      if(currentController == "Recipe Edit Page")
      {
        if(IngredientsHandler.getInstance().deleteIngredientFromRecipe(currentIngredient))
        {
          VBox parentPane = (VBox) ingredientTemplate.getParent();
          parentPane.getChildren().remove(ingredientTemplate);
          if(recipeViewPageController != null)
          {
            recipeViewPageController.deleteIngredient(currentIngredient);
          }
        }
      }
      else {
        //TODO REMOVE INGREDIENT FROM LIST
        VBox parentPane = (VBox) ingredientTemplate.getParent();
        parentPane.getChildren().remove(ingredientTemplate);
  
      }

    } else {
      VBox parentPane = (VBox) ingredientTemplate.getParent();
      parentPane.getChildren().remove(ingredientTemplate);

    }

  }
}
