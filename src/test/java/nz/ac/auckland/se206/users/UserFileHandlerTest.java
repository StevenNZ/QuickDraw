package nz.ac.auckland.se206.users;

import java.io.IOException;
import nz.ac.auckland.se206.UserFileHandler;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

public class UserFileHandlerTest {

  @Test
  void saveUserFileTest() throws IOException {

    JSONObject testUser = new JSONObject();

    testUser.put("name", "tester");
    testUser.put("totalWins", 7);
    testUser.put("totalLoss", 3);

    UserFileHandler.saveUserData(testUser, 12);
  }
}
