package nz.ac.auckland.se206;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  @FXML private Pane paneUserProfile;
  @FXML private Pane paneUserCreation;
  @FXML private TextField textFieldName;
  @FXML private Canvas canvasUser;
  @FXML private Circle circleEraser;
  @FXML private Circle circleBlackPen1;
  @FXML private ImageView imageUser1;
  @FXML private ImageView imageUser2;
  @FXML private ImageView imageUser3;
  @FXML private ImageView imageUser4;
  @FXML private ImageView imageUser5;
  @FXML private ImageView imageUser6;
  @FXML private Text txtPlayer1;
  @FXML private Text txtPlayer2;
  @FXML private Text txtPlayer3;
  @FXML private Text txtPlayer4;
  @FXML private Text txtPlayer5;
  @FXML private Text txtPlayer6;

  private ImageView currentImageView;
  private Text currentNameLabel;
  private GraphicsContext graphic;
  private static final UserProfile[] users = new UserProfile[7];

  @FXML
  public void initialize() {
    users[0] = new UserProfile("Guest");
    // this is where we show locally stored user profiles
  }

  @FXML
  private void onStartCanvas(Event event) {
    String id =
        ((Node) event.getSource()).getId().equals("btnGuest")
            ? ((Node) event.getSource()).getParent().getId()
            : ((Node) event.getSource()).getParent().getParent().getId();

    UserProfile.currentUser = getProfileById(id);
    Node node = (Node) event.getSource();
    Scene sceneOfNode = node.getScene();
    sceneOfNode.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onCreateProfile(Event event) {
    String id = ((Node) event.getSource()).getParent().getId();
    UserProfile.currentUser = getProfileById(id);
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

  private int getProfileById(String id) {

    switch (id) {
      case "paneNewUser1":
        currentImageView = imageUser1;
        currentNameLabel = txtPlayer1;
        return 1;
      case "paneNewUser2":
        currentImageView = imageUser2;
        currentNameLabel = txtPlayer2;
        return 2;
      case "paneNewUser3":
        currentImageView = imageUser3;
        currentNameLabel = txtPlayer3;
        return 3;
      case "paneNewUser4":
        currentImageView = imageUser4;
        currentNameLabel = txtPlayer4;
        return 4;
      case "paneNewUser5":
        currentImageView = imageUser5;
        currentNameLabel = txtPlayer5;
        return 5;
      case "paneNewUser6":
        currentImageView = imageUser6;
        currentNameLabel = txtPlayer6;
        return 6;
      default:
        return 0;
    }
  }

  @FXML
  private void onSaveProfile() throws IOException {
    String name = textFieldName.getText();
    users[UserProfile.currentUser] = new UserProfile(name);
    users[UserProfile.currentUser].setImageView(currentImageView);
    saveProfilePic();

    clearUserCreation();
  }

  private void saveProfilePic() throws IOException {
    File profilePicFile =
        new File(
            "src/main/resources/images/userprofilepic/user" + UserProfile.currentUser + ".png");

    try {
      ImageIO.write(getCurrentSnapshot(), "png", profilePicFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Image profilePicImage =
        new Image(new FileInputStream(profilePicFile)); // gets image of the file of drawing
    users[UserProfile.currentUser].setProfilePic(
        profilePicImage); // stores image as an instance variable
    displayProfilePic(UserProfile.currentUser);
    displayName(UserProfile.currentUser);
  }

  private void displayProfilePic(int currentUser) {
    UserProfile user = users[currentUser];
    user.getImageView().setImage(user.getProfilePic()); // set imageView to the user's image
    currentImageView.getParent().setVisible(true);
  }

  private void displayName(int currentUser) {
    UserProfile user = users[currentUser];
    currentNameLabel.setText(user.getName());
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

  @FXML
  private void onBackUserCreation() {
    clearUserCreation();
  }

  private void clearUserCreation() {
    onClear(); // clear canvas
    textFieldName.clear();
    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
  }
}
