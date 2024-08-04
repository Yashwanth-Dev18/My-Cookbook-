package cookbook.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import cookbook.handlers.ImageHandler;
import cookbook.handlers.SceneHandler;
import cookbook.handlers.UserHandler;

public class WelcomeController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    Label welcomeText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            imageView.setImage(ImageHandler.getImageFromRelativePath("/src/main/resources/images/chefHat2.png"));
            imageView.setOpacity(0);
            imageView.setScaleX(0.1);
            imageView.setScaleY(0.1);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), imageView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(2), imageView);
            scaleUp.setFromX(0.1);
            scaleUp.setFromY(0.1);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);

            fadeIn.play();
            scaleUp.play();
        });

        try {
          welcomeText.setText("Welcome " + UserHandler.getInstance().currentUser.getUsername() + "!" + " Click anywhere to continue!");
        } catch (ClassNotFoundException | SQLException e) {
          e.printStackTrace();
        }
    }

    @FXML
    private void goToHome(){
        Stage stage = (Stage) imageView.getScene().getWindow();
        SceneHandler.switchToScene(stage, "home");

    }
}
