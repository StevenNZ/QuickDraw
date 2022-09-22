package nz.ac.auckland.se206.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class UserProfile {
  public static int currentUser = 0;
  private String name = "";
  private int totalWins = 0;
  private int totalLoss = 0;
  private int quickestWin = 100;
  private List<String> wordHistory = new ArrayList<>();
  private Image profilePic = null;
  private ImageView imageView = null;

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

  public Image getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(Image profilePic) {
    this.profilePic = profilePic;
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  public void setWord(String category) {
    for (String listCategory : wordHistory) {
      if (category.equals(listCategory)) {
        return;
      }
    }

    wordHistory.add(category);
  }

  public void saveUserData() {
    JSONObject userData = new JSONObject();

    userData.put("name", this.name);
    userData.put("totalWins", this.totalWins);
    userData.put("totalLoss", this.totalLoss);
    userData.put("quickestWin", this.quickestWin);
    userData.put("wordHistory", this.wordHistory);

    try {
      UserFileHandler.saveUserData(userData, currentUser);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void readUserData() throws IOException, ParseException {
    JSONObject userData;

    userData = UserFileHandler.readUserData(currentUser);

    this.name = (String) userData.get("name");
    this.totalWins = (int) (long) userData.get("totalWins");
    this.totalLoss = (int) (long) userData.get("totalLoss");
    this.quickestWin = (int) (long) userData.get("quickestWin");
    this.wordHistory = (List<String>) userData.get("wordHistory");
  }
}
