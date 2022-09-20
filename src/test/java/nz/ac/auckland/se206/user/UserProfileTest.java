package nz.ac.auckland.se206.user;

import org.junit.jupiter.api.Test;

public class UserProfileTest {

  @Test
  void SaveUserDataTest() {
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
}
