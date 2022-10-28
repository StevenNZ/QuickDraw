package nz.ac.auckland.se206;

import java.text.DecimalFormat;
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

  private DecimalFormat df = new DecimalFormat("#.00");

  public void initialize() {
    currentUserProfile = UserSelectionController.users[UserProfile.currentUser];

    if (currentUserProfile.getTotalWins() != 0 || currentUserProfile.getTotalLoss() != 0) {
      lblText =
          String.valueOf(
              String.format(
                  "%.2f",
                  (double) currentUserProfile.getTotalWins()
                      / ((double) currentUserProfile.getTotalWins()
                          + currentUserProfile.getTotalLoss())));
      if (lblText.equals("1.00")) {
        lblText = "100";
      } else if (lblText.substring(2, 3).equals("0")) {
        lblText = lblText.substring(3, 4);
      } else {
        lblText = lblText.substring(2, 4);
      }
      lblText += "%";
      lblWinRate.setText(lblText);
    }

    if (currentUserProfile.getQuickestWin() != 100) {
      lblText = String.valueOf(currentUserProfile.getQuickestWin()) + "s";
      lblQuickestWin.setText(lblText);
    }

    lblText = String.valueOf(currentUserProfile.getTotalLoss() + currentUserProfile.getTotalWins());
    lblTotalGames.setText(lblText);

    lblText = String.valueOf(currentUserProfile.getNormalWins());
    lblNormalWins.setText(lblText);

    lblText = String.valueOf(currentUserProfile.getNormalLosses());
    lblNormalLosses.setText(lblText);

    if (currentUserProfile.getNormalQuickest() != 100) {
      lblText = String.valueOf(currentUserProfile.getNormalQuickest() + "s");
      lblNormalQuickest.setText(lblText);
    }

    lblText = String.valueOf(currentUserProfile.getHiddenWins());
    lblHiddenWins.setText(lblText);

    lblText = String.valueOf(currentUserProfile.getHiddenLosses());
    lblHiddenLosses.setText(lblText);

    if (currentUserProfile.getHiddenQuickest() != 100) {
      lblText = String.valueOf(currentUserProfile.getHiddenQuickest() + "s");
      lblHiddenQuickest.setText(lblText);
    }
  }

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }
}
