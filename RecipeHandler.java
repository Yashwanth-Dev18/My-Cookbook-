package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cookbook.Comment;
import cookbook.Database;
import cookbook.Ingredient;
import cookbook.Recipe;
import cookbook.RecipeList;
import cookbook.Tag;
import cookbook.handlers.ImageHandler.ImageAndPath;

public class RecipeHandler {

  private static Database db;

  public static RecipeHandler instance;

  public static synchronized RecipeHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new RecipeHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public ArrayList<Recipe> getAllRecipes() throws SQLException, ClassNotFoundException {
    ArrayList<Recipe> recipesList = new ArrayList<>();
    String query = "SELECT * FROM recipes";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection

      // Execute query
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        // Retrieve recipe information
        int recipeId = result.getInt("recipe_id");
        String name = result.getString("name");
        String description = result.getString("description");
        String instructions = result.getString("instructions");
        int servings = result.getInt("servings");
        int userId = result.getInt("user_id");
        String imagePath = result.getString("image_path");

        ImageAndPath imageAndPath = ImageHandler.getImageAndPathFromRelativePath(imagePath);

        // Fetch ingredients for the current recipe
        ArrayList<Ingredient> ingredientsList = IngredientsHandler.getInstance().getAllIngredients(recipeId);

        // Fetch tags for the current recipe
        ArrayList<Tag> tagsList = TagsHandler.getInstance().getTagRecipeRelation(recipeId);

        // Fetch comments for the current recipe
        ArrayList<Comment> commentsList = CommentsHandler.getInstance().fetchComments(recipeId);

        // Save data as a recipe object.
        Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, false, instructions,
            imageAndPath, commentsList, userId, recipeId);

