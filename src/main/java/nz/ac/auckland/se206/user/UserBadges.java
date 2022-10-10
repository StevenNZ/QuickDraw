package nz.ac.auckland.se206.user;

import com.google.gson.Gson;

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

  public UserBadges() {}

  public void saveBadges() {
    Gson gson = new Gson();

    String badgesData = gson.toJson(this);

    UserFileHandler.saveUserBadges(badgesData);
  }
}
