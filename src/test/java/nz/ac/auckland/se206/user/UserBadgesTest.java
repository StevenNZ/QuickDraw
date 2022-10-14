package nz.ac.auckland.se206.user;

import org.junit.jupiter.api.Test;

public class UserBadgesTest {

  @Test
  void mapBadgesTest() {
    UserProfile.currentUser = 12;
    UserBadges badges = new UserBadges();

    badges.mapBadges();
    System.out.println(badges.getBadgesMap());
  }

  @Test
  void addBadgeTest() {
    UserProfile.currentUser = 12;
    UserBadges badges = new UserBadges();

    badges.addBadge("twentySecondWin");
    System.out.println();
    System.out.println(badges.getBadgesMap());
  }
}
