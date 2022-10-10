package nz.ac.auckland.se206.user;

import org.junit.jupiter.api.Test;

public class UserBadgesTest {

  @Test
  void mapBadgesTest() {
    UserProfile.currentUser = 10;
    UserBadges badges = new UserBadges();

    badges.mapBadges();
    System.out.println(badges.getBadgesMap());
  }
}
