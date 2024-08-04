package cookbook;

import cookbook.handlers.RecipeListsHandler;

import java.sql.SQLException;
import java.util.ArrayList;

public class RecipeListManager {
  private ArrayList<RecipeList> recipeLists;

  
  public RecipeListManager() throws SQLException, ClassNotFoundException {
    recipeLists = RecipeListsHandler.getInstance().getAllLists();
  }
  public void insertList(RecipeList rl) {
    recipeLists.add(rl);
  }

  public void deleteList(RecipeList rl) {
    recipeLists.remove(rl);
  }

public ArrayList<RecipeList> getAllLists() {
  return this.recipeLists;
}
}
