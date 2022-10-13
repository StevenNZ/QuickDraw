package nz.ac.auckland.se206;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nz.ac.auckland.se206.user.UserProfile;

public class GameSelectionController {
  @FXML private ImageView fire;
  @FXML private Pane paneModes;
  @FXML private Pane normalModeSelection;

  private void initialize() {}

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
    // user.setAccuracyDifficulty();
    // user.setConfidenceDifficulty();
    // user.setTimeDifficulty();
    // user.setWordDifficulty();
    // Initialise words

    // Go to player canvas
    node.getScene().setRoot(SceneManager.getUiRoot(getNewRoot(UserProfile.currentUser)));
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
