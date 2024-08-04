package cookbook.handlers;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * The class responsible for choosing an image. It can be used for using recipe
 * Images or user profile Images.
 */
public class ImageHandler {

  public static ImageAndPath chooseImage(String title, String initialDirectory, String relativePath, Stage stage) {
    createFolderIfNotExists(relativePath);
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialDirectory(new File(initialDirectory));
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg"));
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      try {
        return copyImage(selectedFile, relativePath);
      } catch (IOException e) {
        System.err.println("Error copying image: " + e.getMessage());
      }
    }
    return null;
  }

  public static Image getImageFromRelativePath(String relativePath) {
    String fullPath = Paths.get("").toAbsolutePath().toString().replace("\\", "/") + relativePath;

    try {
      File file = new File(fullPath);
      if (file.exists()) {
        return new Image("file:" + fullPath);
      } else {
        System.err.println("Image file does not exist: " + fullPath);
        return null;
      }
    } catch (Exception e) {
      System.err.println("Error loading image: " + e.getMessage());
      return null;
    }
  }

  public static ImageAndPath getImageAndPathFromRelativePath(String relativePath) {
    String fullPath = Paths.get("").toAbsolutePath().toString().replace("\\", "/") + relativePath;

    try {
      File file = new File(fullPath);
      if (file.exists()) {
        Image image = new Image("file:" + fullPath);
        return new ImageAndPath(image, relativePath);
      } else {
        System.err.println("Image file does not exist: " + fullPath);
        return null;
      }
    } catch (Exception e) {
      System.err.println("Error loading image: " + e.getMessage());
      return null;
    }
  }

  private static void createFolderIfNotExists(String folderPath) {
    Path path = Paths.get(folderPath);
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        System.err.println("Error creating folder: " + e.getMessage());
      }
    }
  }

  private static ImageAndPath copyImage(File selectedFile, String relativePath) throws IOException {
    String imageName = selectedFile.getName();
    String fullPath = relativePath + imageName;
    Path source = selectedFile.toPath();
    Path target = Paths.get(fullPath);
    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

    // Construct the full path
    String fullImagePath = "file:" + Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/" + fullPath;

    return new ImageAndPath(new Image(fullImagePath), fullPath);
  }

  public static class ImageAndPath {
    private Image image;
    private String relativePath;

    public ImageAndPath(Image image, String relativePath) {
      this.image = image;
      this.relativePath = relativePath;
    }

    public Image getImage() {
      return image;
    }

    public String getRelativePath() {
      return "/" + relativePath;
    }
  }
}
