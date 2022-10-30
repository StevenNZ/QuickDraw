package nz.ac.auckland.se206.user;

import com.google.gson.Gson;
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
  private int normalWins = 0;
  private int normalLosses = 0;
  private int normalQuickest = 100;
  private int hiddenWins = 0;
  private int hiddenLosses = 0;
  private int hiddenQuickest = 100;
  private List<String> wordHistory = new ArrayList<>();
  private List<String> availableWords = new ArrayList<>();
  private transient Image profilePic = null;
  private transient ImageView imageView = null;
  private Difficulty wordDifficulty = Difficulty.NOTSET;
  private Difficulty accuracyDifficulty = Difficulty.NOTSET;
  private Difficulty timeDifficulty = Difficulty.NOTSET;
  private Difficulty confidenceDifficulty = Difficulty.NOTSET;
  private transient UserBadges badges = new UserBadges();
  private int winStreak = 0;

  public enum Difficulty {
    NOTSET,
    EASY,
    MEDIUM,
    HARD,
    MASTER;

    /**
     * Converts string to Difficulty enum value
     *
     * @param word String to be converted
     * @return The corresponding Difficulty enum value
     */
    public static Difficulty toDifficulty(String word) {
      // takes the string and converts it to the corresponding enum value
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
      // reached if the word input is not an enum equivalent
      throw new RuntimeException("Not a difficulty");
    }
  }

  /** If local data exists it loads that into the instance */
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
    this.badges = new UserBadges();
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

  /**
   * Gets a list of all words played
   *
   * @return StringBuilder with all words played by this user
   */
  public StringBuilder getWordHistory() {
    StringBuilder sb = new StringBuilder();
    // Build a string with all the words played
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

  /**
   * Adds played word to word history
   *
   * @param category Word that has been played
   */
  public void setWord(String category) {
    for (String listCategory : wordHistory) {
      if (category.equals(listCategory)) {
        return;
      }
    }

    wordHistory.add(category);
  }

  /** Saves this user's data locally */
  public void saveUserData() {
    Gson gson = new Gson();

    String userData = gson.toJson(this);
    String badgesData = gson.toJson(this.badges.getBadgesMap());

    try {
      UserFileHandler.saveUserData(userData, currentUser); // save user data into local file
    } catch (IOException e) {
      e.printStackTrace();
    }

    UserFileHandler.saveUserBadges(badgesData);
  }

  /**
   * Reads locally stored user data into this instance
   *
   * @throws IOException
   * @throws ParseException
   */
  public void readUserData() throws IOException, ParseException {
    JSONObject userData;

    userData = UserFileHandler.readUserData(currentUser); // read user data from local file

    // all the fields being read from the data returned
    this.name = (String) userData.get("name");
    this.totalWins = (int) (long) userData.get("totalWins");
    this.totalLoss = (int) (long) userData.get("totalLoss");
    this.quickestWin = (int) (long) userData.get("quickestWin");
    this.normalWins = (int) (long) userData.get("normalWins");
    this.normalLosses = (int) (long) userData.get("normalLosses");
    this.normalQuickest = (int) (long) userData.get("normalQuickest");
    this.hiddenWins = (int) (long) userData.get("hiddenWins");
    this.hiddenLosses = (int) (long) userData.get("hiddenLosses");
    this.hiddenQuickest = (int) (long) userData.get("hiddenQuickest");
    this.wordHistory = (List<String>) userData.get("wordHistory");
    this.wordDifficulty = Difficulty.toDifficulty((String) userData.get("wordDifficulty"));
    this.accuracyDifficulty = Difficulty.toDifficulty((String) userData.get("accuracyDifficulty"));
    this.timeDifficulty = Difficulty.toDifficulty((String) userData.get("timeDifficulty"));
    this.confidenceDifficulty =
        Difficulty.toDifficulty((String) userData.get("confidenceDifficulty"));
    this.winStreak = (int) (long) userData.get("winStreak");

    // reads the user profile image from its file
    this.profilePic = UserFileHandler.readProfileImage(currentUser);

    // reads the user badges from their file
    this.badges = UserFileHandler.readUserBadges();
  }

  /** Initializes words that can be played depending on the difficulty and word history. */
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

  /**
   * Randomly picks a category from available words
   *
   * @return Category that has been selected
   */
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

  /**
   * Randomly selects an index from the available words array
   *
   * @return Index selected
   */
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

  public UserBadges getBadges() {
    return this.badges;
  }

  public int getWinStreak() {
    return this.winStreak;
  }

  public void incrementWinStreak() {
    this.winStreak++;
  }

  public void resetWinStreak() {
    this.winStreak = 0;
  }

  public void incrementNormalWins() {
    this.normalWins++;
  }

  public int getNormalWins() {
    return this.normalWins;
  }

  public void incrementHiddenWins() {
    this.hiddenWins++;
  }

  public int getHiddenWins() {
    return this.hiddenWins;
  }

  public void setNormalQuickest(int time) {
    this.normalQuickest = time;
  }

  public int getNormalQuickest() {
    return this.normalQuickest;
  }

  public void setHiddenQuickest(int time) {
    this.hiddenQuickest = time;
  }

  public int getHiddenQuickest() {
    return this.hiddenQuickest;
  }

  public void incrementNormalLosses() {
    this.normalLosses++;
  }

  public int getNormalLosses() {
    return this.normalLosses;
  }

  public void incrementHiddenLosses() {
    this.hiddenLosses++;
  }

  public int getHiddenLosses() {
    return this.hiddenLosses;
  }

  public List<String> getWordHistoryList() {
    return wordHistory;
  }
}
