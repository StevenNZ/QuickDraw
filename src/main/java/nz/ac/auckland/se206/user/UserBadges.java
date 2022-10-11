package nz.ac.auckland.se206.user;

import com.google.gson.Gson;
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
}
