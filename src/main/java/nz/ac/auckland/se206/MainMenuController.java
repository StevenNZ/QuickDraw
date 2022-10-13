package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/** Controls the Main Menu which acts as a landing page for the game. */
public class MainMenuController {

  /** On exit, it exits the application fully. Not much more complicated than that. */
  @FXML
  private void onExit() {
    Platform.exit();
  }

  /**
   * On start game it sends you to teh user selection page to create your profile
   *
   * @param event
   */
  @FXML
  private void onStartGame(ActionEvent event) {
    Button thisButton = (Button) event.getSource();
    Scene mainMenuScene = thisButton.getScene();
    mainMenuScene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
  }
}
