package nz.ac.auckland.se206.user;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
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
}
