package cookbook.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import cookbook.handlers.RecipeListsHandler;
import cookbook.handlers.UserHandler;
import cookbook.handlers.UserInterfaceHandler;
import cookbook.RecipeList;

public class RecipeListsController {
  @FXML
  private ImageView feedbackImage;

  @FXML
  private Label feedbackText;

  @FXML
  private TextField recipeListNameField;

  @FXML
  private VBox recipeListsContainer;

  @FXML
  void createRecipeList(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
    String name = recipeListNameField.getText();
    int ownerId = UserHandler.getInstance().currentUser.getUserId();
    boolean listAdded = RecipeListsHandler.getInstance().addRecipeList(name, ownerId);
    if (listAdded) {
      UserInterfaceHandler.getInstance().setFeedbackText("Recipe list added!", "success", feedbackText, feedbackImage);
      loadRecipeLists();
    } else {
      UserInterfaceHandler.getInstance().setFeedbackText("Recipe list could not be added!", "error", feedbackText,
          feedbackImage);

    }
  }

  @FXML
  private void initialize() throws SQLException, ClassNotFoundException, IOException {
    loadRecipeLists();
  }

  private void loadRecipeLists() throws IOException, ClassNotFoundException, SQLException {
    recipeListsContainer.getChildren().clear();
    ArrayList<RecipeList> lists = RecipeListsHandler.getInstance().getAllLists();

    for (RecipeList list : lists) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/recipeListTemplate.fxml"));
      HBox recipeView = loader.load();
      RecipeListTemplateController recipeController = loader.getController();
      recipeController.setRecipeListData(list);
      recipeListsContainer.getChildren().add(recipeView);

    }

  }
}
