package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class MainMenuController {

  @FXML
  private void onExit() {
    Platform.exit();
  }

  @FXML
  private void onStartGame(ActionEvent event) {
    Button thisButton = (Button) event.getSource();
    Scene mainMenuScene = thisButton.getScene();
    mainMenuScene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
  }
}
