package nz.ac.auckland.se206;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  private static final UserProfile[] users = new UserProfile[7];
  private static int currentUser = 0;

  @FXML
  private void onStartCanvas(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onCreateProfile(ActionEvent event) {
    String id = ((Node) event.getSource()).getId();
    int idIndex = getProfile(id);
    if (users[idIndex] == null) {
      // Go to profile creation scene
    } else {
      currentUser = idIndex;
      onStartCanvas(event);
    }
  }

  private int getProfile(String id) {
    switch (id) {
      case "btnProfile1":
        return 1;
      case "btnProfile2":
        return 2;
      case "btnProfile3":
        return 3;
      case "btnProfile4":
        return 4;
      case "btnProfile5":
        return 5;
      case "btnProfile6":
        return 6;
      default:
        return 0;
    }
  }

  public void onBlackSelected(MouseEvent mouseEvent) {}
}
