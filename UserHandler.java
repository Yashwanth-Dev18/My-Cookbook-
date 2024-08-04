package cookbook.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.Database;
import cookbook.User;
import javafx.scene.image.Image;

public class UserHandler {
  private static Database db;

  public static UserHandler instance;
  public User currentUser;

  public static synchronized UserHandler getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new UserHandler();
      db = Database.getInstance();
    }
    return instance;
  }

  public boolean updateUserPassword(User user, String newPassword) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "UPDATE users SET password = ? WHERE password = ? AND username = ?";

    try (PreparedStatement preparedStatement = db.connection.prepareStatement(query)) {
      preparedStatement.setString(1, newPassword);
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getUsername());

      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

      preparedStatement.close();
    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;
  }

  public boolean updateUsername(User user, String newUsername, String currentPassword)
      throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query;
    query = "UPDATE users SET username = ? WHERE username = ? AND password = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, newUsername);
      stmt.setString(2, user.getUsername());
      stmt.setString(3, currentPassword);

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;
  }

  public boolean doesUsernameExist(String username) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "SELECT COUNT(*) FROM users WHERE username = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {

      stmt.setString(1, username);
      ResultSet resultSet = stmt.executeQuery();
      resultSet.next();
      int count = resultSet.getInt(1);

      if (count > 0) {
        return true;
      }
    } catch (SQLException e) {
      System.out.println(e);
    }

    return success;
  }

  public boolean updateUserImage(User user, String imagePath) throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "UPDATE users SET profileImagePath = ? WHERE password = ? AND username = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, imagePath);
      stmt.setString(2, user.getPassword());
      stmt.setString(3, user.getUsername());

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

  public boolean login(String username, String password) throws SQLException, ClassNotFoundException {
    ResultSet loginResult = null;
    boolean success = false;
    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, username);
      stmt.setString(2, password);

      loginResult = stmt.executeQuery();

      while (loginResult.next()) {
        int userId = loginResult.getInt("user_id");
        boolean is_admin = loginResult.getBoolean("is_admin");
        String profileImagePath = loginResult.getString("profileImagePath");
        Image image = ImageHandler.getImageFromRelativePath(profileImagePath);
        currentUser = new User(username, password, userId, image, is_admin);

        success = true;
      }
      stmt.close();
    } catch (SQLException e) {
      System.out.println("Error login a user: " + e);
    }
    return success;

  }

  public void logout() {
    currentUser = null;
  }

  public boolean register(String username, String password, boolean isAdmin, String profileImagePath)
      throws SQLException, ClassNotFoundException {
    boolean success = false;
    String query = "INSERT INTO users (username, password, is_admin, profileImagePath) VALUES (?,?,?,?)";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setBoolean(3, isAdmin);
      stmt.setString(4, profileImagePath);

      stmt.executeUpdate();

      success = true;
    } catch (SQLException e) {
      System.out.println("Error register a user: " + e);
    }
    return success;

  }

  public ArrayList<User> getAllUsers(String searchUsername) throws SQLException {
    ArrayList<User> usersList = new ArrayList<>();
    ResultSet result = null;
    String query;
    if (!searchUsername.isEmpty()) {
      query = "SELECT * FROM users WHERE username LIKE ?";
    } else {
      query = "SELECT * FROM users";
    }

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      if (!searchUsername.isEmpty()) {
        stmt.setString(1, "%" + searchUsername + "%");
      }

      result = stmt.executeQuery();

      while (result.next()) {
        String username = result.getString("username");
        String password = result.getString("password");
        Boolean isAdmin = result.getBoolean("is_admin");
        String profileImagePath = result.getString("profileImagePath");
        int userId = result.getInt("user_id");

        Image profileImage = ImageHandler.getImageFromRelativePath(profileImagePath);

        User user = new User(username, password, userId, profileImage, isAdmin);

        usersList.add(user);
      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return usersList;

  }

  public boolean deleteUser(int user_id) throws SQLException {
    boolean success = false;

    removeUserComments(user_id);
    removeUserFavorites(user_id);
    success = removeUserRecord(user_id);

    return success;
  }

  public boolean removeUserFavorites(int user_id) throws SQLException {
    boolean success = false;

    String query = "DELETE FROM favorite_recipes WHERE user_id = ?";
    try (PreparedStatement preparedStatement = db.connection.prepareStatement(query)) {
      preparedStatement.setInt(1, user_id);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected > 0) {
        success = true;
      }

      preparedStatement.close();
    } catch (SQLException e) {
      System.out.println(e);
    }
    return success;
  }

  public boolean removeUserComments(int user_id) {
    boolean success = false;

    String query = "DELETE FROM comments WHERE user_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, user_id);
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

  public boolean removeUserRecord(int user_id) {
    boolean success = false;

    String query = "DELETE FROM users WHERE user_id = ?";
    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, user_id);
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

  public User getUser(int id) throws SQLException {
    User user = null;
    ResultSet result = null;
    String query;
    query = "SELECT * FROM users WHERE user_id = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setInt(1, id);
      result = stmt.executeQuery();

      while (result.next()) {
        String username = result.getString("username");
        String password = result.getString("password");
        Boolean isAdmin = result.getBoolean("is_admin");
        String profileImagePath = result.getString("profileImagePath");
        int userId = result.getInt("user_id");

        Image profileImage = ImageHandler.getImageFromRelativePath(profileImagePath);

        user = new User(username, password, userId, profileImage, isAdmin);

      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user;

  }

  public User getUser(String searchUsername) throws SQLException {
    User user = null;
    ResultSet result = null;
    String query;
    query = "SELECT * FROM users WHERE username = ?";

    try (PreparedStatement stmt = db.connection.prepareStatement(query)) {
      stmt.setString(1, searchUsername);
      result = stmt.executeQuery();

      while (result.next()) {
        String username = result.getString("username");
        String password = result.getString("password");
        Boolean isAdmin = result.getBoolean("is_admin");
        String profileImagePath = result.getString("profileImagePath");
        int userId = result.getInt("user_id");

        Image profileImage = ImageHandler.getImageFromRelativePath(profileImagePath);

        user = new User(username, password, userId, profileImage, isAdmin);

      }
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user;

  }

}
