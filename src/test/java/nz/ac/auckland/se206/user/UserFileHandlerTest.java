package nz.ac.auckland.se206.user;

import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

public class UserFileHandlerTest {

  @Test
  void saveUserFileTest() throws IOException {

    JSONObject testUser = new JSONObject();

    testUser.put("name", "tester");
    testUser.put("totalWins", 7);
    testUser.put("totalLoss", 3);

    UserFileHandler.saveUserData(testUser, 10);
  }

  @Test
  void readUserFileTest() throws IOException, ParseException {

    JSONObject testUser;

    testUser = UserFileHandler.readUserData(10);

    System.out.println(testUser);
  }

  @Test
  void deleteUserFileTest() {

    // File is there to be deleted
    System.out.println("Delete user10: " + UserFileHandler.deleteUserData(10));

    // No such file exists
    System.out.println("Delete user20: " + UserFileHandler.deleteUserData(20));
    System.out.println();
  }
}
