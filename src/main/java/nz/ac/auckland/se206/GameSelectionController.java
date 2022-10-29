package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.user.UserFileHandler;
import nz.ac.auckland.se206.user.UserProfile;
import nz.ac.auckland.se206.user.UserProfile.Difficulty;

public class GameSelectionController {
  protected static String gameMode = "normal";
  @FXML private ImageView imageProfile;
  @FXML private Pane paneModes;
  @FXML private Pane normalModeSelection;
  @FXML private Pane paneEdit;
  @FXML private Text txtName;
  @FXML private Text txtStreak;
  private UserProfile currentUserProfile;

  @FXML private ToggleButton accuracyEasyToggle;
  @FXML private ToggleButton accuracyMedToggle;
  @FXML private ToggleButton accuracyHardToggle;
  @FXML private ToggleButton wordEasyToggle;
  @FXML private ToggleButton wordMedToggle;
  @FXML private ToggleButton wordHardToggle;
  @FXML private ToggleButton wordMasterToggle;
  @FXML private ToggleButton timeEasyToggle;
  @FXML private ToggleButton timeMedToggle;
  @FXML private ToggleButton timeHardToggle;
  @FXML private ToggleButton timeMasterToggle;
  @FXML private ToggleButton confidenceEasyToggle;
  @FXML private ToggleButton confidenceMedToggle;
  @FXML private ToggleButton confidenceHardToggle;
  @FXML private ToggleButton confidenceMasterToggle;
  @FXML private Button btnStartGame;

  /** This method sets the difficulty toggling buttons depending on the user's saved difficulties */
  public void setDifToggles() {
    // loads the current user data
    currentUserProfile = UserSelectionController.users[UserProfile.currentUser];

    // sets the default toggled option to the previous selected by the user
    if (currentUserProfile.getAccuracyDifficulty() == Difficulty.EASY) {
      accuracyEasyToggle.setSelected(true);
    } else if (currentUserProfile.getAccuracyDifficulty() == Difficulty.MEDIUM) {
      accuracyMedToggle.setSelected(true);
    } else if (currentUserProfile.getAccuracyDifficulty() == Difficulty.HARD) {
      accuracyHardToggle.setSelected(true);
    } else {
      accuracyEasyToggle.setSelected(true);
    }

    // sets the default toggled option to the previous selected by the user
    if (currentUserProfile.getWordDifficulty() == Difficulty.EASY) {
      wordEasyToggle.setSelected(true);
    } else if (currentUserProfile.getWordDifficulty() == Difficulty.MEDIUM) {
      wordMedToggle.setSelected(true);
    } else if (currentUserProfile.getWordDifficulty() == Difficulty.HARD) {
      wordHardToggle.setSelected(true);
    } else if (currentUserProfile.getWordDifficulty() == Difficulty.MASTER) {
      wordMasterToggle.setSelected(true);
    } else {
      wordEasyToggle.setSelected(true);
    }

    // sets the default toggled option to the previous selected by the user
    if (currentUserProfile.getTimeDifficulty() == Difficulty.EASY) {
      timeEasyToggle.setSelected(true);
    } else if (currentUserProfile.getTimeDifficulty() == Difficulty.MEDIUM) {
      timeMedToggle.setSelected(true);
    } else if (currentUserProfile.getTimeDifficulty() == Difficulty.HARD) {
      timeHardToggle.setSelected(true);
    } else if (currentUserProfile.getTimeDifficulty() == Difficulty.MASTER) {
      timeMasterToggle.setSelected(true);
    } else {
      timeEasyToggle.setSelected(true);
    }

    // sets the default toggled option to the previous selected by the user
    if (currentUserProfile.getConfidenceDifficulty() == Difficulty.EASY) {
      confidenceEasyToggle.setSelected(true);
    } else if (currentUserProfile.getConfidenceDifficulty() == Difficulty.MEDIUM) {
      confidenceMedToggle.setSelected(true);
    } else if (currentUserProfile.getConfidenceDifficulty() == Difficulty.HARD) {
      confidenceHardToggle.setSelected(true);
    } else if (currentUserProfile.getConfidenceDifficulty() == Difficulty.MASTER) {
      confidenceMasterToggle.setSelected(true);
    } else {
      confidenceEasyToggle.setSelected(true);
    }

    // sets the user image and name
    setProfileDetails();
  }

  /**
   * This method sets the user profile details in this scene such as win streak, username, and
   * profile image
   */
  protected void setProfileDetails() {
    imageProfile.setImage(currentUserProfile.getProfilePic());
    txtName.setText(currentUserProfile.getName());
    txtStreak.setText(String.valueOf(currentUserProfile.getWinStreak()));
  }

  /**
   * This method goes back to main menu scene upon pressing the back button
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onBack(Event event) {
    onResetView();

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }

  /** This method is called when the normal game mode is pressed and toggles the difficulty pane */
  @FXML
  private void onSelectedNormal() {
    gameMode = "normal";
    btnStartGame.setDisable(false);
    enablePaneDifficulties();
  }

  /** This method is called when the hidden game mode is pressed and toggles the difficulty pane */
  @FXML
  private void onSelectedHiddenMode() {
    gameMode = "hidden";
    enablePaneDifficulties();
  }

