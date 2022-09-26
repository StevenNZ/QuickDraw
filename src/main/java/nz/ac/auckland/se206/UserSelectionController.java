package nz.ac.auckland.se206;

import java.awt.Graphics2D;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  public static final UserProfile[] users = new UserProfile[7];
  @FXML private Pane paneUserProfile;
  @FXML private Pane paneUserCreation;
  @FXML private TextField textFieldName;
  @FXML private Canvas canvasUser;
  @FXML private Circle circleEraser;
  @FXML private Circle circlePen;
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
  @FXML private ToggleButton togglePen;
  @FXML private ToggleButton toggleEraser;
  private ImageView currentImageView;
  private Text currentNameLabel;
  private GraphicsContext graphic;

  @FXML
  public void initialize() {
    users[0] = new UserProfile("Guest"); // creates an initial instance of user guest

    // Check whether user Profiles already exist

    for (int i = 1; i < 7; i++) { // loops through the 6 preexisting user profile panes
      UserProfile.currentUser = i;
      users[i] = new UserProfile();
      if (!users[i].getName().equals("")) { // initially displays preexisting user data
        getProfileById("paneNewUser" + i);
        users[i].setImageView(currentImageView);
        displayProfilePic(i);
        displayName(i);
      }
    }

    graphic = canvasUser.getGraphicsContext2D();
    graphic.setLineWidth(8);
    graphic.setLineCap(StrokeLineCap.ROUND);
    circlePen.setOpacity(0.5);

    canvasUser.setOnMousePressed( // canvas implementation for user profile pic drawing
        e -> {
          if (togglePen.isSelected()) { // pen implementation
            graphic.setStroke(Color.BLACK);
            graphic.beginPath();
            graphic.lineTo(e.getX(), e.getY());

          } else if (toggleEraser.isSelected()) { // eraser implementation
            double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });
    canvasUser.setOnMouseDragged( // event listener of mouse drag for smoother pen
        e -> {
          if (togglePen.isSelected()) {
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
          } else if (toggleEraser.isSelected()) {
            double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });

    canvasUser.setOnMouseReleased(
        e -> {
          if (togglePen.isSelected()) {
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
            graphic.closePath();
          } else if (toggleEraser.isSelected()) {
            double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });
  }

  @FXML
  private void onStartCanvas(Event event) {
    String id =
        ((Node) event.getSource()).getId().equals("btnGuest")
            ? ((Node) event.getSource()).getParent().getId()
            : ((Node) event.getSource()).getParent().getParent().getId();
    // checks for parent of event source
    UserProfile.currentUser = getProfileById(id);
    Node node = (Node) event.getSource();
    Scene sceneOfNode = node.getScene();
    sceneOfNode.setRoot(
        SceneManager.getUiRoot(
            getNewRoot(UserProfile.currentUser))); // switch to currentUser's canvas
  }

  private SceneManager.AppUi getNewRoot(int id) {
    switch (id) { // returns the canvas for corresponding ID
      case 1:
        return SceneManager.AppUi.CANVAS_PLAYER1;
      case 2:
        return SceneManager.AppUi.CANVAS_PLAYER2;
      case 3:
        return SceneManager.AppUi.CANVAS_PLAYER3;
      case 4:
        return SceneManager.AppUi.CANVAS_PLAYER4;
      case 5:
        return SceneManager.AppUi.CANVAS_PLAYER5;
      case 6:
        return SceneManager.AppUi.CANVAS_PLAYER6;

      default:
        return SceneManager.AppUi.CANVAS; // defaults to guest canvas
    }
  }

  @FXML
  private void onCreateProfile(Event event) {
    String id = ((Node) event.getSource()).getParent().getId();
    UserProfile.currentUser = getProfileById(id);

    paneUserProfile.setVisible(false);
    paneUserCreation.setVisible(true);
  }

  private int getProfileById(String id) {

    switch (id) { // returns current user index as well as it's imageView and label
      case "paneNewUser1": // ID of the parent pane
        currentImageView = imageUser1; // sets current image view of current player 1
        currentNameLabel = txtPlayer1; // sets current text label of current player 1
        return 1; // current user index 1 returned
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
        return 0; // defaults guest
    }
  }

  @FXML
  private void onSaveProfile() throws IOException {
    String name = textFieldName.getText();
    users[UserProfile.currentUser].setName(name);
    users[UserProfile.currentUser].setImageView(currentImageView);
    saveProfilePic();
    displayProfilePic(UserProfile.currentUser);
    displayName(UserProfile.currentUser);
    users[UserProfile.currentUser].saveUserData(); // save user created into local file
    clearUserCreation();
  }

  private void saveProfilePic() throws IOException {
    File profileFolder = new File(".profiles");

    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File profilePicFile = new File(".profiles/user" + UserProfile.currentUser + "image.png");

    try {
      ImageIO.write(getCurrentSnapshot(), "png", profilePicFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Image profilePicImage =
        new Image(new FileInputStream(profilePicFile)); // gets image of the file of drawing
    users[UserProfile.currentUser].setProfilePic(
        profilePicImage); // stores image as an instance variable
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
  private void onClear() {
    graphic.clearRect(0, 0, canvasUser.getWidth(), canvasUser.getHeight());
  }

  @FXML
  private void onBackUserCreation() {
    clearUserCreation();
  }

  @FXML
  private void onPenSelected() {
    circleEraser.setOpacity(1);
    circlePen.setOpacity(0.5);
  }

  @FXML
  private void onEraserSelected() {
    circlePen.setOpacity(1);
    circleEraser.setOpacity(0.5);
  }

  private void clearUserCreation() {
    onClear(); // clear canvas
    textFieldName.clear();
    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
  }
}
