package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.user.UserProfile;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>IMPORTANT
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class CanvasController {
  private static final int DEFAULT_SECONDS = 60;
  protected static String randomCategory;
  private static String definition;

  @FXML private Button btnSaveDrawing;
  @FXML private Button btnStartTimer;
  @FXML private Canvas canvas;
  @FXML private Label lblCategoryTxt;
  @FXML private Label lblClickStartTimer;
  @FXML private Label lblTimer;
  @FXML private Label lblTopTenGuesses;
  @FXML private Label lblWinOrLoss;
  @FXML private Label lblWins;
  @FXML private Label lblLosses;
  @FXML private Label lblQuickestWin;
  @FXML private Label lblWordHistory;
  @FXML private Label lblCategoryIndex;
  @FXML private Label lblDefinition;
  @FXML private Label lblHiddenWord;
  @FXML private Pane paneCategories;
  @FXML private Pane paneEditCanvas;
  @FXML private Pane paneGameEnd;
  @FXML private Pane paneDefinition;
  @FXML private Button clearButton;
  @FXML private Button btnStats;
  @FXML private Button btnReturnCanvas;
  @FXML private Button btnNewGame;
  @FXML private Circle circleEraser;
  @FXML private Circle circlePen;
  @FXML private Pane paneButtons;
  @FXML private Pane paneCanvas;
  @FXML private Pane paneStats;
  @FXML private ToggleButton togglePen;
  @FXML private ToggleButton toggleEraser;
  @FXML private Polygon greenPolygon;
  @FXML private Polygon redPolygon;
  @FXML private Rectangle neutralRectangle;
  @FXML private TextFlow txtFlow;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  private boolean isStartPredictions = false;
  private UserProfile currentUser;
  private String gameoverString;

  private String outputPredictions;
  private int canvasTimer;
  private Image snapshot;
  private boolean isWin = false;
  private int categoryIndex = 340;

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture future;

  // run by background thread to not cause GUI freezing
  Runnable backgroundThreadTask =
      () -> {
        canvasTimer--;
        Platform.runLater(
            () -> {
              snapshot = canvas.snapshot(null, null); // app thread takes snapshot of canvas
            });

        Platform.runLater(
            () -> {
              lblTimer.setText(
                  String.format("%02d:%02d", canvasTimer / 60, canvasTimer % 60)); // updates timer
            });

        if (isStartPredictions && snapshot != null) {
          if (isStartPredictions) {
            try {
              onPredict();
            } catch (TranslateException ex) {
              throw new RuntimeException(ex);
            }
          }
        }

        if (isWin == true) {
          Platform.runLater(
              () -> {
                onGameEnd(true);
              });
        }

        if (canvasTimer == 0) {
          Platform.runLater(
              () -> {
                onGameEnd(false);
              });
        }

        String hidden = lblHiddenWord.getText();
        int hiddenLength = hidden.replaceAll("_ ", "").length();
        if (canvasTimer % 10 == 0
            && randomCategory.length() - hiddenLength / 2 > hiddenLength / 2
            && GameSelectionController.gameMode.equals("hidden")) {
          Random random = new Random();
          int index = random.nextInt(randomCategory.length()) * 2;
          Platform.runLater(
              () -> {
                lblHiddenWord.setText(
                    hidden.substring(0, index)
                        + randomCategory.charAt(index / 2)
                        + hidden.substring(index + 1));
              });
        }
      };

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    currentUser = UserSelectionController.users[UserProfile.currentUser];

    // Replace lblCategoryTxt on the canvas
    randomCategory = "shoe";
    lblCategoryTxt.setText(this.randomCategory);

    graphic = canvas.getGraphicsContext2D();
    graphic.setLineWidth(10);
    graphic.setLineCap(StrokeLineCap.ROUND);
    circlePen.setOpacity(0.5);

    canvas.setOnMousePressed(
        e -> {
          if (togglePen.isSelected()) { // pen settings
            isStartPredictions = true; // prediction sets to true when user draws (mouse pressed)
            graphic.setStroke(Color.BLACK);
            graphic.beginPath();
            graphic.lineTo(e.getX(), e.getY());
          } else if (toggleEraser.isSelected()) {
            erasing(e);
          }
        });
    canvas.setOnMouseDragged(
        e -> {
          if (togglePen.isSelected()
              && isStartPredictions) { // condition to stop canvas when game ends
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
          } else if (toggleEraser.isSelected() && isStartPredictions) {
            erasing(e);
          }
        });

    canvas.setOnMouseReleased(
        e -> {
          if (togglePen.isSelected() && isStartPredictions) {
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
            graphic.closePath();
          } else if (toggleEraser.isSelected() && isStartPredictions) {
            erasing(e);
          }
        });

    model = new DoodlePrediction();

    txtFlow.getChildren().setAll(new Text("Your top 10 guesses to your drawing will appear here!"));
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  /**
   * On start timer starts the timer and also the ability to play the game while hidign the ability
   * to go back to the main menu
   */
  @FXML
  private void onStartTimer() {
    this.canvasTimer =
        DEFAULT_SECONDS; // timer to be displayed and condition for the TimerTask ending
    future = executor.scheduleAtFixedRate(backgroundThreadTask, 1, 1, TimeUnit.SECONDS);
    Stage stage = (Stage) canvas.getScene().getWindow();
    stage.setOnCloseRequest( // text to speech closes upon closing GUI
        e -> {
          executor.shutdown();
          Platform.exit();
        });

    // Enable being able to edit the canvas and change pen colours
    paneEditCanvas.setDisable(false);
    canvas.setDisable(false);
    btnStartTimer.setDisable(true);
    btnStartTimer.setVisible(false);
    lblClickStartTimer.setVisible(false);

    paneButtons.setVisible(false);
    paneButtons.setDisable(true);

    currentUser.setWord(randomCategory); // Add to user word history

    isStartPredictions = false;
    System.out.println(randomCategory);
  }

  /**
   * This method executes gets the current drawing, queries the DL model and prints on the console
   * the top 5 predictions of the DL model and the elapsed time of the prediction in milliseconds.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  private void onPredict() throws TranslateException {
    List<Classifications.Classification> predictions =
        model.getPredictions(getBinaryImage(snapshot), 340);
    List<Classifications.Classification> predictionsTen = predictions.subList(0, 10);
    trackCategory(predictions);

    // Check the top 3 predictions whether they are what the word is.
    for (int i = 0; i < 3; i++) {
      String predictionClassName = predictionsTen.get(i).getClassName();
      // issue arose that underscores replaced spaces so need to replace them again for both types
      predictionClassName = predictionClassName.replaceAll("_", " ");
      if (randomCategory.equals(predictionClassName)) {
        // This is the win condition.
        isStartPredictions = false;
        isWin = true;
      }
    }

    getStringOfPredictions(predictionsTen);
  }

  private void trackCategory(List<Classifications.Classification> predictions) {
    for (int i = 0; i < predictions.size(); i++) {
      if (predictions.get(i).getClassName().replaceAll("_", " ").equals(randomCategory)) {
        int finalI = i;
        Platform.runLater(
            () -> {
              if (finalI < categoryIndex) {
                greenPolygon.setVisible(true);
                redPolygon.setVisible(false);
                neutralRectangle.setVisible(false);
              } else if (finalI > categoryIndex) {
                redPolygon.setVisible(true);
                greenPolygon.setVisible(false);
                neutralRectangle.setVisible(false);
              } else {
                neutralRectangle.setVisible(true);
                redPolygon.setVisible(false);
                greenPolygon.setVisible(false);
              }
              if (!GameSelectionController.gameMode.equals("hidden")) {
                lblCategoryIndex.setText(String.valueOf(finalI + 1));
              }
              categoryIndex = finalI;
            });
        break;
      }
    }
  }

  /**
   * This will be deprecated soon but shows stat page and hides canvas page. Updates the stats
   * labels
   */
  @FXML
  private void onViewStats() {
    // Update Label for wins
    lblWins.setText("" + currentUser.getTotalWins());
    // update label for losses
    lblLosses.setText("" + currentUser.getTotalLoss());
    // update label for quickest win
    if (currentUser.getQuickestWin() == 100) {
      lblQuickestWin.setText("N/A");
    } else {
      lblQuickestWin.setText("" + currentUser.getQuickestWin() + "s");
    }

    // update label for word history
    if (lblWordHistory != null) {
      lblWordHistory.setText(currentUser.getWordHistory().toString());
    }

    paneCanvas.setDisable(true);
    paneCanvas.setVisible(false);
    // hide the see stats button
    btnStats.setDisable(true);
    btnStats.setVisible(false);
    btnReturnCanvas.setVisible(true);
    btnReturnCanvas.setDisable(false);
    paneStats.setDisable(false);
    paneStats.setVisible(true);
  }

  /** This will be deprecated soon but not before the check hides stat page and shows canvas page */
  @FXML
  private void onBackToCanvas() {
    // Shows the View Statistics
    btnStats.setDisable(false);
    btnStats.setVisible(true);
    // Hides the Back to Canvas button
    btnReturnCanvas.setVisible(false);
    btnReturnCanvas.setDisable(true);
    // Toggles the statistics panes
    paneStats.setDisable(true);
    paneStats.setVisible(false);
    paneCanvas.setDisable(false);
    paneCanvas.setVisible(true);
  }

  /**
   * When called, onSaveDrawing brings up a file chooser to decide where to save the drawing
   *
   * @throws IOException
   */
  @FXML
  private void onSaveDrawing() throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Drawing");
    fileChooser.setInitialFileName(
        randomCategory.replaceAll(" ", "_")); // initially meaningful name
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
    // get the current stage
    Stage stage = (Stage) btnSaveDrawing.getScene().getWindow();
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      fileChooser.setInitialDirectory(file.getParentFile()); // save the image to a file.
      ImageIO.write(getCurrentSnapshot(), "bmp", file);
    }
  }

  /** When a new game is clicked, the board is reset to play a new game. */
  @FXML
  private void onNewGameClicked() {
    reset();
  }

  /**
   * When the game ends, true or false is passed - Reads a congrats or loss message in text to
   * speech - Updates statistics - Stop the timer - Switch panes
   */
  private void onGameEnd(boolean isWinner) {

    // Stops the background thread jobs
    future.cancel(true);

    // UpdateStats
    if (isWinner) {
      gameoverString = "Congratulations! You WON!";
      currentUser.updateWin();
      if ((DEFAULT_SECONDS - canvasTimer) < currentUser.getQuickestWin()) {
        currentUser.setQuickestWin(DEFAULT_SECONDS - canvasTimer);
      }
    } else {
      gameoverString =
          GameSelectionController.gameMode.equals("hidden")
              ? "Sorry, the hidden word was " + randomCategory
              : "Sorry, better luck next time.";
      currentUser.updateLoss();
    }
    currentUser.saveUserData();
    randomCategory = currentUser.pickCategory();
    // get new definition
    if (GameSelectionController.gameMode.equals("hidden")) {
      searchDefinition();
      btnNewGame.setVisible(false);
    }
    // Change labels to display win or loss
    lblWinOrLoss.setText(gameoverString);
    // Make buttons visible to save the drawing and reset appear.
    paneGameEnd.setDisable(false);
    paneGameEnd.setVisible(true);
    // Hide category display information
    paneCategories.setVisible(false);
    paneDefinition.setVisible(false);
    // Disable changing the drawing
    paneEditCanvas.setDisable(true);
    clearButton.setVisible(false);

    paneButtons.setVisible(true);
    paneButtons.setDisable(false);

    callTextToSpeech();
  }

  /**
   * When in eraser mode, clear rectangles based on the where the mouse is on the canvas
   *
   * @param e - MouseEvent (where the mouse has clicked)
   */
  private void erasing(MouseEvent e) {
    final double size = graphic.getLineWidth() * 1.2;
    graphic.clearRect(e.getX() - size / 2, e.getY() - size / 2, size, size);
  }

  /**
   * Creates a Text-To-Speech class on win and loss. Also ensures the app gets closed down correctly
   */
  private void callTextToSpeech() {
    Task<Void> textToSpeechTask = new Task<Void>() { // task run by a background thread
          @Override
          protected Void call() throws Exception {
            String file = isWin ? "/sounds/win.wav" : "/sounds/lose.wav";
            AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(this.getClass().getResource(file));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            TextToSpeech speech = new TextToSpeech();
            Stage stage = (Stage) canvas.getScene().getWindow();
            stage.setOnCloseRequest( // text to speech closes upon closing GUI
                e -> {
                  executor.shutdown();
                  Platform.exit();
                  speech.terminate();
                });
            speech.speak(gameoverString);
            return null;
          }
        };
    Thread textToSpeechThread =
        new Thread(textToSpeechTask); // creating new thread for text to speech task
    textToSpeechThread.start();
  }

  /**
   * Reset the panes and timer It has no parameters and makes the start timer button visible again
   */
  private void reset() {
    // Hide Game End Pane
    paneGameEnd.setVisible(false);
    // Clear the canvas
    onClear();
    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(randomCategory);
    // Hide category display information
    if (GameSelectionController.gameMode.equals("hidden")) {
      lblDefinition.setText(definition);
      paneDefinition.setVisible(true);
    } else {
      paneCategories.setVisible(true);
    }
    btnStartTimer.setVisible(true);
    btnStartTimer.setDisable(false);
    clearButton.setVisible(true);
    lblClickStartTimer.setVisible(true);
    txtFlow.getChildren().setAll(new Text("Your top 10 guesses to your drawing will appear here!"));
    greenPolygon.setVisible(false);
    redPolygon.setVisible(false);
    neutralRectangle.setVisible(true);
    lblCategoryIndex.setText("000");

    // Reset the timer
    this.canvasTimer = DEFAULT_SECONDS;
    lblTimer.setText(String.format("%02d:%02d", canvasTimer / 60, canvasTimer % 60));

    isWin = false;
  }

  /**
   * Retrieve List of predictions This is essentially the same as the printPredictions method in
   * DoodlePredictions.java, but I didn't want to mess with the class as it could potentially break
   * things.
   */
  private void getStringOfPredictions(List<Classifications.Classification> predictionsTen) {
    TextFlow temp = new TextFlow();
    int i = 1;
    for (Classifications.Classification classification : predictionsTen) {
      String category = classification.getClassName().replaceAll("_", " ");
      Text text =
          new Text((i + ".  " + category.substring(0, 1).toUpperCase() + category.substring(1)));
      if (category.equals(randomCategory) && !GameSelectionController.gameMode.equals("hidden")) {
        text.setFill(Color.GREEN);
        text.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 20));
      }
      temp.getChildren().add(text);
      temp.getChildren().add(new Text(System.lineSeparator()));
      i++;
    }
    Platform.runLater(
        () -> {
          txtFlow.getChildren().setAll(temp);
        });
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /**
   * Indicates to the user that the pen has been selected This method is called when the eraser
   * toggle button is pressed and makes the pen's circle at half opacity
   */
  @FXML
  private void onPenSelected() {
    circleEraser.setOpacity(1);
    circlePen.setOpacity(0.5);
  }

  /**
   * Indicates to the user that the eraser has been selected This method is called when the eraser
   * toggle button is pressed and makes the eraser's circle at half opacity
   */
  @FXML
  private void onEraserSelected() {
    circlePen.setOpacity(1);
    circleEraser.setOpacity(0.5);
  }

  /** This method is called when the back button is called and changes scene to main menu */
  @FXML
  private void onBack(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }
  /**
   * This method retreives a binary image of the canvas and returns a binary version of the image
   *
   * @param snapshot snapshot of the canvas
   * @return imageBinary - a binary version of the snapshot
   */
  private BufferedImage getBinaryImage(Image snapshot) {
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  protected void enableHiddenWord() {

    paneDefinition.setVisible(true);
    paneCategories.setVisible(false);
    lblDefinition.setText(definition);
  }

  protected void searchDefinition() {
    Task<Void> definitionTask = new Task<Void>() { // task run by a background thread
          @Override
          protected Void call() throws Exception {
            String definition = "none";

            while (definition.equals("none")) {
              try {
                definition = Dictionary.searchWordInfo(randomCategory);
              } catch (IOException e) {
                e.printStackTrace();
              }
              if (definition.equals("none")) {
                randomCategory = "giraffe";
                // TODO: get new random category
              }
            }
            CanvasController.definition = definition;
            String hidden = "_ ".repeat(randomCategory.length());

            Platform.runLater(
                () -> {
                  lblHiddenWord.setText(hidden);
                  btnNewGame.setVisible(true);
                });
            return null;
          }
        };
    Thread definitionThread =
        new Thread(definitionTask); // creating new thread for text to speech task
    definitionThread.start();
  }
}
