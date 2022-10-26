package nz.ac.auckland.se206;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.user.UserProfile;
import org.json.simple.JSONObject;

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

  @FXML ImageView twentySecondWin;
  @FXML ImageView tenSecondWin;
  @FXML ImageView fiveSecondWin;
  @FXML ImageView threeWinStreak;
  @FXML ImageView sixWinStreak;
  @FXML ImageView tenWinStreak;
  @FXML ImageView fiveHiddenWins;
  @FXML ImageView fifteenHiddenWins;
  @FXML ImageView thirtyHiddenWins;

  private UserProfile currentUserProfile;
  private String lblText;

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

    // Badges initialization
    JSONObject badges = currentUserProfile.getBadges().getBadgesMap();

    // Quickest win badges
    if ((boolean) badges.get("twentySecondWin") == true) {
      twentySecondWin.setVisible(true);
    }

    if ((boolean) badges.get("tenSecondWin") == true) {
      tenSecondWin.setVisible(true);
    }

    if ((boolean) badges.get("fiveSecondWin") == true) {
      fiveSecondWin.setVisible(true);
    }

    // Winstreak badges
    if ((boolean) badges.get("threeWinstreak") == true) {
      threeWinStreak.setVisible(true);
    }

    if ((boolean) badges.get("sixWinstreak") == true) {
      sixWinStreak.setVisible(true);
    }

    if ((boolean) badges.get("tenWinstreak") == true) {
      tenWinStreak.setVisible(true);
    }

    // Hidden wins badges
    if ((boolean) badges.get("fiveHiddenWins") == true) {
      fiveHiddenWins.setVisible(true);
    }

    if ((boolean) badges.get("fifteenHiddenWins") == true) {
      fifteenHiddenWins.setVisible(true);
    }

    if ((boolean) badges.get("thirtyHiddenWins") == true) {
      thirtyHiddenWins.setVisible(true);
    }
  }

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }
}
