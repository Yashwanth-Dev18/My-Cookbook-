package cookbook.handlers;

import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class UserInterfaceHandler {
  public static UserInterfaceHandler instance;

  public static synchronized UserInterfaceHandler getInstance() {
    if (instance == null) {
      instance = new UserInterfaceHandler();
    }
    return instance;
  }

  public Image successImage = ImageHandler
      .getImageFromRelativePath("/src/main/resources/images/UI_Icons/successIcon.png");

  public Image failedImage = ImageHandler
      .getImageFromRelativePath("/src/main/resources/images/UI_Icons/failedIcon.png");

  public Image favImage = ImageHandler.getImageFromRelativePath("/src/main/resources/images/UI_Icons/FilledHeart.png");
  public Image notFavImage = ImageHandler
      .getImageFromRelativePath("/src/main/resources/images/UI_Icons/EmptyHeart.png");

  public void setFeedbackText(String message, String type, Label feedbackText, ImageView feedbackImageView) {
    Image img;
    if (type == "error") {
      img = failedImage;
    } else {
      img = successImage;
    }

    feedbackText.setText(message);
    feedbackImageView.setImage(img);
  }

}
