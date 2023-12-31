package nz.ac.auckland.se206.user;

import com.google.gson.Gson;
import nz.ac.auckland.se206.UserSelectionController;
import org.json.simple.JSONObject;

public class UserBadges {
  private boolean twentySecondWin = false;
  private boolean tenSecondWin = false;
  private boolean fiveSecondWin = false;
  private boolean threeWinstreak = false;
  private boolean sixWinstreak = false;
  private boolean tenWinstreak = false;
  private boolean fiveHiddenWins = false;
  private boolean fifteenHiddenWins = false;
  private boolean thirtyHiddenWins = false;
  private transient JSONObject badgesMap;

  public UserBadges() {
    this.mapBadges();
  }

  /** Saves the badge data locally */
  public void saveBadges() {
    Gson gson = new Gson();
    String badgesData = gson.toJson(this);
    UserFileHandler.saveUserBadges(badgesData);
  }

  /** Maps the UserBadge data into a JSONObject */
  public void mapBadges() {
    Gson gson = new Gson();
    badgesMap = gson.fromJson(gson.toJson(this), JSONObject.class);
  }

  public JSONObject getBadgesMap() {
    return badgesMap;
  }

  /**
   * Changes a badge to true (earned)
   *
   * @param badgeName The name of the badge earned
   */
  public void addBadge(String badgeName) {
    badgesMap.replace(badgeName, true);
  }

  /** Checks if the user has won any badges */
  public void checkBadges() {

    UserProfile user = UserSelectionController.users[UserProfile.currentUser];

    // Quickest win badges
    if (user.getQuickestWin() <= 20) {
      addBadge("twentySecondWin");
    }

    if (user.getQuickestWin() <= 10) {
      addBadge("tenSecondWin");
    }

    if (user.getQuickestWin() <= 5) {
      addBadge("fiveSecondWin");
    }

    // Winstreak badges
    if (user.getWinStreak() >= 3) {
      addBadge("threeWinstreak");
    }

    if (user.getWinStreak() >= 6) {
      addBadge("sixWinstreak");
    }

    if (user.getWinStreak() >= 10) {
      addBadge("tenWinstreak");
    }

    // Hidden mode win badges
    if (user.getHiddenWins() >= 5) {
      addBadge("fiveHiddenWins");
    }

    if (user.getHiddenWins() >= 15) {
      addBadge("fifteenHiddenWins");
    }

    if (user.getHiddenWins() >= 30) {
      addBadge("thirtyHiddenWins");
    }
  }
}
