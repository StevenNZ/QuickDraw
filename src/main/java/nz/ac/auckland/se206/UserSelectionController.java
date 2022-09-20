package nz.ac.auckland.se206;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.imageio.ImageIO;
import javax.swing.*;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  @FXML private Pane paneUserProfile;
  @FXML private Pane paneUserCreation;
  @FXML private TextField textFieldName;
  @FXML private Canvas canvasUser;
  @FXML private Circle circleEraser;
  @FXML private Circle circleBlackPen1;
  private GraphicsContext graphic;
  private static final UserProfile[] users = new UserProfile[7];
  public static int currentUser = 0;

  @FXML
  public void initialize() {
    users[0] = new UserProfile("Guest");
    // this is where we show locally stored user profiles
  }

  @FXML
  private void onStartCanvas(ActionEvent event) {
    System.out.println("canvas" + currentUser);
    System.out.println(Arrays.toString(users));

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onCreateProfile(Event event) {
    currentUser = getProfile(event);
    circleBlackPen1.setOpacity(0.5);
    graphic = canvasUser.getGraphicsContext2D();

    canvasUser.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.fillOval(x, y, size, size);
        });

    paneUserProfile.setVisible(false);
    paneUserCreation.setVisible(true);
  }

  private int getProfile(Event event) {
    String id = ((Node) event.getSource()).getId();

    switch (id) {
      case "circleNewUser1":
        return 1;
      case "circleNewUser2":
        return 2;
      case "circleNewUser3":
        return 3;
      case "circleNewUser4":
        return 4;
      case "circleNewUser5":
        return 5;
      case "circleNewUser6":
        return 6;
      default:
        return 0;
    }
  }

  @FXML
  private void onSaveProfile() throws IOException {
    String name = textFieldName.getText();
    users[currentUser] = new UserProfile(name);
    setProfilePic();

    currentUser = 0;
    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
  }

  private void setProfilePic() throws IOException {
    File file = new File("src/main/resources/images/userprofilepic/user" + currentUser + ".png");
    try {
      ImageIO.write(getCurrentSnapshot(), "png", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvasUser.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  @FXML
  private void onBlackSelected() {
    circleEraser.setOpacity(1);
    circleBlackPen1.setOpacity(0.5);

    canvasUser.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.fillOval(x, y, size, size);
        });
  }

  @FXML
  private void onEraserSelected() {
    circleEraser.setOpacity(0.5);
    circleBlackPen1.setOpacity(1);

    canvasUser.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.clearRect(x, y, size, size);
        });
  }

  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvasUser.getWidth(), canvasUser.getHeight());
  }
}
