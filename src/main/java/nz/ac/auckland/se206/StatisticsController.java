package nz.ac.auckland.se206;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
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

  @FXML Label tt20SecondWin;
  @FXML Label tt10SecondWin;
  @FXML Label tt5SecondWin;
  @FXML Label tt3WinStreak;
  @FXML Label tt6WinStreak;
  @FXML Label tt10WinStreak;
  @FXML Label tt5HiddenWins;
  @FXML Label tt15HiddenWins;
  @FXML Label tt30HiddenWins;

  @FXML TextArea txtEasyWords;
  @FXML TextArea txtMedWords;
  @FXML TextArea txtHardWords;

  private UserProfile currentUserProfile;
  private String lblText;
  private Image winStreakBlack;
  private Image quickestWinBlack;
  private Image hiddenWinsBlack;

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
    initializeBadgeStats();

    initializeWordHistory(currentUserProfile);
  }

  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }

  private void initializeBadgeStats() {
    JSONObject badges = currentUserProfile.getBadges().getBadgesMap();

    try {
      winStreakBlack =
          new Image(
              new FileInputStream(
                  new File("src\\main\\resources\\images\\badges\\win_streak_black.png")));
      quickestWinBlack =
          new Image(
              new FileInputStream(
                  new File("src\\main\\resources\\images\\badges\\quickest_win_black.png")));
      hiddenWinsBlack =
          new Image(
              new FileInputStream(
                  new File("src\\main\\resources\\images\\badges\\hidden_wins_black.png")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // Quickest win badges
    if ((boolean) badges.get("twentySecondWin") == false) {
      twentySecondWin.setImage(quickestWinBlack);
    }

    if ((boolean) badges.get("tenSecondWin") == false) {
      tenSecondWin.setImage(quickestWinBlack);
    }

    if ((boolean) badges.get("fiveSecondWin") == false) {
      fiveSecondWin.setImage(quickestWinBlack);
    }

    // Winstreak badges
    if ((boolean) badges.get("threeWinstreak") == false) {
      threeWinStreak.setImage(winStreakBlack);
    }

    if ((boolean) badges.get("sixWinstreak") == false) {
      sixWinStreak.setImage(winStreakBlack);
    }

    if ((boolean) badges.get("tenWinstreak") == false) {
      tenWinStreak.setImage(winStreakBlack);
    }

    // Hidden wins badges
    if ((boolean) badges.get("fiveHiddenWins") == false) {
      fiveHiddenWins.setImage(hiddenWinsBlack);
    }

    if ((boolean) badges.get("fifteenHiddenWins") == false) {
      fifteenHiddenWins.setImage(hiddenWinsBlack);
    }

    if ((boolean) badges.get("thirtyHiddenWins") == false) {
      thirtyHiddenWins.setImage(hiddenWinsBlack);
    }
  }

  private void initializeWordHistory(UserProfile currentUserProfile) {

    for (String category : currentUserProfile.getWordHistoryList()) {
      if (CategorySelector.getEasyDifWords().contains(category)) {
        txtEasyWords.appendText(category + "\n");
      } else if (CategorySelector.getMediumDifWordsOnly().contains(category)) {
        txtMedWords.appendText(category + "\n");
      } else if (CategorySelector.getMasterDifWords().contains(category)) {
        txtHardWords.appendText(category + "\n");
      } else {
        throw new RuntimeException("Played word not found in database");
      }
    }
  }
}
