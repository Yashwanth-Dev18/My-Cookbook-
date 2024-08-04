package cookbook.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.sql.SQLException;
import cookbook.Ingredient;
import cookbook.Recipe;
import cookbook.Tag;
import cookbook.handlers.ImageHandler;
import cookbook.handlers.IngredientsHandler;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.TagsHandler;
import cookbook.handlers.UserHandler;
import cookbook.handlers.UserInterfaceHandler;
import cookbook.handlers.ImageHandler.ImageAndPath;
import java.io.IOException;

public class CreateRecipeController {

  @FXML
  private TextField recipeNameField;

  @FXML
  private TextArea recipeDescriptionArea;

  @FXML
  private TextArea recipeInstructionsArea;

  @FXML
  private TextField tagNameField;

  @FXML
  private TextField ingredientNameField;

  @FXML
  private TextField ingredientQuantity;

  @FXML
  private ComboBox<String> ingredientsComboBox;

  @FXML
  private ComboBox<String> tagSearchResults;

  @FXML
  ImageView recipeImage;

  @FXML
  private TextField servingsTextField;

  @FXML
  private Label feedbackText;

  @FXML
  Label ingredientsResultsLabel;

  @FXML
  Label tagResultsLabel;

  private ArrayList<Ingredient> ingredientsList;
  private ArrayList<Tag> tagsList;

  private ImageAndPath imageAndPath;

  private boolean imageChosen;

  private UserInterfaceHandler uiHandler = UserInterfaceHandler.getInstance();

  @FXML
  ImageView feedbackImageView;

  @FXML
  VBox ingredientsContainer;
  @FXML
  HBox tagsContainer;

  public void initialize() throws ClassNotFoundException, SQLException {
    ingredientsList = new ArrayList<Ingredient>();
    tagsList = new ArrayList<Tag>();

    ingredientNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        searchForIngredient();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    tagNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        searchForTag();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    getAllIngredients();
    getAllTags();
  }

  @FXML
  void addIngredientButtonClicked() throws IOException, ClassNotFoundException, SQLException {
    String name = ingredientNameField.getText();
    String quantity = ingredientQuantity.getText();
    String selectedIngName = ingredientsComboBox.getSelectionModel().getSelectedItem();
    System.out.println(selectedIngName);
    Ingredient ingredient = null;

    if (name.isEmpty() && selectedIngName == null && quantity.isEmpty()) {
      uiHandler.setFeedbackText("Please enter an ingredient name before adding!", "error", feedbackText,
          feedbackImageView);
    } else {

      if (selectedIngName != null && !selectedIngName.isEmpty()) {
        name = ingredientsComboBox.getSelectionModel().getSelectedItem();
        ingredient = IngredientsHandler.getInstance().getIngredientFromRecipeIngredients(name);
        if(!quantity.isEmpty())ingredient.setQuantity(quantity);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        ingredientsList.add(ingredient);
        ingredientListItemController.setIngredientData(ingredient, "Create Recipe Controller", null);
        ingredientsContainer.getChildren().add(ingredientHBox);

      } else {
        Ingredient insertedIngredient = new Ingredient(name, quantity, -1, -1);
        int insertedIngredientId = IngredientsHandler.getInstance().insertIngredient(insertedIngredient);
        insertedIngredient = IngredientsHandler.getInstance().getIngredient(insertedIngredientId);
        System.out.println(insertedIngredient);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        if(!quantity.isEmpty())insertedIngredient.setQuantity(quantity);
        ingredientsList.add(insertedIngredient);
        ingredientListItemController.setIngredientData(insertedIngredient, "Create Recipe Controller", null);
        ingredientsContainer.getChildren().add(ingredientHBox);

      }
    }
  }

  void searchForIngredient() throws ClassNotFoundException, SQLException {
    try {
      ArrayList<Ingredient> allIngredients = IngredientsHandler.getInstance()
          .searchForIngredients(ingredientNameField.getText());

      
      System.out.println(allIngredients.size());

      ArrayList<String> allIngredientStrings = new ArrayList<>();

      for (Ingredient i : allIngredients) {
        allIngredientStrings.add(i.getName());
      }
      ingredientsComboBox.getItems().clear();
      ingredientsComboBox.getItems().addAll(allIngredientStrings);
      ingredientsResultsLabel.setText("Found " + allIngredientStrings.size() + " Ingredients!");


    } catch (SQLException e) {
      System.out.println(e);
      uiHandler.setFeedbackText("Could not search for ingredient!", "error", feedbackText, feedbackImageView);
    }

  }

  void getAllIngredients() throws ClassNotFoundException, SQLException {
    try {
      ArrayList<Ingredient> allIngredients = IngredientsHandler.getInstance().getAllIngredients();

      ArrayList<String> allIngredientStrings = new ArrayList<>();

      for (Ingredient i : allIngredients) {
        allIngredientStrings.add(i.getName());
      }

      ingredientsComboBox.getItems().addAll(allIngredientStrings);

    } catch (SQLException e) {
      System.out.println(e);
      uiHandler.setFeedbackText("Could not get all ingredients!", "error", feedbackText, feedbackImageView);
    }

  }

