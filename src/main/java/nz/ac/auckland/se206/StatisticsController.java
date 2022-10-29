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

  @FXML private Label lblWinRate;
  @FXML private Label lblQuickestWin;
  @FXML private Label lblTotalGames;
  @FXML private Label lblNormalWins;
  @FXML private Label lblNormalLosses;
  @FXML private Label lblNormalQuickest;
  @FXML private Label lblHiddenWins;
  @FXML private Label lblHiddenLosses;
  @FXML private Label lblHiddenQuickest;

  @FXML private ImageView twentySecondWin;
  @FXML private ImageView tenSecondWin;
  @FXML private ImageView fiveSecondWin;
  @FXML private ImageView threeWinStreak;
  @FXML private ImageView sixWinStreak;
  @FXML private ImageView tenWinStreak;
  @FXML private ImageView fiveHiddenWins;
  @FXML private ImageView fifteenHiddenWins;
  @FXML private ImageView thirtyHiddenWins;

  @FXML private Label tt20SecondWin;
  @FXML private Label tt10SecondWin;
  @FXML private Label tt5SecondWin;
  @FXML private Label tt3WinStreak;
  @FXML private Label tt6WinStreak;
  @FXML private Label tt10WinStreak;
  @FXML private Label tt5HiddenWins;
  @FXML private Label tt15HiddenWins;
  @FXML private Label tt30HiddenWins;

  @FXML private TextArea txtEasyWords;
  @FXML private TextArea txtMedWords;
  @FXML private TextArea txtHardWords;

  private UserProfile currentUserProfile;
  private String lblText;
  private Image winStreakBlack;
  private Image quickestWinBlack;
  private Image hiddenWinsBlack;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we update all relevant
   * current user stats and badges and words played
   */
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

  /**
   * This method goes back to game selection scene via the back button
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onBack(Event event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }

  /** This method initializes the badges to black if it has not been unlocked */
  private void initializeBadgeStats() {
    JSONObject badges = currentUserProfile.getBadges().getBadgesMap();

    try {
      winStreakBlack =
          new Image(
              new FileInputStream(
                  new File("src/main/resources/images/badges/win_streak_black.png")));
      quickestWinBlack =
          new Image(
              new FileInputStream(
                  new File("src/main/resources/images/badges/quickest_win_black.png")));
      hiddenWinsBlack =
          new Image(
              new FileInputStream(
                  new File("src/main/resources/images/badges/hidden_wins_black.png")));
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

  /**
   * This method initializes word history of current users for all easy, medium, and hard words
   *
   * @param currentUserProfile the current user's instance
   */
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

  /** This method toggles the music button either on or off */
  @FXML
  private void onToggleMusic() {
    MainMenuController.toggleMusic();
  }
}
