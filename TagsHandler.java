package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cookbook.Database;
import cookbook.Tag;

public class TagsHandler {
  private static Database db;

  public static TagsHandler instance;

  public static synchronized TagsHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new TagsHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public ArrayList<Tag> getAllTags() throws ClassNotFoundException, SQLException {
    ArrayList<Tag> tagList = new ArrayList<>();
    String query = "SELECT * FROM tags";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      result = stmt.executeQuery();
      while (result.next()) {
        String name = result.getString("name");
        int tagId = result.getInt("tag_id");
        Tag tag = new Tag(name, tagId, -1);
        tagList.add(tag);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
    return tagList;

  }

  public ArrayList<Tag> searchForTags(String tagName) {
    ArrayList<Tag> tagList = new ArrayList<>();
    String query = "SELECT * FROM tags WHERE name LIKE ?";
    ResultSet result = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, "%" + tagName + "%");
      result = stmt.executeQuery();
      while (result.next()) {
        String name = result.getString("name");
        int tagId = result.getInt("tag_id");
        Tag tag = new Tag(name, tagId, -1);
        tagList.add(tag);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tagList;
  }

  public Tag insertTag(Tag tag) throws SQLException {
    String query;
    query = "INSERT INTO tags (name) VALUES (?)";
    try (PreparedStatement statement = db.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, tag.getName());
      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Inserting tag failed, no rows affected.");
      }
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          tag.setId(generatedKeys.getInt(1));
          return tag;
        }
      }
    }
    return tag;
  }

  public ArrayList<Tag> getTagRecipeRelation(int recipeId) throws SQLException, ClassNotFoundException {
    ArrayList<Tag> tagsList = new ArrayList<>();
    ResultSet result = null;
    String query = "SELECT * FROM tags t JOIN recipetags rt ON t.tag_id = rt.tag_id WHERE rt.recipe_id = ?";
  

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipeId);
      result = stmt.executeQuery();

      while (result.next()) {
        String tagName = result.getString("name");
        int tagId = result.getInt("tag_id");
        Tag tag = new Tag(tagName, tagId, recipeId);
        tagsList.add(tag);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tagsList;
  }

  public boolean updateTag(Tag tag) {
    boolean success = false;
    String newName = tag.getName();
    int tagId = tag.getId();
    String query = "UPDATE tags SET name = ? WHERE tag_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, newName);
      stmt.setInt(2, tagId);
      stmt.executeUpdate();
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;

  }

  public Tag insertOrGetTag(Tag tag) throws SQLException {
    ResultSet result = null;
    if (!checkIfTagExists(tag)) {
      return insertTag(tag);
    } else {
      String query = "SELECT * FROM tags where name = ?";
      try (PreparedStatement statement = db.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, tag.getName());
        result = statement.executeQuery();

        if (result.next()) {
          int id = result.getInt("tag_id");
          tag.setId(id);
        }
      }
    }
    return tag;

  }

  public Tag getTag(int id) throws SQLException {
    String query = "SELECT * FROM tags t INNER JOIN recipetags rt ON tag_id = rt.tag_id WHERE t.tag_id = ?";

    ResultSet result = null;
    Tag tag = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setInt(1, id);
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        String name = result.getString("name");
        int recipe_id = result.getInt("recipe_id");
        tag = new Tag(name, id, recipe_id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tag;

  }

  public Tag getTag(String tagName) throws SQLException {
    String query = "SELECT * FROM tags where name = ?";

    ResultSet result = null;
    Tag tag = null;

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      // Establish connection
      stmt.setString(1, tagName);
      result = stmt.executeQuery();

      // Process results
      while (result.next()) {
        String name = result.getString("name");
        int tagId = result.getInt("tag_id");
        tag = new Tag(name, tagId, -1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tag;

  }

  public boolean deleteTagRecipeRelation(Tag tag) {
    int tagId = tag.getId();
    boolean success = false;

    String query = "DELETE FROM recipetags WHERE recipe_id = ? AND tag_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, tag.getRecipeId());
      stmt.setInt(2, tagId);
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
    }
    return success;

  }

  public boolean checkIfTagExists(Tag tag) {
    boolean success = false;
    ResultSet result = null;
    String query = "SELECT * FROM tags WHERE name = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, tag.getName());
      result = stmt.executeQuery();
      if (result.next()) {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;

  }

}
