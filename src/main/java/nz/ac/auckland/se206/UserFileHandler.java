package nz.ac.auckland.se206;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;

public class UserFileHandler {

  public static void saveUserData(JSONObject user, int userNumber) throws IOException {

    File profileFolder = new File("profiles");
    String fileLocation = "profiles/user" + userNumber + ".json";

    // Creates the profiles folder
    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File userFile = new File(fileLocation);

    // Creates the user json file
    if (!userFile.exists()) {
      userFile.mkdir();
    }

    // This writes the data to the json file
    FileWriter userFileWriter = new FileWriter(userFile, false);

    userFileWriter.write(user.toJSONString());
    userFileWriter.flush();
    userFileWriter.close();
  }
}
