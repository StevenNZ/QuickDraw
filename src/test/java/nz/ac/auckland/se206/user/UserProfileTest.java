package nz.ac.auckland.se206.user;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

public class UserProfileTest {

  @Test
  void saveUserDataTest() {
    UserProfile.currentUser = 11;

    UserProfile testSubject = new UserProfile("Test Subject");

    for (int i = 0; i < 8; i++) {
      testSubject.updateWin();
    }

    for (int i = 0; i < 3; i++) {
      testSubject.updateLoss();
    }

    testSubject.saveUserData();
  }

  @Test
  void readUserDataTest() throws IOException, ParseException {
    UserProfile.currentUser = 11;

    UserProfile testSubject = new UserProfile("not right");

    testSubject.readUserData();

    System.out.println("Name: " + testSubject.getName());
    System.out.println("Total Wins: " + testSubject.getTotalWins());
    System.out.println("Total Losses: " + testSubject.getTotalLoss());
  }
}
