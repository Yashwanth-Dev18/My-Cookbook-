package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import cookbook.Comment;
import cookbook.Database;
import cookbook.Recipe;

public class CommentsHandler {

  private static Database db;

  public static CommentsHandler instance;

  public static synchronized CommentsHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new CommentsHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public ArrayList<Comment> fetchComments(int recipeId) throws SQLException, ClassNotFoundException {
    ArrayList<Comment> commentsList = new ArrayList<>();
    ResultSet result = null;
    String query = "SELECT * FROM comments WHERE recipe_id = ? ORDER BY created_at DESC";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipeId);
      result = stmt.executeQuery();

      while (result.next()) {
        String commentText = result.getString("comment");
        int userId = result.getInt("user_id");
        int commentRecipeId = result.getInt("recipe_id");
        int commentId = result.getInt("comment_id");
        Timestamp createdAt = result.getTimestamp("created_at");

        Comment comment = new Comment(commentText, userId, createdAt, commentRecipeId, commentId);

        commentsList.add(comment);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return commentsList;
  }

  public boolean deleteComment(Comment comment) {
    boolean success = false;
    String query = "DELETE FROM comments WHERE comment_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, comment.getId());
      stmt.executeUpdate();
      success = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return success;
  }

  public boolean insertComment(Comment comment) {

    if (checkIfCommentExists(comment)) {
      return updateComment(comment);
    }
    boolean success = false;
    String query = "INSERT INTO comments (recipe_id, user_id, comment, created_at) VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, comment.getRecipeId());
      stmt.setInt(2, comment.getOwnerId());
      stmt.setString(3, comment.getContent());
      stmt.setTimestamp(4, comment.getCommentDate());
      stmt.executeUpdate();
      success = true;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return success;

  }

  public boolean checkIfCommentExists(Comment comment) {
    boolean exists = false;
    ResultSet result = null;
    String query = "SELECT * FROM comments WHERE comment_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, comment.getId());
      result = stmt.executeQuery();

      while (result.next()) {
        exists = true;

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return exists;
  }

  public boolean updateComment(Comment comment) {
    boolean success = false;
    String query = "UPDATE comments SET comment = ?, created_at = ? WHERE comment_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, comment.getContent());
      stmt.setTimestamp(2, comment.getCommentDate());
      stmt.setInt(3, comment.getId());
      stmt.executeUpdate();
      success = true;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return success;
  }

  public boolean deleteCommentsFromRecipe(Recipe recipe) {
    boolean success = false;
    String query = "DELETE FROM comments WHERE recipe_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, recipe.getId());
      stmt.executeUpdate();
      success = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return success;
  }
}
