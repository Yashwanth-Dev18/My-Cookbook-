package cookbook.controller;

import java.io.IOException;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import cookbook.Comment;
import cookbook.Ingredient;
import cookbook.Recipe;
import cookbook.Tag;
import cookbook.handlers.ImageHandler;
import cookbook.handlers.ImageHandler.ImageAndPath;
import cookbook.handlers.IngredientsHandler;
import cookbook.handlers.RecipeHandler;
import cookbook.handlers.TagsHandler;
import cookbook.handlers.UserHandler;
import cookbook.handlers.UserInterfaceHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.ArrayList;

public class RecipeViewPageController {
  @FXML
  VBox recipeViewPageRoot;

  private ImageAndPath imageAndPath;

  private boolean imageChosen;

  @FXML
  TextField recipeNameField;
  @FXML
  TextArea recipeDescriptionArea;
  @FXML
  TextArea recipeInstructionsArea;
  @FXML
  ImageView recipeImage;
  @FXML
  ListView<String> ingredientsListView;
  @FXML
  ImageView favImageView;

  @FXML
  VBox ingredientsContainer;

  private Recipe recipe;

  @FXML
  Label feedbackText;

  @FXML
  ImageView feedbackImageView;

  @FXML
  VBox addIngredientVbox;

  UserInterfaceHandler uiHandler = UserInterfaceHandler.getInstance();
  UserHandler userHandler;

  String currentMode = "Recipe View Page";

  @FXML
  HBox tagsContainer;

  @FXML
  TextField ingredientNameField;
  @FXML
  TextField tagNameField;

  @FXML
  TextField ingredientQuantity;
  @FXML
  TextField servingsTextField;

  @FXML
  Label ingredientsResultsLabel;
  @FXML
  Label tagResultsLabel;

  @FXML
  private ComboBox<String> ingredientsComboBox;

  @FXML
  private ComboBox<String> tagSearchResults;

  @FXML
  VBox ingredientsVbox;
  @FXML
  VBox tagsVbox;
  @FXML
  HBox recipeIngredientsList;

  @FXML
  ImageView saveImage;

  @FXML
  ImageView deleteRecipeImage;

  HomeController homeController;

  @FXML
  VBox commentsContainer;

  @FXML 
  Stage confirmStage;

