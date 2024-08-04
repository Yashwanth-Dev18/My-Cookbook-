package cookbook;

import javafx.scene.image.Image;

public class User {
  private int userId;
  private String username;
  private String password;
  private boolean isAdmin = false;
  private Image profileImage;

  public User(String username, String password, int userId, Image profileImage, boolean isAdmin) {
    this.setUsername(username);
    this.setPassword(password);
    this.setUserId(userId);
    this.setImage(profileImage);
    this.setAdmin(isAdmin);
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public int getUserId() {
    return userId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public void setImage(Image profileImage) {
    this.profileImage = profileImage;
  }

  public Image getImage() {
    return this.profileImage;
  }

}
