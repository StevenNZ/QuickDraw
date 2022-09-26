package nz.ac.auckland.se206.user;

import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.CategorySelector;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class UserProfile {
  public static int currentUser = 0;
  private String name = "";
  private int totalWins = 0;
  private int totalLoss = 0;
  private int quickestWin = 100;
  private List<String> wordHistory = new ArrayList<>();
  private List<String> availableEasyWords = new ArrayList<>();
  private Image profilePic = null;
  private ImageView imageView = null;

  public UserProfile() {
    String userDataLocation = ".profiles/user" + currentUser + ".json";
    File userData = new File(userDataLocation);

    if (userData.exists()) {
      try {
        readUserData(); // read user data from existing file
      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    }

    try {
      initializeAvailableWords();
    } catch (CsvValidationException | IOException e) {
      e.printStackTrace();
    }
  }

  public UserProfile(String name) {
    this.name = name;
    try {
      initializeAvailableWords();
    } catch (CsvValidationException | IOException e) {
      e.printStackTrace();
    }
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

  public void setName(String nameInput) {
    this.name = nameInput;
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

  public int getQuickestWin() {
    return quickestWin;
  }

  public StringBuilder getWordHistory() {
    StringBuilder sb = new StringBuilder();
    // Build a string with all the top 10 predictions from the ml api
    for (String word : wordHistory) {
      sb.append(word + "  ");
    }
    return sb;
  }

  public void setQuickestWin(int newQuickest) {
    this.quickestWin = newQuickest;
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
      UserFileHandler.saveUserData(userData, currentUser); // save user data into local file
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void readUserData() throws IOException, ParseException {
    JSONObject userData;

    userData = UserFileHandler.readUserData(currentUser); // read user data from local file

    this.name = (String) userData.get("name");
    this.totalWins = (int) (long) userData.get("totalWins");
    this.totalLoss = (int) (long) userData.get("totalLoss");
    this.quickestWin = (int) (long) userData.get("quickestWin");
    this.wordHistory = (List<String>) userData.get("wordHistory");

    this.profilePic = UserFileHandler.readProfileImage(currentUser);
  }

  public void initializeAvailableWords() throws CsvValidationException, IOException {
    List<String> easyWords;

    easyWords = CategorySelector.getEasyWords(); // All easy words

    for (String category : easyWords) { // For each easy word
      boolean found = false;
      for (String playedCategory : this.wordHistory) {
        if (category.equals(playedCategory)) { // Check if already played
          found = true;
          break;
        }
      }
      if (!found) {
        this.availableEasyWords.add(category);
      }
    }

    // If played all easy words, they are all available to be played
    if (this.availableEasyWords.size() == 0) {
      availableEasyWords = easyWords;
    }
  }

  public String pickEasyCategory() {
    Random random = new Random();
    int randNumber;
    String pickedCategory;

    randNumber = random.nextInt(availableEasyWords.size());
    pickedCategory =
        this.availableEasyWords.get(
            randNumber); // pick random entry from the list of easy categories

    this.availableEasyWords.remove(randNumber); // Remove from available Easy Words

    return pickedCategory;
  }
}