  @FXML
  void initialize() throws IOException, SQLException, ClassNotFoundException {
    Platform.runLater(() -> {
      Scene scene = recipeNameField.getScene();
      Parent root = scene.getRoot();
      homeController = (HomeController) root.getUserData();
      recipe = homeController.getCurrentRecipe();
      try {
        setRecipe(recipe, "Recipe View Page");
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    userHandler = UserHandler.getInstance();
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

  void setRecipe(Recipe recipe, String controllerType) throws IOException, ClassNotFoundException, SQLException {
    recipeImage.setImage(recipe.getImageAndPath().getImage());
    recipeNameField.setText(recipe.getName());
    servingsTextField.setText("" + recipe.getServings());
    this.currentMode = controllerType;
    if (currentMode == "Recipe View Page") {
      recipeDescriptionArea.setEditable(false);
      recipeInstructionsArea.setEditable(false);
      addIngredientVbox.setVisible(false);
      addIngredientVbox.setDisable(true);
      addIngredientVbox.setManaged(false);
      recipeNameField.setEditable(false);
      servingsTextField.setEditable(false);
      saveImage.setVisible(false);
      saveImage.setDisable(true);
      deleteRecipeImage.setVisible(false);
      deleteRecipeImage.setDisable(true);

      // Hide add ingredient
      ingredientsVbox.setDisable(true);
      ingredientsVbox.setVisible(false);
      ingredientsVbox.setManaged(false);
      recipeIngredientsList.setVisible(false);
      recipeIngredientsList.setManaged(false);

      // Hide add tags
      tagsVbox.setDisable(true);
      tagsVbox.setVisible(false);
      tagsVbox.setManaged(false);
      tagsContainer.setVisible(false);
      tagsContainer.setManaged(false);

      // Show ingredients list only if there are any.
      if (recipe.getIngredients().size() > 0) {
        recipeIngredientsList.setVisible(true);
        recipeIngredientsList.setManaged(true);
        ingredientsContainer.getChildren().clear();
        for (Ingredient i : recipe.getIngredients()) {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
          HBox ingredientHBox = loader.load();
          IngredientListItemController ingredientListItemController = loader.getController();
          ingredientListItemController.setIngredientData(i, currentMode, this);
          ingredientsContainer.getChildren().add(ingredientHBox);
        }
      }

      if (recipe.getTags().size() > 0) {
        tagsContainer.setVisible(true);
        tagsContainer.setManaged(true);
        tagsContainer.getChildren().clear();
        for (Tag tag : recipe.getTags()) {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/tagTemplate.fxml"));
          HBox tagHbox = loader.load();
          TagTemplateController tagTemplateController = loader.getController();
          tagTemplateController.setTagData(tag, currentMode, this);
          tagsContainer.getChildren().add(tagHbox);

        }
      }
      if (recipe.getCommentsList().size() > 0) {
        for (Comment comment : recipe.getCommentsList()) {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/commentTemplate.fxml"));
          HBox commentHBox = loader.load();
          CommentTemplateController commentTemplateController = loader.getController();
          commentTemplateController.setCommentData(comment, this);
          commentsContainer.getChildren().add(commentHBox);
        }
      }

    } else if (currentMode == "Recipe Edit Page") {
      recipeDescriptionArea.setEditable(true);
      recipeInstructionsArea.setEditable(true);
      addIngredientVbox.setVisible(true);
      addIngredientVbox.setDisable(false);
      addIngredientVbox.setManaged(true);
      recipeNameField.setEditable(true);
      servingsTextField.setEditable(true);
      recipeIngredientsList.setVisible(true);
      recipeIngredientsList.setManaged(true);
      ingredientsVbox.setDisable(false);
      ingredientsVbox.setVisible(true);
      ingredientsVbox.setManaged(true);
      tagsContainer.setVisible(true);
      tagsContainer.setManaged(true);
      tagsVbox.setDisable(false);
      tagsVbox.setVisible(true);
      tagsVbox.setManaged(true);

      saveImage.setVisible(true);
      saveImage.setDisable(false);
      deleteRecipeImage.setVisible(true);
      deleteRecipeImage.setDisable(false);

      // Show ingredients list only if there are any.
      recipeIngredientsList.setVisible(true);
      recipeIngredientsList.setManaged(true);
      ingredientsContainer.getChildren().clear();
      for (Ingredient i : recipe.getIngredients()) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        ingredientListItemController.setIngredientData(i, currentMode, this);
        ingredientsContainer.getChildren().add(ingredientHBox);
      }

      tagsContainer.setVisible(true);
      tagsContainer.setManaged(true);
      tagsContainer.getChildren().clear();
      for (Tag tag : recipe.getTags()) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/tagTemplate.fxml"));
        HBox tagHbox = loader.load();
        TagTemplateController tagTemplateController = loader.getController();
        tagTemplateController.setTagData(tag, currentMode, this);
        tagsContainer.getChildren().add(tagHbox);

      }
    }

    recipeDescriptionArea.setText(recipe.getDescription());
    recipeInstructionsArea.setText(recipe.getInstructions());

    try {
      if (RecipeHandler.getInstance().checkIfRecipeIsFavorite(recipe)) {
        favImageView.setImage(uiHandler.favImage);
      } else {
        favImageView.setImage(uiHandler.notFavImage);
      }
    } catch (SQLException e) {
      System.out.println(e);
    } catch (ClassNotFoundException e) {
      System.out.println(e);
    }

  }

  @FXML
  private void favoriteButtonClicked() throws SQLException, ClassNotFoundException {
    System.out.println("CLICK");
    boolean success = false;
    try {
      success = RecipeHandler.getInstance().setRecipeFavorite(recipe);

    } catch (SQLException e) {
      System.out.println(e);
      return;
    }

    if (success) {
      if (RecipeHandler.getInstance().checkIfRecipeIsFavorite(recipe)) {
        favImageView.setImage(uiHandler.favImage);
      } else {
        favImageView.setImage(uiHandler.notFavImage);
      }
    }

  }

  @FXML
  private void addNewIngredient() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
    HBox ingredientHBox = loader.load();
    IngredientListItemController ingredientListItemController = loader.getController();
    Ingredient emptyIngredient = new Ingredient("", "", -1, recipe.getId());
    ingredientListItemController.setIngredientData(emptyIngredient, currentMode, this);
    ingredientsContainer.getChildren().add(ingredientHBox);
    recipe.addIngredient(emptyIngredient);
  }

  @FXML
  private void addNewComment() throws SQLException, IOException, ClassNotFoundException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/commentTemplate.fxml"));
    HBox commentHBox = loader.load();
    CommentTemplateController commentTemplateController = loader.getController();
    Date date = new Date();
    Comment comment = new Comment(null, UserHandler.getInstance().currentUser.getUserId(),
        new Timestamp(date.getTime()), recipe.getId(), -1);
    commentTemplateController.setMode("Edit");
    commentTemplateController.setCommentData(comment, this);
    commentsContainer.getChildren().add(0, commentHBox);
    recipe.addComment(comment);

  }

