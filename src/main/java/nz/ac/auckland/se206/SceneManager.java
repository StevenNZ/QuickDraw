package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {
  public enum AppUi {
    MAINMENU,
    CANVAS,
    USER_SELECTION,
    CANVAS_PLAYER1,
    CANVAS_PLAYER2,
    CANVAS_PLAYER3,
    CANVAS_PLAYER4,
    CANVAS_PLAYER5,
    CANVAS_PLAYER6,
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }
}
