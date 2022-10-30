package nz.ac.auckland.se206.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserFileHandler {

  /**
   * Saves the users data locally
   *
   * @param userData JSONString containing the user's data
   * @param userNumber The user whos data it is
   * @throws IOException
   */
  public static void saveUserData(String userData, int userNumber) throws IOException {

    File profileFolder = new File(".profiles");
    String fileLocation = ".profiles/user" + userNumber + ".json";

    // Creates the profiles folder
    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File userFile = new File(fileLocation);

    // This writes the data to the json file
    FileWriter userFileWriter = new FileWriter(userFile, false);

    userFileWriter.write(userData);
    userFileWriter.flush(); // Clears the data in the file writer
    userFileWriter.close(); // Closes the data stream
  }

  /**
   * Loads local user data into a JSONObject
   *
   * @param userNumber The user whos data it is
   * @return The stored users data
   * @throws IOException
   * @throws ParseException
   */
  public static JSONObject readUserData(int userNumber) throws IOException, ParseException {

    String fileLocation = ".profiles/user" + userNumber + ".json";
    JSONParser jsonParser = new JSONParser();
    JSONObject returnData;

    FileReader userFileReader = new FileReader(fileLocation);
    returnData = (JSONObject) jsonParser.parse(userFileReader);
    userFileReader.close(); // Closes the data stream
    return returnData;
  }

  /**
   * Deletes locally stored user data
   *
   * @param userNumber The user whos data it is
   * @return
   */
  public static Boolean deleteUserData(int userNumber) {

    String fileLocation = ".profiles/user" + userNumber + ".json";
    String badgesLocation = ".profiles/user" + userNumber + "Badges.json";
    File userFile = new File(fileLocation);
    File badgesFile = new File(badgesLocation);

    badgesFile.delete();

    if (userFile.exists()) {
      // Returns true if the target file has been deleted
      // Returns false if it has not been deleted
      return userFile.delete();
    } else {
      System.out.println("File doesn't exist");
      return false;
    }
  }

  /**
   * Loads the users profile picture
   *
   * @param userNumber The user whos data it is
   * @return The profile picture
   * @throws FileNotFoundException
   */
  public static Image readProfileImage(int userNumber) throws FileNotFoundException {
    String profileImageLocation = ".profiles/user" + userNumber + "image.png";

    File imageFile = new File(profileImageLocation);

    if (imageFile.exists()) {
      return new Image(
          new FileInputStream(profileImageLocation)); // returns image stored in local file
    } else {
      return null;
    }
  }

  /**
   * Saves the users badge data locally
   *
   * @param badges The badge data to be saved
   */
  public static void saveUserBadges(String badges) {

    File profileFolder = new File(".profiles");
    String fileLocation = ".profiles/user" + UserProfile.currentUser + "Badges.json";

    // Creates the profiles folder
    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File userFile = new File(fileLocation);

    try {

      // This writes the badge data to the json file
      FileWriter userFileWriter = new FileWriter(userFile, false);
      userFileWriter.write(badges);
      userFileWriter.flush(); // Clears the data in the file writer
      userFileWriter.close(); // Closes the data stream
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads local user badge data into a UserBadges instance
   *
   * @return The user badge data
   */
  public static UserBadges readUserBadges() {
    // badges file location
    String fileLocation = ".profiles/user" + UserProfile.currentUser + "Badges.json";
    JSONParser jsonParser = new JSONParser();
    UserBadges returnBadges;

    try {
      // opens the file
      FileReader userFileReader = new FileReader(fileLocation);
      returnBadges =
          new Gson().fromJson(jsonParser.parse(userFileReader).toString(), UserBadges.class);
      userFileReader.close(); // Closes the data stream
      returnBadges.mapBadges();
      return returnBadges;
    } catch (JsonSyntaxException | IOException | ParseException e) {
      System.out.println("Badges File doesn't exist");
      e.printStackTrace();
      return null;
    }
  }
}
