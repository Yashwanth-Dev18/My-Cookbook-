package cookbook;

import java.util.ArrayList;

import cookbook.handlers.ImageHandler.ImageAndPath;

/**
 * recipe class for cookbook.
 * 
 * @author Steven Uhlemann.
 */
public class Recipe {
  private String name;
  private ArrayList<Ingredient> ingredientList;
  private ArrayList<Tag> recipeTags;
  private String description;
  private int recipeId;
  private String instructions;
  private Boolean isFavorite;
  private int servings;
  private int userId;
  private ImageAndPath imageAndPath;
  private ArrayList<Comment> commentsList;

  private void setCommentsList(ArrayList<Comment> commentsList) {
    this.commentsList = commentsList;
  }

  public ArrayList<Comment> getCommentsList() {
    return this.commentsList;
  }

  /**
   * A constructor used in the create recipe page.
   */
  public Recipe(String name, ArrayList<Ingredient> ingredientsList, ArrayList<Tag> tagsList, String description,
      int servings, boolean is_favorite, String instructions, ImageAndPath ImageAndPath,
      ArrayList<Comment> commentsList, int user_id, int recipeId) {
    this.setIngredients(ingredientsList);
    this.setInstructions(instructions);
    this.setTags(tagsList);
    this.setServings(servings);
    this.setName(name);
    this.setDescription(description);
    this.setFavorite(is_favorite);
    this.setImageAndPath(ImageAndPath);
    this.setCommentsList(commentsList);
    this.setUserid(user_id);
    this.setId(recipeId);
  }

  private void setImageAndPath(ImageAndPath imageAndPath) {
    this.imageAndPath = imageAndPath;
  }

  public ImageAndPath getImageAndPath() {
    return this.imageAndPath;
  }

  /**
   * get name.
   * 
   * @return the name.
   */
  public String getName() {
    return name;
  }

  public int getServings() {
    return this.servings;
  }

  /**
   * get ID.
   * 
   * @return the ID.
   */
  public int getId() {
    return recipeId;
  }

  /**
   * the list of the ingredients.
   * 
   * @return the list.
   */
  public ArrayList<Ingredient> getIngredients() {
    return ingredientList;
  }

  /**
   * the list for the tags.
   * 
   * @return the tags.
   */
  public ArrayList<Tag> getTags() {
    return recipeTags;
  }

  /**
   * the recipe description.
   * 
   * @return the description.
   */
  public String getDescription() {
    return description;
  }

  public Boolean getFavorite() {
    return isFavorite;
  }

  public String getInstructions() {
    return instructions;
  }

  public int getUserid() {
    return userId;
  }

  /**
   * private set name for recipe.
   * 
   * @param newName the recipe name.
   */
  private void setName(String newName) {
    this.name = newName;
  }

  public void setId(int id) {
    this.recipeId = id;
  }

  /**
   * private set ingredients for recipe.
   * 
   * @param ingredients the ingredients.
   */
  private void setIngredients(ArrayList<Ingredient> ingredients) {
    this.ingredientList = ingredients;
  }

  /**
   * private set tags for recipe.
   * 
   * @param tags the tags.
   */
  private void setTags(ArrayList<Tag> tags) {
    this.recipeTags = tags;
  }

  /**
   * private set description.
   * 
   * @param description the description.
   */
  private void setDescription(String description) {
    this.description = description;
  }

  private void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public void setFavorite(Boolean is_favorite) {
    this.isFavorite = is_favorite;
  }

  private void setUserid(int user_id) {
    this.userId = user_id;
  }

  private void setServings(int servings) {
    this.servings = servings;
  }

  public void addIngredient(Ingredient i) {
    this.ingredientList.add(i);
  }
  public void addTag(Tag t)
  {
    this.recipeTags.add(t);
  }
  public void removeIngredient(Ingredient i)
  {
    this.ingredientList.remove(i);
  }
  public void removeTag(Tag t)
  {
    this.recipeTags.remove(t);
  }

  public void addComment(Comment comment) {
    this.commentsList.add(comment);
  }
  public void removeComment(Comment comment)
  {
    this.commentsList.remove(comment);
  }


}
