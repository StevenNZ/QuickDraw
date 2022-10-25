package nz.ac.auckland.se206;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.user.UserProfile;

public class StatisticsController {

  @FXML Label lblWinRate;
  @FXML Label lblQuickestWin;
  @FXML Label lblTotalGames;
  @FXML Label lblNormalWins;
  @FXML Label lblNormalLosses;
  @FXML Label lblNormalQuickest;
  @FXML Label lblHiddenWins;
  @FXML Label lblHiddenLosses;
  @FXML Label lblHiddenQuickest;
  private UserProfile currentUserProfile;
  private String lblText;

  public void initialize() {
    currentUserProfile = UserSelectionController.users[UserProfile.currentUser];

    if (currentUserProfile.getTotalWins() != 0 || currentUserProfile.getTotalLoss() != 0) {
      lblText =
          String.valueOf(
              (double) currentUserProfile.getTotalWins()
                  / ((double) currentUserProfile.getTotalWins()
                      + currentUserProfile.getTotalLoss()));
      lblText = lblText.substring(2, 4) + "%";
      lblWinRate.setText(lblText);
    }

    if (currentUserProfile.getQuickestWin() != 100) {
      lblText = String.valueOf(currentUserProfile.getQuickestWin()) + "s";
      lblQuickestWin.setText(lblText);
    }

    lblText = String.valueOf(currentUserProfile.getTotalLoss() + currentUserProfile.getTotalWins());
    lblTotalGames.setText(lblText);
  }

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }
}
