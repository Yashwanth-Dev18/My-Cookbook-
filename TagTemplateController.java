package cookbook.controller;

import java.sql.SQLException;

import cookbook.Tag;
import cookbook.handlers.TagsHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TagTemplateController {

  public Tag currentTag;

  @FXML
  Label tagName;
  @FXML
  HBox tagTemplate;

  @FXML
  private ImageView deleteImage;

  private String currentController;

  RecipeViewPageController recipeViewPageController = null;

  public void setTagData(Tag tag, String controllerName, RecipeViewPageController recipeViewPageController) {
    currentController = controllerName;

    this.recipeViewPageController = recipeViewPageController;

    if(currentController == "Recipe View Page")
    {
      deleteImage.setDisable(true);
      deleteImage.setVisible(false);
      deleteImage.setManaged(false);
    }
    else if(currentController == "Recipe Edit Page")
    {
      deleteImage.setDisable(false);
      deleteImage.setVisible(true);
      deleteImage.setManaged(true);
    }
    this.currentTag = tag;
    tagName.setText(tag.getName());

  }

  @FXML
  private void deleteTag() throws SQLException, ClassNotFoundException {
    if (currentTag.getId() != -1) {
      if(currentController == "Recipe Edit Page")
      {
        if(TagsHandler.getInstance().deleteTagRecipeRelation(currentTag))
        {
          HBox parentPane = (HBox) tagTemplate.getParent();
          parentPane.getChildren().remove(tagTemplate);
          if(this.recipeViewPageController != null)
          {
            recipeViewPageController.deleteTag(currentTag);
          }
        }
      }
      else
      {
        HBox parentPane = (HBox) tagTemplate.getParent();
        parentPane.getChildren().remove(tagTemplate);  
      }

    } else {
      System.out.println("TAG WITHOUT ID");
      HBox parentPane = (HBox) tagTemplate.getParent();
      parentPane.getChildren().remove(tagTemplate);

    }

  }
}