        if (checkIfRecipeIsFavorite(recipe)) {
          recipe.setFavorite(true);
        }
        recipesList.add(recipe);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching recipes: " + e);
    }
    return recipesList;
  }

  public ArrayList<Recipe> getAllRecipesFromUserId(int user_id) throws SQLException, ClassNotFoundException {
    ArrayList<Recipe> recipesList = new ArrayList<>();
    String query = "SELECT * FROM recipes WHERE user_id = ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setInt(1, user_id);
      // Execute query
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        // Retrieve recipe information
        int recipeId = result.getInt("recipe_id");
        String name = result.getString("name");
        String description = result.getString("description");
        String instructions = result.getString("instructions");
        int servings = result.getInt("servings");
        int userId = result.getInt("user_id");
        String imagePath = result.getString("image_path");

        ImageAndPath imageAndPath = ImageHandler.getImageAndPathFromRelativePath(imagePath);

        // Fetch ingredients for the current recipe
        ArrayList<Ingredient> ingredientsList = IngredientsHandler.getInstance().getAllIngredients(recipeId);

        // Fetch tags for the current recipe
        ArrayList<Tag> tagsList = TagsHandler.getInstance().getTagRecipeRelation(recipeId);

        // Fetch comments for the current recipe
        ArrayList<Comment> commentsList = CommentsHandler.getInstance().fetchComments(recipeId);

        // Save data as a recipe object.
        Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, false, instructions,
            imageAndPath, commentsList, userId, recipeId);

        if (checkIfRecipeIsFavorite(recipe)) {
          recipe.setFavorite(true);
        }
        recipesList.add(recipe);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching recipes: " + e);
    }
    return recipesList;
  }

  public ArrayList<Recipe> searchForRecipes(String searchText) throws SQLException, ClassNotFoundException {
    ArrayList<Recipe> recipesList = new ArrayList<>();
    String query = "SELECT * FROM recipes WHERE name LIKE ? OR description LIKE ? OR instructions LIKE ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection

      stmt.setString(1, "%" + searchText + "%");
      stmt.setString(2, "%" + searchText + "%");
      stmt.setString(3, "%" + searchText + "%");

      // Execute query
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        // Retrieve recipe information
        int recipeId = result.getInt("recipe_id");
        String name = result.getString("name");
        String description = result.getString("description");
        String instructions = result.getString("instructions");
        int servings = result.getInt("servings");
        int userId = result.getInt("user_id");
        String imagePath = result.getString("image_path");

        ImageAndPath imageAndPath = ImageHandler.getImageAndPathFromRelativePath(imagePath);

        // Fetch ingredients for the current recipe
        ArrayList<Ingredient> ingredientsList = IngredientsHandler.getInstance().getAllIngredients(recipeId);

        // Fetch tags for the current recipe
        ArrayList<Tag> tagsList = TagsHandler.getInstance().getTagRecipeRelation(recipeId);

        // Fetch comments for the current recipe
        ArrayList<Comment> commentsList = CommentsHandler.getInstance().fetchComments(recipeId);

        // Save data as a recipe object.
        Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, false, instructions,
            imageAndPath, commentsList, userId, recipeId);

        if (checkIfRecipeIsFavorite(recipe)) {
          recipe.setFavorite(true);
        }
        recipesList.add(recipe);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching recipes: " + e);
    }
    return recipesList;
  }

  // ::::::TRYING SOMETHING:::::
  // try {
  // // Database connection and query execution code
  // // Example: ResultSet resultSet = database.executeQuery("SELECT * FROM
  // recipes WHERE title LIKE '%" + searchText + "%'");

  // // If you found a matching recipe, create a Recipe object and return it
  // if (resultSet.next()) {
  // Recipe recipe = new Recipe();
  // // Populate the recipe object with data from the result set
  // // Example: recipe.setTitle(resultSet.getString("title"));
  // return recipe;
  // } else {
  // // If no matching recipe found, return null or throw an exception, depending
  // on your design
  // return null;
  // }
  // } catch (SQLException e) {
  // // Handle SQLException
  // throw e;
  // } catch (ClassNotFoundException e) {
  // // Handle ClassNotFoundException
  // throw e;
  // }
  // }

  public boolean saveRecipe(Recipe recipe) throws SQLException, ClassNotFoundException {

    try {
      // Save recipe details and get the recipe ID
      int generatedRecipeId = saveRecipeDetails(recipe);
      if (generatedRecipeId != -1) {
        // Set the generated ID in the Recipe object
        recipe.setId(generatedRecipeId);

        // Save tags and their relationships with the recipe
        for (Tag tag : recipe.getTags()) {
          tag.setRecipeId(generatedRecipeId);
          tag = TagsHandler.getInstance().insertOrGetTag(tag); // Insert tag and get its ID
          if (tag.getId() != -1) {
            saveRecipeTagRelationship(generatedRecipeId, tag.getId());
          }
        }

        // Save ingredients and their relationships with the recipe
        for (Ingredient ingredient : recipe.getIngredients()) {
          int ingredientId = IngredientsHandler.getInstance().insertIngredient(ingredient); // Insert ingredient and get
                                                                                            // its ID
          if (ingredientId != -1) {
            saveRecipeIngredientRelationship(generatedRecipeId, ingredientId, ingredient.getQuantity());
          }
        }
      }
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean saveRecipeTagRelationship(int recipeId, int tagId) throws SQLException {
    boolean success = false;
    String query = "INSERT INTO recipetags (recipe_id, tag_id) VALUES (?, ?)";

    if (checkIfTagRecipeRelationExists(recipeId, tagId)) {
      return true;
    }

    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setInt(1, recipeId);
      statement.setInt(2, tagId);
      statement.executeUpdate();
      success = true;
    } catch (SQLException e) {
      System.err.println("Error saving recipe-tag relationship: " + e.getMessage());
      throw e;
    }
    return success;
  }

  public boolean checkIfTagRecipeRelationExists(int recipeId, int tagId) throws SQLException {
    boolean success = false;
    ResultSet result = null;
    String query = "SELECT * FROM recipetags WHERE recipe_id = ? AND tag_id = ?";
    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setInt(1, recipeId);
      statement.setInt(2, tagId);
      result = statement.executeQuery();
      if (result.next()) {
        success = true;
      }
    } catch (SQLException e) {
      System.err.println("Error saving recipe-tag relationship: " + e.getMessage());
      throw e;
    }
    return success;

  }

  public boolean saveRecipeIngredientRelationship(int recipeId, int ingredientId, String quantity) throws SQLException {
    boolean success = false;
    String query = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)";
    
    if (checkIfRecipeIngredientRelationExists(recipeId, ingredientId, quantity)) {
      return true;
    }

    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setInt(1, recipeId);
      statement.setInt(2, ingredientId);
      statement.setString(3, quantity);
      statement.executeUpdate();
      success = true;
    } catch (SQLException e) {
      System.err.println("Error saving recipe-ingredient relationship: " + e.getMessage());
    }
    return success;
  }

  public boolean checkIfRecipeIngredientRelationExists(int recipeId, int ingredientId, String quantity) {
    boolean success = false;
    ResultSet result = null;
    String query = "SELECT * FROM recipe_ingredients WHERE recipe_id = ? AND ingredient_id = ? AND quantity = ?";
    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setInt(1, recipeId);
      statement.setInt(2, ingredientId);
      statement.setString(3, quantity);
      result = statement.executeQuery();
      if (result.next()) {
        success = true;
      }
    } catch (SQLException e) {
      System.err.println("Error saving recipe-ingredient relationship: " + e.getMessage());
    }
    return success;

  }

  public int saveRecipeDetails(Recipe recipe) throws SQLException, ClassNotFoundException {
    if (checkIfRecipeExists(recipe) != -1) {
      if (updateRecipe(recipe)) {
        return recipe.getId();
      }
    }
    String query = "INSERT INTO recipes (name, description, instructions, servings, user_id, image_path) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = db.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, recipe.getName());
      statement.setString(2, recipe.getDescription());
      statement.setString(3, recipe.getInstructions());
      statement.setInt(4, recipe.getServings());
      statement.setInt(5, UserHandler.getInstance().currentUser.getUserId());
      statement.setString(6, recipe.getImageAndPath().getRelativePath());
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        throw new SQLException("Inserting recipe failed, no rows affected.");
      }
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1); // Return generated recipe ID
        } else {
          throw new SQLException("Inserting recipe failed, no ID obtained.");
        }
      }
    }
  }

  private boolean updateRecipe(Recipe recipe) throws SQLException {
    boolean success = false;
    String query = "UPDATE recipes SET name = ?, description = ?, instructions = ?, servings = ?, image_path = ? WHERE recipe_id = ?";
    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setString(1, recipe.getName());
      statement.setString(2, recipe.getDescription());
      statement.setString(3, recipe.getInstructions());
      statement.setInt(4, recipe.getServings());
      statement.setString(5, recipe.getImageAndPath().getRelativePath());
      statement.setInt(6, recipe.getId());
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        throw new SQLException("Updating recipe failed, no rows affected.");
      } else {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
      throw e;
    }
    return success;
  }

  private int checkIfRecipeExists(Recipe recipe) throws SQLException {
    String query = "SELECT * from recipes WHERE recipe_id = ?";
    ResultSet result = null;
    int recipeId = -1;

    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setInt(1, recipe.getId());
      result = statement.executeQuery();

      if (result.next()) {
        recipeId = result.getInt("recipe_id");
      }

    } catch (SQLException e) {
      System.out.println(e);
      throw e;
    }
    return recipeId;
  }

  public boolean favoriteRecipe(Recipe recipe) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "INSERT INTO favorite_recipes (user_id, recipe_id) VALUES (?, ?)";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, UserHandler.getInstance().currentUser.getUserId());
      stmt.setInt(2, recipe.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

      stmt.close();
    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;

  }

  public boolean unFavoriteRecipe(Recipe recipe) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "DELETE FROM favorite_recipes WHERE user_id = ? AND recipe_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, UserHandler.getInstance().currentUser.getUserId());
      stmt.setInt(2, recipe.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

      stmt.close();
    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;

  }

  public boolean setRecipeFavorite(Recipe recipe) throws SQLException, ClassNotFoundException {
    if (checkIfRecipeIsFavorite(recipe)) {
      return unFavoriteRecipe(recipe);
    } else {
      return favoriteRecipe(recipe);
    }
  }

  public boolean checkIfRecipeIsFavorite(Recipe recipe) throws SQLException, ClassNotFoundException {
    boolean isFavorite = false;
    String query = "SELECT * FROM favorite_recipes WHERE recipe_id = ? AND user_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.setInt(2, UserHandler.getInstance().currentUser.getUserId());

      ResultSet results = stmt.executeQuery();

      if (results.next()) {
        if (results.getInt("recipe_id") == recipe.getId()
            && results.getInt("user_id") == UserHandler.getInstance().currentUser.getUserId()) {
          isFavorite = true;
        }
      }

      stmt.close();
    } catch (SQLException e) {
      System.out.println(e);
    }

    return isFavorite;

  }

  public ArrayList<Recipe> getAllFavoriteRecipes(int user_id) throws SQLException, ClassNotFoundException {
    ArrayList<Recipe> recipesList = new ArrayList<>();
    String query = "SELECT * FROM favorite_recipes fr INNER JOIN recipes r ON fr.recipe_id = r.recipe_id WHERE fr.user_id = ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, user_id);
      // Execute query
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        // Retrieve recipe information
        int recipeId = result.getInt("recipe_id");
        String name = result.getString("name");
        String description = result.getString("description");
        String instructions = result.getString("instructions");
        int servings = result.getInt("servings");
        int userId = result.getInt("user_id");
        String imagePath = result.getString("image_path");

        ImageAndPath imageAndPath = ImageHandler.getImageAndPathFromRelativePath(imagePath);

        // Fetch ingredients for the current recipe
        ArrayList<Ingredient> ingredientsList = IngredientsHandler.getInstance().getAllIngredients(recipeId);

        // Fetch tags for the current recipe
        ArrayList<Tag> tagsList = TagsHandler.getInstance().getTagRecipeRelation(recipeId);

        // Fetch comments for the current recipe
        ArrayList<Comment> commentsList = CommentsHandler.getInstance().fetchComments(recipeId);

        // Save data as a recipe object.
        Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, true, instructions,
            imageAndPath, commentsList, userId, recipeId);
        recipesList.add(recipe);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching recipes: " + e);
    }
    return recipesList;

  }

  public Recipe getRecipe(int id) throws ClassNotFoundException {
    String query = "SELECT * FROM recipes WHERE recipe_id = ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection

      // Execute query
      stmt.setInt(1, id);
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        // Retrieve recipe information
        int recipeId = result.getInt("recipe_id");
        String name = result.getString("name");
        String description = result.getString("description");
        String instructions = result.getString("instructions");
        int servings = result.getInt("servings");
        int userId = result.getInt("user_id");
        String imagePath = result.getString("image_path");

        ImageAndPath imageAndPath = ImageHandler.getImageAndPathFromRelativePath(imagePath);

        // Fetch ingredients for the current recipe
        ArrayList<Ingredient> ingredientsList = IngredientsHandler.getInstance().getAllIngredients(recipeId);

        // Fetch tags for the current recipe
        ArrayList<Tag> tagsList = TagsHandler.getInstance().getTagRecipeRelation(recipeId);

        // Fetch comments for the current recipe
        ArrayList<Comment> commentsList = CommentsHandler.getInstance().fetchComments(recipeId);

        // Save data as a recipe object.
        Recipe recipe = new Recipe(name, ingredientsList, tagsList, description, servings, false, instructions,
            imageAndPath, commentsList, userId, recipeId);

        if (checkIfRecipeIsFavorite(recipe)) {
          recipe.setFavorite(true);
        }
        return recipe;
      }
    } catch (SQLException e) {
      System.out.println("Error fetching recipes: " + e);
    }
    return null;

  }

  public boolean deleteRecipe(Recipe recipe) throws ClassNotFoundException, SQLException {
    for (Ingredient ingredient : recipe.getIngredients()) {
      IngredientsHandler.getInstance().deleteIngredientFromRecipe(ingredient);
    }
    for (Tag tag: recipe.getTags())
    {
      TagsHandler.getInstance().deleteTagRecipeRelation(tag);
    }
    unFavoriteRecipe(recipe);
    RecipeListsHandler.getInstance().deleteRecipeFromRecipeList(recipe);
    CommentsHandler.getInstance().deleteCommentsFromRecipe(recipe);
    boolean success = false;
    String query = "DELETE FROM recipes WHERE user_id = ? AND recipe_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getUserid());
      stmt.setInt(2, recipe.getId());
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 0) {
        success = true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return success;

  }

  public boolean deleteRecipesFromRecipeList(Recipe recipe, RecipeList recipeList) {
    boolean success = false;
    String query = "DELETE FROM recipe_in_recipe_list WHERE recipe_id = ? AND list_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.setInt(2, recipeList.getListId());
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 0) {
        success = true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return success;
  }

}
