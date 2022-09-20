package nz.ac.auckland.se206;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  @FXML private Pane paneUserProfile;
  @FXML private Pane paneUserCreation;
  private static final UserProfile[] users = new UserProfile[7];

  @FXML
  private void onStartCanvas(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onCreateProfile(Event event) {
    String id = ((Node) event.getSource()).getId();
    UserProfile.currentUser = getProfile(id);

    paneUserProfile.setVisible(false);
    paneUserCreation.setVisible(true);
  }

  private int getProfile(String id) {
    switch (id) {
      case "circleNewUser1":
        return 1;
      case "circleNewUser2":
        return 2;
      case "circleNewUser3":
        return 3;
      case "circleNewUser4":
        return 4;
      case "circleNewUser5":
        return 5;
      case "circleNewUser6":
        return 6;
      default:
        return 0;
    }
  }

  @FXML
  public void onSaveProfile() {
    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
  }

  @FXML
  public void onBlackSelected() {}

  @FXML
  public void onEraserSelected() {}

  @FXML
  public void onClear() {}
}
