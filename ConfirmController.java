package cookbook.controller;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ConfirmController {

  @FXML
  private VBox confirmTemplate;

  @FXML
  private Label labelText;
  
  private RecipeViewPageController recipeViewPageController  = null;
  private RecipeListViewPageContoller recipeListViewPageContoller = null;


  public void setup(String text, Object controller)
  {
    labelText.setText(text);

    if(controller instanceof RecipeViewPageController)
    {
      this.recipeViewPageController = (RecipeViewPageController) controller;
    }
    else if(controller instanceof RecipeListViewPageContoller)
    {
      this.recipeListViewPageContoller = (RecipeListViewPageContoller) controller;
    }
  }

  @FXML
  void noButton(ActionEvent event) throws ClassNotFoundException, SQLException {
    if(recipeViewPageController != null)
    {
      recipeViewPageController.ConfirmAction("Delete Recipe", false);
    }
    else if(recipeListViewPageContoller != null)
    {
      recipeListViewPageContoller.ConfirmAction("Delete Recipe List", false);
    }
  }

  @FXML
  void yesButton(ActionEvent event) throws ClassNotFoundException, SQLException {
    if(recipeViewPageController != null)
    {
      recipeViewPageController.ConfirmAction("Delete Recipe", true);
    }
    else if(recipeListViewPageContoller != null)
    {
      recipeListViewPageContoller.ConfirmAction("Delete Recipe List", true);
    }
  }


}
