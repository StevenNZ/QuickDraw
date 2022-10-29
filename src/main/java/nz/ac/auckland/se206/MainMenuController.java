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

  /** This method either stops the music or loop it again */
  protected static void toggleMusic() {
    if (clip.isRunning()) {
      clip.stop();
    } else {
      clip.loop(Integer.MAX_VALUE);
    }
  }

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a clip for
   * music
   *
   * @throws UnsupportedAudioFileException when the audio file is invalid and not supported
   * @throws IOException when the file cannot be opened due to an error in audioInputStream
   * @throws LineUnavailableException when an error occurs getting the clip of the audio system
   */
  public void initialize()
      throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    // turns on the game music
    String file = "/sounds/backgroundMusic.wav";
    AudioInputStream audioInputStream =
        AudioSystem.getAudioInputStream(this.getClass().getResource(file));
    clip = AudioSystem.getClip();
    clip.open(audioInputStream);
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    // music settings
    float range = gainControl.getMaximum() - gainControl.getMinimum();
    float gain = (range * 0.68f) + gainControl.getMinimum();
    gainControl.setValue(gain);
    try {
      clip.wait(4000);
    } catch (Exception ignored) {
    }
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
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onStartGame(ActionEvent event) {
    Button thisButton = (Button) event.getSource();
    Scene mainMenuScene = thisButton.getScene();
    mainMenuScene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
  }

  /** This method is called when the music button is pressed and toggles it on or off */
  @FXML
  private void onToggleMusic() {
    toggleMusic();
  }
}
