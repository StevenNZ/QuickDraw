package nz.ac.auckland.se206;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class GameSelectionController {

  private void initialize() {}

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }
}
