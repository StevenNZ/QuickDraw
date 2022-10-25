package nz.ac.auckland.se206;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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

  public void initialize() {}

  public void setDifToggles() {
    currentUserProfile = UserSelectionController.users[UserProfile.currentUser];

    if (currentUserProfile.getAccuracyDifficulty() == Difficulty.EASY) {
      accuracyEasyToggle.setSelected(true);
    } else if (currentUserProfile.getAccuracyDifficulty() == Difficulty.MEDIUM) {
      accuracyMedToggle.setSelected(true);
    } else if (currentUserProfile.getAccuracyDifficulty() == Difficulty.HARD) {
      accuracyHardToggle.setSelected(true);
    } else {
      accuracyEasyToggle.setSelected(true);
    }

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

    setProfileDetails();
  }

  private void setProfileDetails() {
    imageProfile.setImage(currentUserProfile.getProfilePic());
    txtName.setText(currentUserProfile.getName());
    txtStreak.setText(String.valueOf(currentUserProfile.getWinStreak()));
  }

  @FXML
  private void onBack(Event event) {
    resetView();

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }

  @FXML
  private void onNormal() {
    gameMode = "normal";
    btnStartGame.setDisable(false);
    enablePaneDifficulties();
  }

  @FXML
  private void onHiddenMode() {
    gameMode = "hidden";
    enablePaneDifficulties();
  }

  @FXML
  private void onZenMode(Event event) {
    gameMode = "zen";
    switchToCanvas(event);
    App.canvasInstances.get(UserProfile.currentUser).enableZenMode();
  }

  private void enablePaneDifficulties() {
    paneModes.setVisible(false);
    paneModes.setDisable(true);

    normalModeSelection.setDisable(false);
    normalModeSelection.setVisible(true);
  }

  private void resetView() {
    paneModes.setDisable(false);
    paneModes.setVisible(true);
    normalModeSelection.setDisable(true);
    normalModeSelection.setVisible(false);
  }

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

  private void switchToCanvas(Event event) {
    Node node = (Node) event.getSource();
    node.getScene().setRoot(SceneManager.getUiRoot(getNewRoot(UserProfile.currentUser)));
    resetView();
  }

  private void setAccuracyDif() {
    if (accuracyEasyToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.EASY);
    } else if (accuracyMedToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.MEDIUM);
    } else if (accuracyHardToggle.isSelected()) {
      currentUserProfile.setAccuracyDifficulty(Difficulty.HARD);
    } else {
      currentUserProfile.setAccuracyDifficulty(Difficulty.EASY);
    }
  }

  private void setWordDif() {
    if (wordEasyToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.EASY);
    } else if (wordMedToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.MEDIUM);
    } else if (wordHardToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.HARD);
    } else if (wordMasterToggle.isSelected()) {
      currentUserProfile.setWordDifficulty(Difficulty.MASTER);
    } else {
      currentUserProfile.setWordDifficulty(Difficulty.EASY);
    }
  }

  private void setTimeDif() {
    if (timeEasyToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.EASY);
    } else if (timeMedToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.MEDIUM);
    } else if (timeHardToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.HARD);
    } else if (timeMasterToggle.isSelected()) {
      currentUserProfile.setTimeDifficulty(Difficulty.MASTER);
    } else {
      currentUserProfile.setTimeDifficulty(Difficulty.EASY);
    }
  }

  private void setConfidenceDif() {
    if (confidenceEasyToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.EASY);
    } else if (confidenceMedToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.MEDIUM);
    } else if (confidenceHardToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.HARD);
    } else if (confidenceMasterToggle.isSelected()) {
      currentUserProfile.setConfidenceDifficulty(Difficulty.MASTER);
    } else {
      currentUserProfile.setConfidenceDifficulty(Difficulty.EASY);
    }
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
  private void onStats(Event event) {
    resetView();

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATISTICS));
  }

  @FXML
  private void onEdit(Event event) {
    resetView();

    Node node = (Node) event.getSource();
    Scene sceneOfNode = node.getScene();
    sceneOfNode.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
    App.userSelectionInstance.onEditMode();
  }

  protected void setEditMode(int user) {
    paneEdit.setVisible(user != 0);
  }

  //  private void receiveData(){
  //    Node node = (Node) fire.getSource();
  //    UserProfile user = (UserProfile) stage.getUserData();
  //
  //  }
}
