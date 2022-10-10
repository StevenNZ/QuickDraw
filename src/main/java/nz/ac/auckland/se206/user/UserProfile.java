package nz.ac.auckland.se206.user;

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
  private List<String> availableWords = new ArrayList<>();
  private Image profilePic = null;
  private ImageView imageView = null;
  private Difficulty wordDifficulty = Difficulty.NOTSET;
  private Difficulty accuracyDifficulty = Difficulty.NOTSET;
  private Difficulty timeDifficulty = Difficulty.NOTSET;
  private Difficulty confidenceDifficulty = Difficulty.NOTSET;

  public enum Difficulty {
    NOTSET,
    EASY,
    MEDIUM,
    HARD,
    MASTER;

    public static Difficulty toDifficulty(String word) {
      switch (word) {
        case "NOTSET":
          return NOTSET;
        case "EASY":
          return EASY;
        case "MEDIUM":
          return MEDIUM;
        case "HARD":
          return HARD;
        case "MASTER":
          return MASTER;
      }
      throw new RuntimeException("Not a difficulty");
    }
  }

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
  }

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
    userData.put("wordDifficulty", this.wordDifficulty);
    userData.put("accuracyDifficulty", this.accuracyDifficulty);
    userData.put("timeDifficulty", this.timeDifficulty);
    userData.put("confidenceDifficulty", this.confidenceDifficulty);

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
    this.wordDifficulty = (Difficulty) userData.get("wordDifficulty");
    this.accuracyDifficulty = (Difficulty) userData.get("accuracyDifficulty");
    this.timeDifficulty = (Difficulty) userData.get("timeDifficulty");
    this.confidenceDifficulty = (Difficulty) userData.get("confidenceDifficulty");

    this.profilePic = UserFileHandler.readProfileImage(currentUser);
  }

  private void initializeAvailableWords() {
    List<String> allReleventWords;

    this.availableWords.clear();

    // Get words for the difficulty selected
    switch (this.wordDifficulty) {
      case EASY:
        allReleventWords = CategorySelector.getEasyDifWords();
        break;
      case MEDIUM:
        allReleventWords = CategorySelector.getMediumDifWords();
        break;
      case HARD:
        allReleventWords = CategorySelector.getHardDifWords();
        break;
      case MASTER:
        allReleventWords = CategorySelector.getMasterDifWords();
        break;
      default:
        throw new RuntimeException("Not a recognised word difficulty");
    }

    for (String category : allReleventWords) { // For each word
      boolean found = false;
      for (String playedCategory : this.wordHistory) {
        if (category.equals(playedCategory)) { // Check if already played
          found = true;
          break;
        }
      }
      if (!found) {
        this.availableWords.add(category);
      }
    }

    // If played all possible words, they are all available to be played
    if (this.availableWords.size() == 0) {
      availableWords = allReleventWords;
    }
  }

  public String pickCategory() {
    String pickedCategory;
    int randNumber = getRandomCategoryIndex();

    pickedCategory =
        this.availableWords.get(randNumber); // pick random entry from the list of categories

    this.availableWords.remove(randNumber); // Remove from available words

    if (this.availableWords.size() == 0) {
      initializeAvailableWords();
    }

    return pickedCategory;
  }

  private int getRandomCategoryIndex() {
    Random random = new Random();
    return random.nextInt(availableWords.size()); // pick random entry from the list of categories
  }

  public void setWordDifficulty(Difficulty dif) {
    this.wordDifficulty = dif;
    initializeAvailableWords();
  }

  public Difficulty getWordDifficulty() {
    return this.wordDifficulty;
  }

  public void setAccuracyDifficulty(Difficulty dif) {
    this.accuracyDifficulty = dif;
  }

  public Difficulty getAccuracyDifficulty() {
    return this.accuracyDifficulty;
  }

  public void setTimeDifficulty(Difficulty dif) {
    this.timeDifficulty = dif;
  }

  public Difficulty getTimeDifficulty() {
    return this.timeDifficulty;
  }

  public void setConfidenceDifficulty(Difficulty dif) {
    this.confidenceDifficulty = dif;
  }

  public Difficulty getConfidenceDifficulty() {
    return this.confidenceDifficulty;
  }
}
