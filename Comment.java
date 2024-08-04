package cookbook;

import java.sql.Timestamp;

public class Comment {

  private String content;
  private int ownerId;
  private Timestamp commentDate;
  private int recipeId;
  private int id;

  public Comment(String content, int ownerId, Timestamp commentDate, int recipeId, int id) {
    this.content = content;
    this.ownerId = ownerId;
    this.commentDate = commentDate;
    this.recipeId = recipeId;
    this.id = id;
  }

  public String getContent() {
    return this.content;
  }

  public int getOwnerId() {
    return this.ownerId;
  }

  public Timestamp getCommentDate() {
    return this.commentDate;
  }

  public int getId() {
    return this.id;
  }

  public int getRecipeId() {
    return this.recipeId;
  }

  public void setContent(String text) {
    this.content = text;
  }

}