  /**
   * This method is called when the zen game mode is pressed and switches straight into canvas
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onSelectedZenMode(Event event) {
    gameMode = "zen";
    switchToCanvas(event);
    App.canvasInstances.get(UserProfile.currentUser).enableZenMode();
  }

  /**
   * This method is called when user selects either normal or hidden game mode and pane difficulties
   * is visible
   */
  private void enablePaneDifficulties() {
    paneModes.setVisible(false);
    paneModes.setDisable(true);

    normalModeSelection.setDisable(false);
    normalModeSelection.setVisible(true);
  }

  /**
   * This method is called to reset the scene to the initial view when user starts game or go back
   */
  @FXML
  private void onResetView() {
    paneModes.setDisable(false);
    paneModes.setVisible(true);
    normalModeSelection.setDisable(true);
    normalModeSelection.setVisible(false);
  }

  /**
   * This method is called when user starts game from the pane difficulties or zen mode
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onStartGame(Event event) {

    // Set difficulty settings
    setAccuracyDif();
    setWordDif();
    setTimeDif();
    setConfidenceDif();
    App.canvasInstances.get(UserProfile.currentUser).setGameDif(currentUserProfile);

    App.canvasInstances.get(UserProfile.currentUser).resetMode();

    // Go to player canvas
    switchToCanvas(event);
  }

  /**
   * This method is called upon after difficulties has been set and switches from this scene to
   * canvas
   *
   * @param event ActionEvent of the button to get scene
   */
  private void switchToCanvas(Event event) {
    Node node = (Node) event.getSource();
    node.getScene().setRoot(SceneManager.getUiRoot(getNewRoot(UserProfile.currentUser)));
    onResetView();
  }

  /** This method sets the accuracy difficulty of the current user instance */
  private void setAccuracyDif() {
    // Sets the user's difficulty to the one selected
    if (accuracyEasyToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.EASY);
    } else if (accuracyMedToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.MEDIUM);
    } else if (accuracyHardToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.HARD);

      // if no toggle is selected, default to easy
    } else {
      currentUserProfile.setAccuracyDifficulty(Difficulty.EASY);
    }
  }

  /** This method sets the word difficulty of the current user instance */
  private void setWordDif() {
    // Sets the user's difficulty to the one selected
    if (wordEasyToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.EASY);
    } else if (wordMedToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.MEDIUM);
    } else if (wordHardToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.HARD);
    } else if (wordMasterToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.MASTER);

      // if no toggle is selected, default to easy
    } else {
      currentUserProfile.setWordDifficulty(Difficulty.EASY);
    }
  }

  /** This method sets the time difficulty of the current user instance */
  private void setTimeDif() {
    // Sets the user's difficulty to the one selected
    if (timeEasyToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.EASY);
    } else if (timeMedToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.MEDIUM);
    } else if (timeHardToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.HARD);
    } else if (timeMasterToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.MASTER);

      // if no toggle is selected, default to easy
    } else {
      currentUserProfile.setTimeDifficulty(Difficulty.EASY);
    }
  }

  /** This method sets the confidence difficulty of the current user instance */
  private void setConfidenceDif() {
    // Sets the user's difficulty to the one selected
    if (confidenceEasyToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.EASY);
    } else if (confidenceMedToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.MEDIUM);
    } else if (confidenceHardToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.HARD);
    } else if (confidenceMasterToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.MASTER);

      // if no toggle is selected, default to easy
    } else {
      currentUserProfile.setConfidenceDifficulty(Difficulty.EASY);
    }
  }

  /**
   * This method returns the scene of the current user's canvas via input of the current user id
   *
   * @param id current user number from 1-6
   * @return the canvas scene of the current user's
   */
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

  /**
   * This method switches scene to the statistics scene
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onStats(Event event) {
    onResetView();

    // handles IOException
    try {
      SceneManager.addUi(SceneManager.AppUi.STATISTICS, App.addNode("statistics"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    // open stats page
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATISTICS));
  }

  /**
   * This method switches scene to userselection and toggles the user creation pane to allow edits
   * to profile
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onEdit(Event event) {
    onResetView();

    Node node = (Node) event.getSource();
    Scene sceneOfNode = node.getScene();
    // goes back to user edit scene
    sceneOfNode.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
    App.userSelectionInstance.onEditMode();
  }

  /**
   * This method will set edit mode visible if user is not guest
   *
   * @param user current user id where guest is 0
   */
  protected void setEditMode(int user) {
    paneEdit.setVisible(user != 0);
  }

  /** This method toggles the music button, either turns it off or on */
  @FXML
  private void onToggleMusic() {
    MainMenuController.toggleMusic();
  }

  /**
   * This method deletes the current user profile and will delete all stats and details
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onDelete(Event event) {
    // deletes the user and user badges data
    UserFileHandler.deleteUserData(UserProfile.currentUser);
    // sets the instance to a new UserProfile
    UserSelectionController.users[UserProfile.currentUser] = new UserProfile();
    onResetView();

    App.userSelectionInstance.currentImageView.getParent().setVisible(false);
    App.userSelectionInstance.currentNameLabel.setText("");

    Node node = (Node) event.getSource();
    Scene sceneOfNode = node.getScene();
    // goes back to user selection
    sceneOfNode.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
  }
}
