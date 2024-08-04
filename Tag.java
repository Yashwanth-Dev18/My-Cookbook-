package cookbook;

/**
 * Tag class for cookbook.
 * 
 * @author Steven Uhlemann.
 */
public class Tag {
  String name;
  int id;
  int recipeId;

  public Tag(String name, int id, int recipeId) {
    this.setName(name);
    this.setId(id);
    this.setRecipeId(recipeId);
  }

  /**
   * get name method for the tags.
   * 
   * @return the tag name.
   */
  public String getName() {
    return name;
  }

  /**
   * private set name for tag.
   * 
   * @param name the tag name.
   */
  private void setName(String name) {
    this.name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getRecipeId() {
    return this.recipeId;
  }

  public void setRecipeId(int recipeId) {
    this.recipeId = recipeId;
  }
}