  @FXML
  private void editRecipe() throws ClassNotFoundException, SQLException, IOException {
    int currentUserId = userHandler.currentUser.getUserId();
    int recipeUserId = recipe.getUserid();
    if (currentMode == "Recipe View Page") {
      if (currentUserId == recipeUserId || userHandler.currentUser.isAdmin()) {
        currentMode = "Recipe Edit Page";
        setRecipe(recipe, "Recipe Edit Page");
        uiHandler.setFeedbackText("Entering edit mode!", "success", feedbackText, feedbackImageView);
      } else if (currentUserId != recipeUserId) {
        uiHandler.setFeedbackText("Cannot edit a recipe you did not create!", "error", feedbackText, feedbackImageView);
      } else if (currentUserId != recipeUserId && userHandler.currentUser.isAdmin()) {
        currentMode = "Recipe Edit Page";
        setRecipe(recipe, "Recipe Edit Page");
        uiHandler.setFeedbackText("Entering edit mode as an admin!", "success", feedbackText, feedbackImageView);
      }
    } else if (currentMode == "Recipe Edit Page") {
      currentMode = "Recipe View Page";
      setRecipe(recipe, "Recipe View Page");
      uiHandler.setFeedbackText("Exiting edit mode!", "success", feedbackText, feedbackImageView);

    }
  }

