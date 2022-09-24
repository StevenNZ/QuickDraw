package nz.ac.auckland.se206.user;

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

  public static void saveUserData(JSONObject user, int userNumber) throws IOException {

    File profileFolder = new File(".profiles");
    String fileLocation = ".profiles/user" + userNumber + ".json";

    // Creates the profiles folder
    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File userFile = new File(fileLocation);

    // This writes the data to the json file
    FileWriter userFileWriter = new FileWriter(userFile, false);

    userFileWriter.write(user.toJSONString());
    userFileWriter.flush(); // Clears the data in the file writer
    userFileWriter.close(); // Closes the data stream
  }

  public static JSONObject readUserData(int userNumber) throws IOException, ParseException {

    String fileLocation = ".profiles/user" + userNumber + ".json";
    JSONParser jsonParser = new JSONParser();
    JSONObject returnData;

    FileReader userFileReader = new FileReader(fileLocation);
    returnData = (JSONObject) jsonParser.parse(userFileReader);
    userFileReader.close(); // Closes the data stream
    return returnData;
  }

  public static Boolean deleteUserData(int userNumber) {

    String fileLocation = ".profiles/user" + userNumber + ".json";
    File userFile = new File(fileLocation);

    if (userFile.exists()) {
      // Returns true if the target file has been deleted
      // Returns false if it has not been deleted
      return userFile.delete();
    } else {
      System.out.println("File doesn't exist");
      return false;
    }
  }

  public static Image readProfileImage(int userNumber) throws FileNotFoundException {
    String profileImageLocation = ".profiles/user" + userNumber + "image.png";

    Image profileImage = new Image(new FileInputStream(profileImageLocation));

    return profileImage;
  }
}
