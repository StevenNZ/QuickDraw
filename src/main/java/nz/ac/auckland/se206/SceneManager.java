package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {
  public enum AppUi {
    MAINMENU(-3),
    CANVAS(-2),
    GAME_SELECTION(-1),
    USER_SELECTION(0),
    CANVAS_PLAYER1(1),
    CANVAS_PLAYER2(2),
    CANVAS_PLAYER3(3),
    CANVAS_PLAYER4(4),
    CANVAS_PLAYER5(5),
    CANVAS_PLAYER6(6),
    STATISTICS(7);

    /**
     * This method returns the scene that corresponds to its respective ordinal.
     *
     * @param ord the ordinal of its scene
     * @return scene from the enum AppUi
     */
    public static AppUi getAppUiEnum(int ord) {
      for (AppUi scene : AppUi.values()) {
        if (scene.appOrdinal == ord) {
          return scene;
        }
      }
      return null;
    }

    private int appOrdinal;

    AppUi(int ord) {
      this.appOrdinal = ord;
    }
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }
}
