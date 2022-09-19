package nz.ac.auckland.se206;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javax.swing.*;

public class UserSelectionController {
  @FXML
  private void onStartCanvas(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  public void onBlackSelected(MouseEvent mouseEvent) {}
}
