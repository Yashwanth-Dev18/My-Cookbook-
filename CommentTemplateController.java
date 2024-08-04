package cookbook.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import cookbook.Comment;
import cookbook.User;
import cookbook.handlers.CommentsHandler;
import cookbook.handlers.UserHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CommentTemplateController {

  @FXML
  HBox editModeHbox;

  @FXML
  private Button addCommentButton;

  @FXML
  private Button deleteCommentButton;

  @FXML
  private TextArea commentArea;

  @FXML
  private ImageView editImage;

  @FXML
  private ImageView userImage;

  @FXML
  private Label userInfo;

  @FXML
  HBox commentTemplate;

  private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  RecipeViewPageController recipeViewPageController;
  Comment currentComment;

  String currentMode = "View";


  @FXML
  void addComment(ActionEvent event) throws ClassNotFoundException, SQLException {
    if (commentArea.getText().isEmpty()) {
      recipeViewPageController.setFeedbackText("Cannot add an empty comment!", "error");
    }
    else
    {
      currentComment.setContent(commentArea.getText());
      if(CommentsHandler.getInstance().insertComment(currentComment))
      {
        recipeViewPageController.setFeedbackText("Comment saved successfully!", "success");
      }
      else
      {
        recipeViewPageController.setFeedbackText("Comment could not be saved!", "error");

      }
    }
  }

  void setCommentData(Comment comment, RecipeViewPageController recipeViewPageController) throws ClassNotFoundException, SQLException {
    this.recipeViewPageController = recipeViewPageController;
    this.currentComment = comment;

    User commentOwner = UserHandler.getInstance().getUser(comment.getOwnerId());
    User currentUser = UserHandler.getInstance().currentUser;

    if(currentMode == "View")
    {
      addCommentButton.setVisible(false);
      addCommentButton.setDisable(true);
      addCommentButton.setManaged(false);
      deleteCommentButton.setVisible(false);
      deleteCommentButton.setDisable(true);
      deleteCommentButton.setManaged(false);

      commentArea.setEditable(false);
    }
    if(!commentOwner.getUsername().equals(currentUser.getUsername()))
    {
      commentArea.setEditable(false);
      addCommentButton.setVisible(false);
      addCommentButton.setDisable(true);
      addCommentButton.setManaged(false);
      editImage.setVisible(false);
      editImage.setDisable(true);
      editImage.setManaged(false);
    }
    if(comment.getContent() != null)
    {
      commentArea.setText(comment.getContent());
    }
    userImage.setImage(commentOwner.getImage());
    if(commentOwner.getUsername().equals(currentUser.getUsername()))
    {
      userInfo.setText("You" + " @ " + sdf3.format(comment.getCommentDate()));
    }
    else
    {
      userInfo.setText(commentOwner.getUsername() + " @ " + sdf3.format(comment.getCommentDate()));

    }

  }

  @FXML
  private void editImagePress()
  {
    if(currentMode == "View")
    {
      addCommentButton.setVisible(true);
      addCommentButton.setDisable(false);
      addCommentButton.setManaged(true);
      deleteCommentButton.setVisible(true);
      deleteCommentButton.setDisable(false);
      deleteCommentButton.setManaged(true);

      commentArea.setEditable(true);
      currentMode = "Edit";
    }
    else if(currentMode == "Edit")
    {
      addCommentButton.setVisible(false);
      addCommentButton.setDisable(true);
      addCommentButton.setManaged(false);
      deleteCommentButton.setVisible(false);
      deleteCommentButton.setDisable(true);
      deleteCommentButton.setManaged(false);

      commentArea.setEditable(false);
      currentMode = "View";
    }
  }

  @FXML
  private void deleteComment() throws ClassNotFoundException, SQLException
  {
    if(CommentsHandler.getInstance().deleteComment(currentComment))
    {
      recipeViewPageController.setFeedbackText("Comment successfully deleted!", "success");
      VBox commentsContainer = (VBox) commentTemplate.getParent();
      commentsContainer.getChildren().remove(commentTemplate);
      recipeViewPageController.deleteComment(currentComment);
    }
    else
    {
      recipeViewPageController.setFeedbackText("Could not delete comment!", "error");
    }
  }

  public void setMode(String mode) {
    this.currentMode = mode;
  }

}
