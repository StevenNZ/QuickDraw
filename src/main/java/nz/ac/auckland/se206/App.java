package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.user.UserProfile;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {
  public static List<CanvasController> canvasInstances = new ArrayList<>();
  public static GameSelectionController gameSelectionInstance = new GameSelectionController();

  public static void main(final String[] args) {
    launch();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent parent = loader.load();
    if (fxml.equals("canvas")) {
      canvasInstances.add(loader.getController());
    } else if (fxml.equals("gameselection")) {
      gameSelectionInstance = loader.getController();
    }
    return parent;
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Main menu" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // Ensuring the app closes properly
    stage.setOnCloseRequest(
        e -> {
          Platform.exit();
        });
    SceneManager.addUi(SceneManager.AppUi.MAINMENU, loadFxml("mainmenu"));

    SceneManager.addUi(SceneManager.AppUi.USER_SELECTION, loadFxml("userselection"));

    UserProfile.currentUser = 0;
    SceneManager.addUi(SceneManager.AppUi.CANVAS, loadFxml("canvas"));

    for (int i = 1; i < 7; i++) { // initialises canvas for each user profile
      UserProfile.currentUser = i;
      AppUi sceneName = SceneManager.AppUi.getAppUiEnum(i);

      SceneManager.addUi(sceneName, loadFxml("canvas"));
    }

    SceneManager.addUi(SceneManager.AppUi.GAME_SELECTION, loadFxml("gameselection"));

    SceneManager.addUi(SceneManager.AppUi.STATISTICS, loadFxml("statistics"));

    final Scene mainMenuScene =
        new Scene(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU), 1280, 720);

    stage.setScene(mainMenuScene);
    stage.show();
  }
}