  @FXML
  void addIngredientButtonClicked() throws IOException, ClassNotFoundException, SQLException {
    String name = ingredientNameField.getText();
    String quantity = ingredientQuantity.getText();
    String selectedIngName = ingredientsComboBox.getSelectionModel().getSelectedItem();
    Ingredient ingredient = null;

    if (name.isEmpty() && selectedIngName == null && quantity.isEmpty()) {
      uiHandler.setFeedbackText("Please enter an ingredient name before adding!", "error", feedbackText,
          feedbackImageView);
    } else {

      if (selectedIngName != null && !selectedIngName.isEmpty()) {
        name = ingredientsComboBox.getSelectionModel().getSelectedItem();
        ingredient = IngredientsHandler.getInstance().getIngredientFromRecipeIngredients(name);
        if (!quantity.isEmpty())
          ingredient.setQuantity(quantity);
        if (recipe.getIngredients().contains(ingredient)) {
          return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        recipe.addIngredient(ingredient);
        ingredientListItemController.setIngredientData(ingredient, "Create Recipe Controller", this);
        ingredientsContainer.getChildren().add(ingredientHBox);

      } else {
        Ingredient insertedIngredient = new Ingredient(name, quantity, -1, recipe.getId());
        int insertedIngredientId = IngredientsHandler.getInstance().insertIngredient(insertedIngredient);
        insertedIngredient = IngredientsHandler.getInstance().getIngredient(insertedIngredientId);
        System.out.println(insertedIngredient);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        if (!quantity.isEmpty())
          insertedIngredient.setQuantity(quantity);
        recipe.addIngredient(insertedIngredient);
        ingredientListItemController.setIngredientData(insertedIngredient, "Create Recipe Controller", this);
        ingredientsContainer.getChildren().add(ingredientHBox);

      }
    }
  }

  @FXML
  void saveRecipe() throws SQLException, ClassNotFoundException {
    String name = recipeNameField.getText();
    String description = recipeDescriptionArea.getText();
    String instructions = recipeInstructionsArea.getText();
    String servingsText = servingsTextField.getText();
    int servings = Integer.parseInt(servingsText);

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
      imageAndPath = recipe.getImageAndPath();
    }
    recipe = new Recipe(name, this.recipe.getIngredients(), this.recipe.getTags(), description, servings,
        this.recipe.getFavorite(),
        instructions,
        imageAndPath, this.recipe.getCommentsList(), this.recipe.getUserid(), this.recipe.getId());

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
        tagTemplateController.setTagData(tag, "Create Recipe Controller", this);
        tagsContainer.getChildren().add(tagHbox);
        recipe.addTag(tag);
        tag.setRecipeId(recipe.getId());
      } else {
        tag = new Tag(name, -1, -1);
        tag = TagsHandler.getInstance().insertOrGetTag(tag);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/tagTemplate.fxml"));
        HBox tagHbox = loader.load();
        TagTemplateController tagTemplateController = loader.getController();
        tagTemplateController.setTagData(tag, "Create Recipe Controller", this);
        tagsContainer.getChildren().add(tagHbox);
        recipe.addTag(tag);
        tag.setRecipeId(recipe.getId());
      }
      tagNameField.clear();
    }
  }

  @FXML
  private void deleteRecipeUI() throws ClassNotFoundException, SQLException, IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/confirmTemplate.fxml"));
    Parent root = loader.load();
    ConfirmController confirmController = loader.getController();
    confirmController.setup("Are you sure you want to delete this recipe?", this);
    confirmStage = new Stage();
    Scene scene = new Scene(root);
    confirmStage.setScene(scene);
    confirmStage.show();

  }

  private void deleteRecipe() throws ClassNotFoundException, SQLException
  {
    if (RecipeHandler.getInstance().deleteRecipe(recipe)) {
      homeController.loadPage("recommended");
    } else {
      uiHandler.setFeedbackText("Could not delete reciep!", "error", feedbackText, feedbackImageView);
    }
  }

  public void deleteTag(Tag tag) {
    recipe.removeTag(tag);
  }

  public void deleteIngredient(Ingredient ingredient) {
    recipe.removeIngredient(ingredient);
  }

  public void deleteComment(Comment comment) {
    recipe.removeComment(comment);
  }

  public void setFeedbackText(String text, String type) {
    uiHandler.setFeedbackText(text, type, feedbackText, feedbackImageView);
  }

  private QuantityUnitPair parseQuantityAndUnit(String ingredientQuantity) {
    Pattern pattern = Pattern.compile("^(\\d+(?:\\.\\d+)?)(.*)$");
    Matcher matcher = pattern.matcher(ingredientQuantity.trim());
    if (matcher.find()) {
      double quantity = Double.parseDouble(matcher.group(1));
      String unit = matcher.group(2).trim();
      return new QuantityUnitPair(quantity, unit);
    }
    return new QuantityUnitPair(1, ingredientQuantity.trim()); // default to 1 if parsing fails
  }

  private void adjustServings(int change) {
    try {
      int currentServings = Integer.parseInt(servingsTextField.getText());
      int newServings = currentServings + change;
      if (newServings > 0) {
        servingsTextField.setText(String.valueOf(newServings));
        updateIngredientQuantities(currentServings, newServings);
      } else {
        uiHandler.setFeedbackText("Servings must be a positive number!", "error", feedbackText, feedbackImageView);
      }
    } catch (NumberFormatException e) {
      uiHandler.setFeedbackText("Servings must be a number!", "error", feedbackText, feedbackImageView);
    }
  }

  private void updateIngredientQuantities(int oldServings, int newServings) {
    for (Ingredient ingredient : recipe.getIngredients()) {
      QuantityUnitPair parsed = parseQuantityAndUnit(ingredient.getQuantity());
      double newQuantity = parsed.quantity * newServings / oldServings;
      ingredient.setQuantity(formatQuantity(newQuantity) + " " + parsed.unit);
    }
    updateIngredientsListView();
  }

  private String formatQuantity(double quantity) {
    if (quantity == (long) quantity) {
      return String.format("%d", (long) quantity);
    } else {
      return String.format("%.2f", quantity);
    }
  }

  private void updateIngredientsListView() {
    ingredientsContainer.getChildren().clear();
    for (Ingredient ingredient : recipe.getIngredients()) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/ingredientListItemTemplate.fxml"));
      try {
        HBox ingredientHBox = loader.load();
        IngredientListItemController ingredientListItemController = loader.getController();
        ingredientListItemController.setIngredientData(ingredient, currentMode, this);
        ingredientsContainer.getChildren().add(ingredientHBox);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @FXML
  private void increaseServings() {
    adjustServings(1);
  }

  @FXML
  private void decreaseServings() {
    adjustServings(-1);
  }

  private class QuantityUnitPair {
    double quantity;
    String unit;

    QuantityUnitPair(double quantity, String unit) {
      this.quantity = quantity;
      this.unit = unit;
    }
  }

  public void ConfirmAction(String action, boolean b) throws ClassNotFoundException, SQLException {
    if(action == "Delete Recipe" && b == true)
    {
      deleteRecipe();
    }
    closeConfirmUI();
  }

  private void closeConfirmUI() {
    confirmStage.close();
  }

}
