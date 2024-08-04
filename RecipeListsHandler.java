package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.Database;
import cookbook.Recipe;
import cookbook.RecipeList;

public class RecipeListsHandler {
  private static Database db;

  public static RecipeListsHandler instance;

  public static synchronized RecipeListsHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new RecipeListsHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public ArrayList<RecipeList> getAllLists() throws SQLException, ClassNotFoundException {
    ArrayList<RecipeList> lists = new ArrayList<>();
    String query = "SELECT * FROM recipe_list";
    ResultSet recipelistsResult = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      recipelistsResult = stmt.executeQuery();
      while (recipelistsResult.next()) {
        String Name = recipelistsResult.getString("list_name");
        int id = recipelistsResult.getInt("id");
        int owner = recipelistsResult.getInt("owner");
        RecipeList recipeList = new RecipeList(Name, owner, id, null);

        recipeList = new RecipeList(Name, owner, id, getAllRecipesFromList(recipeList));
        lists.add(recipeList);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return lists;

  }

  public boolean addRecipeList(String name, int ownerId) {
    String query = "INSERT INTO recipe_list (list_name,owner) VALUES (?,?)";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, name);
      stmt.setInt(2, ownerId);
      stmt.executeUpdate();

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean removeRecipeFromList(Recipe recipe) {
    String query = "DELETE FROM recipe_in_recipe_list where recipe_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.executeUpdate();

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<Recipe> getAllRecipesFromList(RecipeList recipeList) throws ClassNotFoundException {
    ArrayList<Recipe> recipes = new ArrayList<>();
    String query = "SELECT * FROM recipe_in_recipe_list WHERE list_id = ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipeList.getListId());
      // Establish connection
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        recipes.add(RecipeHandler.getInstance().getRecipe(result.getInt("recipe_id")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return recipes;
  }

  public boolean addRecipeToList(Recipe recipe, int listId) {
    if(checkIfRecipeExistsInList(recipe, listId))
    {
      return true;
    }
    boolean success = false;
    String query = "INSERT INTO recipe_in_recipe_list (recipe_id, list_id) VALUES (?,?)";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.setInt(2, listId);
      stmt.executeUpdate();

      success = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return success;
  }

  public boolean checkIfRecipeExistsInList(Recipe recipe, int listId)
  {
    boolean success = false;
    ResultSet result = null;
    String query = "SELECT * FROM recipe_in_recipe_list WHERE recipe_id = ? AND list_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.setInt(2, listId);
      result = stmt.executeQuery();

      if(result.next())
      {
        success = true;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return success;
  }

  public boolean deleteList(RecipeList recipeList) {
    for (Recipe r : recipeList.getRecipeList()) {
      deleteRecipesFromRecipeList(r, recipeList);
    }
    boolean success = false;
    String query = "DELETE FROM recipe_list WHERE owner = ? AND id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipeList.getUserId());
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
  public boolean deleteRecipeFromRecipeList(Recipe recipe) {
    boolean success = false;
    String query = "DELETE FROM recipe_in_recipe_list WHERE recipe_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
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
