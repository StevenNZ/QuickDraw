package nz.ac.auckland.se206.user;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserFileHandler {

  public static void saveUserData(JSONObject user, int userNumber) throws IOException {

    File profileFolder = new File("profiles");
    String fileLocation = "profiles/user" + userNumber + ".json";

    // Creates the profiles folder
    if (!profileFolder.exists()) {
      profileFolder.mkdir();
    }

    File userFile = new File(fileLocation);

    // This writes the data to the json file
    FileWriter userFileWriter = new FileWriter(userFile, false);

    userFileWriter.write(user.toJSONString());
    userFileWriter.flush();
    userFileWriter.close();
  }

  public static JSONObject readUserData(int userNumber) throws IOException, ParseException {

    String fileLocation = "profiles/user" + userNumber + ".json";
    JSONParser jsonParser = new JSONParser();

    FileReader userFileReader = new FileReader(fileLocation);
    return (JSONObject) jsonParser.parse(userFileReader);
  }
}
