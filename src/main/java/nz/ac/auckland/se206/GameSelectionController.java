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
import javafx.stage.Stage;
import nz.ac.auckland.se206.user.UserProfile;
import nz.ac.auckland.se206.user.UserProfile.Difficulty;

public class GameSelectionController {
  @FXML private ImageView imageProfile;
  @FXML private Pane paneModes;
  @FXML private Pane normalModeSelection;
  @FXML private Text txtName;
  private UserProfile currentUser;

  @FXML private ToggleButton accuracyEasyToggle;
  @FXML private ToggleButton accuracyMedToggle;
  @FXML private ToggleButton accuracyHardToggle;
  @FXML private ToggleButton accuracyMasterToggle;
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

  public void initialize() {
    currentUser = UserSelectionController.users[UserProfile.currentUser];
    System.out.println(UserProfile.currentUser);
  }

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
    // Save difficulty settings
    Node node = (Node) event.getSource();
    Stage stage = (Stage) node.getScene().getWindow();
    UserProfile user = (UserProfile) stage.getUserData();

    setAccuracyDif();
    setWordDif();
    setTimeDif();
    setConfidenceDif();

    // Go to player canvas
    node.getScene().setRoot(SceneManager.getUiRoot(getNewRoot(UserProfile.currentUser)));
  }

  private void setAccuracyDif() {
    if (accuracyEasyToggle.isSelected()) {
      currentUser.setAccuracyDifficulty(Difficulty.EASY);
    } else if (accuracyMedToggle.isSelected()) {
      currentUser.setAccuracyDifficulty(Difficulty.MEDIUM);
    } else if (accuracyHardToggle.isSelected()) {
      currentUser.setAccuracyDifficulty(Difficulty.HARD);
    } else if (accuracyMasterToggle.isSelected()) {
      currentUser.setAccuracyDifficulty(Difficulty.MASTER);
    } else {
      currentUser.setAccuracyDifficulty(Difficulty.EASY);
    }
  }

  private void setWordDif() {
    if (wordEasyToggle.isSelected()) {
      currentUser.setWordDifficulty(Difficulty.EASY);
    } else if (wordMedToggle.isSelected()) {
      currentUser.setWordDifficulty(Difficulty.MEDIUM);
    } else if (wordHardToggle.isSelected()) {
      currentUser.setWordDifficulty(Difficulty.HARD);
    } else if (wordMasterToggle.isSelected()) {
      currentUser.setWordDifficulty(Difficulty.MASTER);
    } else {
      currentUser.setWordDifficulty(Difficulty.EASY);
    }
  }

  private void setTimeDif() {
    if (timeEasyToggle.isSelected()) {
      currentUser.setTimeDifficulty(Difficulty.EASY);
    } else if (timeMedToggle.isSelected()) {
      currentUser.setTimeDifficulty(Difficulty.MEDIUM);
    } else if (timeHardToggle.isSelected()) {
      currentUser.setTimeDifficulty(Difficulty.HARD);
    } else if (timeMasterToggle.isSelected()) {
      currentUser.setTimeDifficulty(Difficulty.MASTER);
    } else {
      currentUser.setTimeDifficulty(Difficulty.EASY);
    }
  }

  private void setConfidenceDif() {
    if (confidenceEasyToggle.isSelected()) {
      currentUser.setConfidenceDifficulty(Difficulty.EASY);
    } else if (confidenceMedToggle.isSelected()) {
      currentUser.setConfidenceDifficulty(Difficulty.MEDIUM);
    } else if (confidenceHardToggle.isSelected()) {
      currentUser.setConfidenceDifficulty(Difficulty.HARD);
    } else if (confidenceMasterToggle.isSelected()) {
      currentUser.setConfidenceDifficulty(Difficulty.MASTER);
    } else {
      currentUser.setConfidenceDifficulty(Difficulty.EASY);
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
