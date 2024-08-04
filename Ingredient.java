package cookbook;

/**
 * Represents an ingredient in a recipe.
 */
public class Ingredient {
  private String name;
  private String quantity;
  private int ingredientId;
  private int recipeId;

  /**
   * Constructs an ingredient with a given name, quantity, and unit type.
   * 
   * @param name     Ingredient name.
   * @param quantity Ingredient quantity.
   */
  public Ingredient(String name, String quantity, int ingredientId, int recipeId) {
    this.setName(name);
    this.setQuantity(quantity);
    this.setId(ingredientId);
    this.setRecipeId(recipeId);
  }

  /**
   * Gets the ingredient name.
   * 
   * @return Ingredient name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the ingredient name.
   * 
   * @param name Ingredient name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the ingredient quantity.
   * 
   * @return Ingredient quantity.
   */
  public String getQuantity() {
    return quantity;
  }

  /**
   * Sets the ingredient quantity.
   * 
   * @param quantity Quantity of ingredient.
   */
  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  private void setId(int ingredientId) {
    this.ingredientId = ingredientId;
  }

  public int getId() {
    return this.ingredientId;
  }

  public String toString() {
    return this.name + ", " + this.quantity;
  }

  public int getRecipeId() {
    return this.recipeId;
  }

  private void setRecipeId(int recipeId) {
    this.recipeId = recipeId;
  }

}