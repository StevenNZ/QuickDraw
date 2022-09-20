package nz.ac.auckland.se206;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javax.swing.*;
import nz.ac.auckland.se206.user.UserProfile;

public class UserSelectionController {

  @FXML private Pane paneUserProfile;
  @FXML private Pane paneUserCreation;
  @FXML private TextField textFieldName;
  private static final UserProfile[] users = new UserProfile[7];
  public static int currentUser = 0;

  @FXML
  private void onStartCanvas(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onCreateProfile(Event event) {
    currentUser = getProfile(event);

    if (currentUser == 0) {
      users[0] = new UserProfile("Guest");
    }

    paneUserProfile.setVisible(false);
    paneUserCreation.setVisible(true);
  }

  private int getProfile(Event event) {
    String id = ((Node) event.getSource()).getId();

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
  private void onSaveProfile() {
    String name = textFieldName.getText();
    users[currentUser] = new UserProfile(name);
    currentUser = 0;

    paneUserProfile.setVisible(true);
    paneUserCreation.setVisible(false);
  }

  @FXML
  private void onBlackSelected() {}

  @FXML
  private void onEraserSelected() {}

  @FXML
  private void onClear() {}
}