  void getAllTags() throws ClassNotFoundException, SQLException {
    try {
      ArrayList<Tag> allTags = TagsHandler.getInstance().getAllTags();

      ArrayList<String> allTagsString = new ArrayList<>();

      for (Tag t : allTags) {
        allTagsString.add(t.getName());
      }
      tagSearchResults.getItems().clear();
      tagSearchResults.getItems().addAll(allTagsString);

    } catch (SQLException e) {
      System.out.println(e);
      uiHandler.setFeedbackText("Could not get all tags!", "error", feedbackText, feedbackImageView);
    }

  }

  @FXML
  void searchForTag() throws ClassNotFoundException, SQLException, IOException {
    try {
      ArrayList<Tag> allTags = TagsHandler.getInstance().searchForTags(tagNameField.getText());

      ArrayList<String> allTagsStrings = new ArrayList<>();

      for (Tag t : allTags) {
        allTagsStrings.add(t.getName());
        System.out.println(t.getName());
      }

      tagSearchResults.getItems().clear();
      tagSearchResults.getItems().addAll(allTagsStrings);
      tagResultsLabel.setText("Found " + allTagsStrings.size() + " tags!");
    } catch (SQLException e) {
      uiHandler.setFeedbackText("Could not search for tags!", "error", feedbackText, feedbackImageView);
      System.out.println(e);
    }

  }

  @FXML
  void addTagButtonClick() throws IOException, ClassNotFoundException, SQLException {
    Tag tag = null;
    String name = tagNameField.getText();
    String selectedTagName = tagSearchResults.getSelectionModel().getSelectedItem();
    if (name.isEmpty() && selectedTagName == null) {
      uiHandler.setFeedbackText("Please enter a tag name before adding!", "error", feedbackText, feedbackImageView);
    } else {
      if (selectedTagName != null && !selectedTagName.isEmpty()) {
        tag = TagsHandler.getInstance().getTag(selectedTagName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/tagTemplate.fxml"));
        HBox tagHbox = loader.load();
        TagTemplateController tagTemplateController = loader.getController();
        tagTemplateController.setTagData(tag, "Create Recipe Controller", null);
        tagsContainer.getChildren().add(tagHbox);
        tagsList.add(tag);
      } else {
        tag = new Tag(name, -1, -1);
        tag = TagsHandler.getInstance().insertOrGetTag(tag);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/tagTemplate.fxml"));
        HBox tagHbox = loader.load();
        TagTemplateController tagTemplateController = loader.getController();
        tagTemplateController.setTagData(tag, "Create Recipe Controller", null);
        tagsContainer.getChildren().add(tagHbox);
        tagsList.add(tag);
      }
      tagNameField.clear();
    }
  }

  @FXML
  void saveRecipeButtonClicked() throws SQLException, ClassNotFoundException {
    String name = recipeNameField.getText();
    String description = recipeDescriptionArea.getText();
    String instructions = recipeInstructionsArea.getText();
    String servingsText = servingsTextField.getText();
    int servings = 0;

    try {
      servings = Integer.parseInt(servingsText);
    } catch (NumberFormatException e) {
      uiHandler.setFeedbackText("Servings must be a number!", "error", feedbackText, feedbackImageView);
      return;
    }

    if (name.isEmpty() || description.isEmpty() || instructions.isEmpty() || servingsText.isEmpty()) {
      uiHandler.setFeedbackText("You need to have a name, description, instructions, and servings to create a recipe!",
          "error", feedbackText, feedbackImageView);
      return;
    }
    if (!imageChosen) {
      imageAndPath = ImageHandler
          .getImageAndPathFromRelativePath("/src/main/resources/images/recipes/defaultRecipe.png");
    }
    Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, false, instructions,
        imageAndPath, new ArrayList<>(), UserHandler.getInstance().currentUser.getUserId(), -1);
    boolean insertRecipeSuccess = false;
    try {
      insertRecipeSuccess = RecipeHandler.getInstance().saveRecipe(recipe);
    } catch (SQLException e) {
      System.out.println(e);
      uiHandler.setFeedbackText("Could not save recipe!", "error", feedbackText, feedbackImageView);
      return;
    }
    if (insertRecipeSuccess) {
      uiHandler.setFeedbackText("Recipe inserted successfully", "success", feedbackText, feedbackImageView);

    }

    recipeNameField.clear();
    recipeDescriptionArea.clear();
    recipeInstructionsArea.clear();
    ingredientsList.clear();
    tagsList.clear();
  }

  @FXML
  void chooseImageButtonClicked() {
    Stage stage = (Stage) recipeImage.getScene().getWindow();
    imageAndPath = ImageHandler.chooseImage("Choose Recipe Image", System.getProperty("user.home"),
        "src/main/resources/images/recipes/", stage);
    if (imageAndPath != null) {
      recipeImage.setImage(imageAndPath.getImage());
      recipeImage.setFitHeight(150);
      recipeImage.setFitWidth(200);
      imageChosen = true;

    }
  }
}
