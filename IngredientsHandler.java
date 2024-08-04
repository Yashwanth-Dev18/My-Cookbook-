package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cookbook.Database;
import cookbook.Ingredient;

public class IngredientsHandler {
  private static Database db;

  public static IngredientsHandler instance;

  public static synchronized IngredientsHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new IngredientsHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public int insertIngredient(Ingredient ingredient) throws SQLException, ClassNotFoundException {
    // Check if the ingredient already exists
    if (checkIfIngredientExists(ingredient) != -1) {
      return checkIfIngredientExists(ingredient);
    }

    // Ingredient doesn't exist, insert it
    String query = "INSERT INTO ingredients (name) VALUES (?)";
    try (PreparedStatement statement = db.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, ingredient.getName());
      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Inserting ingredient failed, no rows affected.");
      }
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1); // Return generated ingredient ID
        } else {
          throw new SQLException("Inserting ingredient failed, no ID obtained.");
        }
      }
    }
  }

  public int insertOrGetIngredient(Ingredient ingredient) throws SQLException, ClassNotFoundException {
    // Check if the ingredient already exists
    if (checkIfIngredientExists(ingredient) != -1) {
      // If ingredient exists and is not -1 (Does not exist) return its ID.
      return checkIfIngredientExists(ingredient);
    } else {

      // Ingredient doesn't exist, insert it
      String query = "INSERT INTO ingredients (name) VALUES (?)";
      try (PreparedStatement statement = db.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, ingredient.getName());
        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
          throw new SQLException("Inserting ingredient failed, no rows affected.");
        }
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
          } else {
            throw new SQLException("Inserting ingredient failed, no ID obtained.");
          }
        }
      }
    }

  }

  public Ingredient getIngredient(int ingredientId) throws SQLException {
    String query = "SELECT * FROM ingredients i INNER JOIN recipe_ingredients ri ON i.ingredient_id = ri.ingredient_id WHERE i.ingredient_id = ?";
    ResultSet ingredientsResult = null;
    Ingredient ingredient = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setInt(1, ingredientId);
      ingredientsResult = stmt.executeQuery();

      // Process results
      while (ingredientsResult.next()) {
        String ingredientName = ingredientsResult.getString("name");
        String quantity = ingredientsResult.getString("quantity");
        int recipe_id = ingredientsResult.getInt("recipe_id");
        ingredient = new Ingredient(ingredientName, quantity, ingredientId, recipe_id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
    if (ingredient == null) {
      ingredient = getIngredientWithoutRecipe(ingredientId);
    }
    return ingredient;

  }

  public Ingredient getIngredientWithoutRecipe(int ingredientId) throws SQLException {
    String query = "SELECT * FROM ingredients WHERE ingredient_id = ?";
    ResultSet ingredientsResult = null;
    Ingredient ingredient = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setInt(1, ingredientId);
      ingredientsResult = stmt.executeQuery();

      // Process results
      while (ingredientsResult.next()) {
        String ingredientName = ingredientsResult.getString("name");
        ingredient = new Ingredient(ingredientName, "", ingredientId, -1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
    return ingredient;

  }

  public Ingredient getIngredientWithoutRecipe(String ingredientName) throws SQLException {
    String query = "SELECT * FROM ingredients WHERE name = ?";
    ResultSet ingredientsResult = null;
    Ingredient ingredient = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setString(1, ingredientName);
      ingredientsResult = stmt.executeQuery();

      // Process results
      while (ingredientsResult.next()) {
        int ingredientId = ingredientsResult.getInt("ingredient_id");
        ingredient = new Ingredient(ingredientName, "", ingredientId, -1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
    return ingredient;

  }

  public Ingredient getIngredientFromRecipeIngredients(String ingredientName) throws SQLException {
    String query = "SELECT * FROM ingredients i INNER JOIN recipe_ingredients ri ON i.ingredient_id = ri.ingredient_id WHERE i.name = ?";
    ResultSet result = null;
    Ingredient ingredient = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setString(1, ingredientName);
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        String quantity = result.getString("quantity");
        int recipe_id = result.getInt("recipe_id");
        int ingredientId = result.getInt("ingredient_id");
        ingredient = new Ingredient(ingredientName, quantity, ingredientId, recipe_id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (ingredient == null) {
      ingredient = getIngredientWithoutRecipe(ingredientName);
    }

    return ingredient;

  }

  public int checkIfIngredientExists(Ingredient ingredient) throws SQLException {
    // Check if the ingredient already exists
    String query = "SELECT ingredient_id FROM ingredients WHERE name = ?";
    try (PreparedStatement statement = db.connection.prepareStatement(query)) {
      statement.setString(1, ingredient.getName());
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          // Ingredient already exists, return its ID
          return resultSet.getInt("ingredient_id");
        } else {
          return -1;
        }
      }
    }

  }

  public ArrayList<Ingredient> getAllIngredients(int recipe_id) throws ClassNotFoundException, SQLException {
    ArrayList<Ingredient> ingredientsList = new ArrayList<>();
    String query = "SELECT * FROM ingredients i INNER JOIN recipe_ingredients ri ON i.ingredient_id = ri.ingredient_id WHERE ri.recipe_id = ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe_id);
      result = stmt.executeQuery();

      while (result.next()) {
        String ingredientName = result.getString("name");
        String quantity = result.getString("quantity");
        int ingredientId = result.getInt("ingredient_id");
        Ingredient ingredient = new Ingredient(ingredientName, quantity, ingredientId, recipe_id);
        ingredientsList.add(ingredient);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }

    return ingredientsList;
  }

  public ArrayList<Ingredient> searchForIngredients(String ingredientName) throws ClassNotFoundException, SQLException {
    ArrayList<Ingredient> ingredientsList = new ArrayList<>();
    String query = "SELECT * FROM ingredients WHERE name LIKE ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, "%" + ingredientName + "%");
      result = stmt.executeQuery();

      while (result.next()) {
        int ingredientId = result.getInt("ingredient_id");
        String name = result.getString("name");
        Ingredient ingredient = new Ingredient(name, "", ingredientId, -1);
        ingredientsList.add(ingredient);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ingredientsList;
  }

  public ArrayList<Ingredient> getAllIngredients() throws ClassNotFoundException, SQLException {
    ArrayList<Ingredient> ingredientsList = new ArrayList<>();
    String query = "SELECT * FROM ingredients";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      result = stmt.executeQuery();
      while (result.next()) {
        String name = result.getString("name");
        int ingredientId = result.getInt("ingredient_id");

        // Quantity is zero here because its used before creating a recipe.
        Ingredient ingredient = new Ingredient(name, "", ingredientId, -1);
        ingredientsList.add(ingredient);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }

    return ingredientsList;

  }

  public boolean updateIngredient(Ingredient ingredient) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String newName = ingredient.getName();
    int ingredientId = ingredient.getId();
    String query = "UPDATE ingredients SET name = ? WHERE ingredient_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, newName);
      stmt.setInt(2, ingredientId);
      stmt.executeUpdate();
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

      stmt.close();
    } catch (SQLException e) {
      System.out.println(e);
    }

    if (success) {
      updateIngredientQuantity(ingredient);
    }
    return success;

  }

  public boolean updateIngredientQuantity(Ingredient ingredient) throws SQLException, ClassNotFoundException {
    String newQuantity = ingredient.getQuantity();
    int ingredientId = ingredient.getId();
    boolean success = false;

    String query = "UPDATE recipe_ingredients SET quantity = ? WHERE ingredient_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, newQuantity);
      stmt.setInt(2, ingredientId);
      stmt.executeUpdate();
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

  public boolean deleteIngredientFromRecipe(Ingredient ingredient) throws SQLException {
    boolean success = false;

    String query = "DELETE FROM recipe_ingredients WHERE recipe_id = ? AND ingredient_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, ingredient.getRecipeId());
      stmt.setInt(2, ingredient.getId());
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
    }
    return success;

  }

}
