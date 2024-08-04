package cookbook.controller;

import java.sql.SQLException;

import cookbook.RecipeList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class RecipeListTemplateController {
  @FXML
  private Label listName;

  RecipeList currentList;

  @FXML
  void viewRecipeList(MouseEvent event) {
    Scene scene = listName.getScene();
    Parent root = scene.getRoot();
    HomeController homeController = (HomeController) root.getUserData();
    homeController.setCurrentRecipeList(currentList);
    homeController.loadPage("recipeListViewPage");

  }

  public void setRecipeListData(RecipeList list) throws ClassNotFoundException, SQLException {
    this.currentList = list;
    listName.setText(list.getListName());
  }
}
