package cookbook.handlers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Handles scene switching.
 */
public class SceneHandler {

  /**
   * Switches to a scene depending on fxmlFileName and the event.
   * 
   * @param event        actionEvent (Button onClick probably)
   * @param fxmlFileName the fxml file name.
   */
  public static void switchToScene(Stage stage, String fxmlFileName) {
    try {
      // Load the FXML file for the specified scene
      FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource("/cookbook/fxml/"
          + fxmlFileName + ".fxml"));
      Parent root = loader.load();

      // Create a new scene with the specified scene root
      Scene scene = new Scene(root);

      // Create a fade transition
      FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5),
          stage.getScene().getRoot());
      fadeOut.setFromValue(1);
      fadeOut.setToValue(0);
      fadeOut.setOnFinished(actionEvent -> {
        // Set the new scene after the fade out animation completes
        stage.setScene(scene);
        stage.setTitle(fxmlFileName.substring(0, 1).toUpperCase()
            + fxmlFileName.substring(1) + " Page");

        // Create a fade in transition for the new scene
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1),
            stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
      });

      // Start the fade out transitionl
      fadeOut.play();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
