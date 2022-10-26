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
  private boolean fiveMasterWins = false;
  private boolean fifteenMasterWins = false;
  private boolean thirtyMasterWins = false;
  private boolean fiveHiddenWins = false;
  private boolean fifteenHiddenWins = false;
  private boolean thirtyHiddenWins = false;
  private transient JSONObject badgesMap;

  public UserBadges() {
    this.mapBadges();
  }

  public void saveBadges() {
    Gson gson = new Gson();
    String badgesData = gson.toJson(this);
    UserFileHandler.saveUserBadges(badgesData);
  }

  public void mapBadges() {
    Gson gson = new Gson();
    badgesMap = gson.fromJson(gson.toJson(this), JSONObject.class);
  }

  public JSONObject getBadgesMap() {
    return badgesMap;
  }

  public void addBadge(String badgeName) {
    badgesMap.replace(badgeName, true);
  }

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

    if (user.getWinStreak() >= 3) {
      addBadge("sixWinstreak");
    }

    if (user.getWinStreak() >= 3) {
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
