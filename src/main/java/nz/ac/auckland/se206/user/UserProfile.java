package nz.ac.auckland.se206.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class UserProfile {
  public static int currentUser = 0;

  private String name = "";
  private int totalWins = 0;
  private int totalLoss = 0;
  private int quickestWin;
  private List<String> wordHistory = new ArrayList<>();

  public UserProfile(String name) {
    this.name = name;
  }

  public void updateWin() {
    totalWins++;
  }

  public void updateLoss() {
    totalLoss++;
  }

  public String getName() {
    return name;
  }

  public int getTotalWins() {
    return totalWins;
  }

  public int getTotalLoss() {
    return totalLoss;
  }

  public void saveUserData() {
    JSONObject userData = new JSONObject();

    userData.put("name", this.name);
    userData.put("totalWins", this.totalWins);
    userData.put("totalLoss", this.totalLoss);
    userData.put("quickestWin", this.quickestWin);

    try {
      UserFileHandler.saveUserData(userData, currentUser);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
