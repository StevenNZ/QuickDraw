package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javax.sound.sampled.*;

/** Controls the Main Menu which acts as a landing page for the game. */
public class MainMenuController {

  protected static Clip clip;

  public void initialize()
      throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    String file = "/sounds/backgroundMusic.wav";
    AudioInputStream audioInputStream =
        AudioSystem.getAudioInputStream(this.getClass().getResource(file));
    clip = AudioSystem.getClip();
    clip.open(audioInputStream);
    clip.loop(Integer.MAX_VALUE);
  }

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
