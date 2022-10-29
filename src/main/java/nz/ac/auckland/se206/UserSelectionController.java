package nz.ac.auckland.se206;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
  @FXML private Circle circlePaint;
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
  @FXML private ColorPicker colourPick;
  @FXML private Slider sliderThick;
  protected ImageView currentImageView;
  protected Text currentNameLabel;
  private GraphicsContext graphic;
  private Color penColour = Color.BLACK;
  private double thickness;
  private Stack<Image> drawingStack = new Stack<>();

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we initialize the
   * drawing canvas and set up all the local user profiles saved
   */
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

    // canvas implementation for user profile pic drawing
    canvasUser.setOnMousePressed(
        e -> {
          graphic.setLineWidth(thickness);
          if (!toggleEraser.isSelected()) { // pen implementation
            graphic.setStroke(penColour);
            graphic.beginPath();
            graphic.lineTo(e.getX(), e.getY());

          } else if (toggleEraser.isSelected()) { // eraser implementation
            double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
          saveStroke();
        });

    // event listener of mouse drag for smoother pen
    canvasUser.setOnMouseDragged(
        e -> {
          if (!toggleEraser.isSelected()) {
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
          if (!toggleEraser.isSelected()) {
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

  /**
   * This method is called when user starts game by pressing their image or guest button
   *
   * @param event ActionEvent of the node to get scene
   */
  @FXML
  private void onStartCanvas(Event event) {
    Node node = (Node) event.getSource();
    Stage stage = (Stage) node.getScene().getWindow();
    String id =
        node.getId().equals("btnGuest")
            ? node.getParent().getId()
            : node.getParent().getParent().getId();
    // checks for parent of event source
    UserProfile.currentUser = getProfileById(id);
    UserProfile user = users[UserProfile.currentUser];

    App.gameSelectionInstance.setDifToggles();
    App.gameSelectionInstance.setEditMode(UserProfile.currentUser);

    stage.setUserData(user);
    Scene sceneOfNode = node.getScene();

    sceneOfNode.setRoot(
        SceneManager.getUiRoot(
            SceneManager.AppUi.GAME_SELECTION)); // switch to currentUser's canvas
  }

  /**
   * This method is called when a user profile has not yet been made and pressing the plus icon
   * toggles the user creation pane
   *
   * @param event ActionEvent of the node to get scene
   */
  @FXML
  private void onCreateProfile(Event event) {
    String id = ((Node) event.getSource()).getParent().getId();
    UserProfile.currentUser = getProfileById(id);

    changeToUserCreation();
  }

  /** This method hides the user selection pane and brings up the user creation pane */
  private void changeToUserCreation() {
    paneUserProfile.setVisible(false);
    paneUserCreation.setVisible(true);
  }

  /**
   * This method is called when user wants to edit their profile, so it toggles the user creation
   * pane and their name and profile image will be set in the GUI controls
   */
  protected void onEditMode() {
    changeToUserCreation();

    textFieldName.setText(users[UserProfile.currentUser].getName());
    canvasUser
        .getGraphicsContext2D()
        .drawImage(users[UserProfile.currentUser].getImageView().getImage(), 0, 0);
  }

  /**
   * This method grabs the ID of the current user by their string ID when user selects a user
   * profile. It also sets the current image and name of the current user
   *
   * @param id string of their pane ID
   * @return an int of their ID
   */
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

  /**
   * This method is called when user presses the save profile button and stores the profile locally
   * and in game Their image and username will also display
   *
   * @throws IOException when an error occurs by saving the image in an incorrect pathed file
   */
  @FXML
  private void onSaveProfile() throws IOException {
    String name = textFieldName.getText();
    // checks if a username has been input
    if (name.strip().equals("")) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Username Error");
      alert.setGraphic(new ImageView(new Image("./images/usernameError.png")));
      alert.setHeaderText(" ");
      alert.showAndWait();
      textFieldName.setStyle("-fx-border-color: red");
    } else {
      // saves the user name and profile image to the UserProfile variable
      users[UserProfile.currentUser].setName(name);
      users[UserProfile.currentUser].setImageView(currentImageView);
      // saves the profile image to local files
      saveProfilePic();
      displayProfilePic(UserProfile.currentUser);
      displayName(UserProfile.currentUser);
      users[UserProfile.currentUser].saveUserData(); // save user created into local file
      clearUserCreation();
    }
  }

  /**
   * This method is called to save the user's profile image locally into a file
   *
   * @throws IOException when an error occurs by saving the image in an incorrect pathed file
   */
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

  /**
   * This method displays the userprofile image onto their respective user pane
   *
   * @param currentUser ID of the current user
   */
  private void displayProfilePic(int currentUser) {
    UserProfile user = users[currentUser];
    user.getImageView().setImage(user.getProfilePic()); // set imageView to the user's image
    currentImageView.getParent().setVisible(true);
  }

  /**
   * This method displays the user profile name onto their respective user pane
   *
   * @param currentUser ID of the current user
   */
  private void displayName(int currentUser) {
    UserProfile user = users[currentUser];
    currentNameLabel.setText(user.getName());
  }

  /**
   * This method takes a snapshot of the canvas when user saves their profile and returns it as an
   * image
   *
   * @return a bufferedimage of the canvas snapshot
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvasUser.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /** This method clears the drawing canvas */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvasUser.getWidth(), canvasUser.getHeight());
  }

  /**
   * This method is called when user goes back instead of saving, and resets the user creation pane
   */
  @FXML
  private void onBackUserCreation() {
    clearUserCreation();
  }

  /** This method is called when user selects the pen tool icon and notifies them about it */
  @FXML
  private void onPenSelected() {
    circleEraser.setOpacity(1);
    circlePen.setOpacity(0.5);
    togglePen.setSelected(true);
  }

  /** This method is called when user selections the eraser tool icon and notifies them about it */
  @FXML
  private void onEraserSelected() {
    circlePen.setOpacity(1);
    circleEraser.setOpacity(0.5);
    toggleEraser.setSelected(true);
  }

  /**
   * This method is called when user leaves the user creation pane back to the user selection pane,
   * so it resets the initial view of the user creation pane
   */
  private void clearUserCreation() {
    onClear(); // clear canvas
    textFieldName.clear();
    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
    textFieldName.setStyle("-fx-border-color: transparent");
    circlePaint.setFill(Color.WHITE);
    onPenSelected();
    thickness = 8;
    sliderThick.setValue(8);
    penColour = Color.BLACK;
    drawingStack.clear();
  }

  /**
   * This method is called when the user wants to change colour of their pen by clicking the paint
   * brush icon. A nice gradient change on the brush icon is also shown
   */
  @FXML
  private void onChangeColour() {
    // colour selected by user
    Color colour = colourPick.getValue();
    penColour = colour;
    LinearGradient paint =
        new LinearGradient(
            0.0,
            1.0,
            1.0,
            0.076,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0.255, colour),
            new Stop(1.0, new Color(1.0, 1.0, 1.0, 1.0)));
    circlePaint.setFill(paint);
    // turns pen function on with the new colour
    onPenSelected();
  }

  /** This method changes the thickness of the pen and eraser tool via a slider */
  @FXML
  private void onSliderReleased() {
    thickness = sliderThick.getValue();
  }

  /** This method toggles the music button either turning it on or off */
  @FXML
  private void onToggleMusic() {
    MainMenuController.toggleMusic();
  }

  /** This method saves the current canvas drawing by taking a snapshot and adding it to a stack */
  private void saveStroke() {
    drawingStack.add(canvasUser.snapshot(null, null));
  }

  /**
   * This method is called when user wants to undo their drawing by popping the stack and then draw
   * the popped image
   */
  @FXML
  private void onUndo() {
    if (!drawingStack.isEmpty()) {
      canvasUser.getGraphicsContext2D().drawImage(drawingStack.pop(), 0, 0);
    }
  }
}
