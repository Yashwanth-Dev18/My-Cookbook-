package cookbook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Cookbook app.
 */
public class App extends Application {

  public Stage primaryStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Load the FXML file

    this.primaryStage = primaryStage;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cookbook/fxml/login.fxml"));
    Parent root = loader.load();

    // Set up the scene
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Register Page.");
    // Disable resizing
    primaryStage.setResizable(false);

    // Disable fullscreen mode
    primaryStage.setFullScreen(false);
    primaryStage.show();
    Font.loadFont(getClass().getResource("/fonts/SignPainter-HouseScript.ttf").toExternalForm(), 12);


  }

  public static void main(String[] args) throws SQLException{
    Application.launch(args);
  }
}

