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
  @FXML private ImageView imageProfile;
  @FXML private Pane paneModes;
  @FXML private Pane normalModeSelection;
  @FXML private Text txtName;
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

  public void initialize() {}

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }

  @FXML
  private void onNormal() {
    paneModes.setVisible(false);
    paneModes.setDisable(true);

    normalModeSelection.setDisable(false);
    normalModeSelection.setVisible(true);
  }

  @FXML
  private void onStartGame(Event event) {
    currentUserProfile = UserSelectionController.users[UserProfile.currentUser];

    Node node = (Node) event.getSource();

    // Set difficulty settings
    setAccuracyDif();
    setWordDif();
    setTimeDif();
    setConfidenceDif();
    App.canvasInstances.get(UserProfile.currentUser).setGameDif(currentUserProfile);

    // Go to player canvas
    node.getScene().setRoot(SceneManager.getUiRoot(getNewRoot(UserProfile.currentUser)));
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

  //  private void receiveData(){
  //    Node node = (Node) fire.getSource();
  //    UserProfile user = (UserProfile) stage.getUserData();
  //
  //  }
}
