package cookbook;
import java.util.ArrayList;


/**
 * RecipeList class is for the user to be able to add recipes to their own list.
 * The recipes come from the recipe class, which can be added to the user's own recipe list.

 * @author - Yashwanth Krishna Devanaboina
 */

public class RecipeList {

  /**
   * Creating an array list of type Recipe class consisting of its objects.
   */
  private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

  private String listName;
  private int userId;
  private int listId;
  /**
   * Public Constrcutor for instantiating an empty recipe list for the user.
   */
  public RecipeList(String listName, int userId, int listid, ArrayList<Recipe> recipes) {
    this.recipeList = recipes;
    setListName(listName);
    setUserId(userId);
    this.listId = listid;
  }

  /**
   * This method helps list all the existing recipeLists in the list.

   * @return - returns the list of recipes.
   */
  public ArrayList<Recipe> getRecipeList() {
    return recipeList;
  }

  public void clear()
  {
    this.recipeList.clear();
  }

  /**
   * This method is used to insert a recipe list into arraylist of recipeLists.

   * @param recipe - adding recipe
   */
  public void recipeInsertion(Recipe r) {
    recipeList.add(r);
  }

  /**
   * This method is used to delete existing recipe lists from the recipeLists.

   * @param recipe - removing recipe
   */
  public void deleteRecipe(Recipe r) {
    recipeList.remove(r);
  }

  // below are all setters and getters.
  private void setUserId(int userId) {
    this.userId = userId;
  }

  private void setListName(String listName) {
    this.listName = listName;
  }

  public int getUserId() {
    return this.userId;
  }

  public String getListName() {
    return this.listName;
  }

  public int getListId() {
    return this.listId;
  }

  public int getRecipeCount() {
    return this.recipeList.size();
  }

  public String getOwner() {
    return "";
  }

}
