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
import java.util.Stack;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
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
import nz.ac.auckland.se206.user.UserProfile.Difficulty;

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
  protected static String randomCategory;

  private int timerMax;
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
  @FXML private Label lblZenTxt;
  @FXML private Label lblHiddenWord;
  @FXML private Pane paneCategories;
  @FXML private Pane paneEditCanvas;
  @FXML private Pane paneGameEnd;
  @FXML private Pane paneDefinition;
  @FXML private Pane paneTimer;
  @FXML private Pane paneSaveDrawing;
  @FXML private Button btnStats;
  @FXML private Button btnReturnCanvas;
  @FXML private Button btnNewGame;
  @FXML private Circle circleEraser;
  @FXML private Circle circlePen;
  @FXML private Circle circlePaint;
  @FXML private Pane paneButtons;
  @FXML private Pane paneCanvas;
  @FXML private Pane paneStats;
  @FXML private Pane paneZen;
  @FXML private Pane panePaint;
  @FXML private ToggleButton togglePen;
  @FXML private ToggleButton toggleEraser;
  @FXML private ToggleButton clearButton;
  @FXML private ColorPicker paintButton;

  @FXML private Polygon greenPolygon;
  @FXML private Polygon redPolygon;
  @FXML private Rectangle neutralRectangle;
  @FXML private TextFlow txtFlow;
  @FXML private ImageView imageLoad;
  @FXML private Slider sliderThick;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  private boolean isStartPredictions = false;
  private UserProfile currentUser;
  private String gameoverString;
  private int canvasTimer;
  private Image snapshot;
  private boolean isWin = false;
  private int categoryIndex = 340;
  private int accuracyLevel;
  private double confidenceLevel;
  private Color paintColour = Color.BLACK;
  private double thickness;
  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture future;
  private Stack<Image> drawingStack = new Stack<>();

  // run by background thread to not cause GUI freezing
  private Runnable backgroundThreadTask =
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
        if (canvasTimer % 10 == 1
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

    graphic = canvas.getGraphicsContext2D();
    graphic.setLineWidth(10);
    graphic.setLineCap(StrokeLineCap.ROUND);
    circlePen.setOpacity(0.5);

    canvas.setOnMousePressed(
        e -> {
          graphic.setLineWidth(thickness);
          if (!toggleEraser.isSelected()) { // pen settings
            isStartPredictions = true; // prediction sets to true when user draws (mouse pressed)
            graphic.setStroke(paintColour);
            graphic.beginPath();
            graphic.lineTo(e.getX(), e.getY());
          } else if (toggleEraser.isSelected()) {
            erasing(e);
          }
          saveStroke();
        });
    canvas.setOnMouseDragged(
        e -> {
          if (!toggleEraser.isSelected()
              && isStartPredictions) { // condition to stop canvas when game ends
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
          } else if (toggleEraser.isSelected() && isStartPredictions) {
            erasing(e);
          }
        });

    canvas.setOnMouseReleased(
        e -> {
          if (!toggleEraser.isSelected() && isStartPredictions) {
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
    this.canvasTimer = timerMax; // timer to be displayed and condition for the TimerTask ending
    future = executor.scheduleAtFixedRate(backgroundThreadTask, 1, 1, TimeUnit.SECONDS);
    Stage stage = (Stage) canvas.getScene().getWindow();
    stage.setOnCloseRequest(
        e -> {
          future.cancel(true);
          executor.shutdown();
          Platform.exit();
        });

    // Enable being able to edit the canvas and change pen colours
    paneEditCanvas.setDisable(false);
    canvas.setDisable(false);

    btnStartTimer.setDisable(true);
    btnStartTimer.setVisible(false);

    if (!GameSelectionController.gameMode.equals("zen")) {
      lblClickStartTimer.setVisible(false);

      paneButtons.setVisible(false);
      paneButtons.setDisable(true);

      currentUser.setWord(randomCategory); // Add to user word history
    }

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

    // Check the top predictions if they are what the word is.
    for (int i = 0; i < accuracyLevel; i++) {
      //      System.out.println(predictionsTen.get(i));
      String predictionClassName = predictionsTen.get(i).getClassName();
      // issue arose that underscores replaced spaces so need to replace them again for both types
      predictionClassName = predictionClassName.replaceAll("_", " ");
      if (randomCategory.equals(predictionClassName)
          && !GameSelectionController.gameMode.equals("zen")) {
        if (predictionsTen.get(i).getProbability() >= confidenceLevel) {
          // This is the win condition.
          isStartPredictions = false;
          isWin = true;
        }
      }
    }

    getStringOfPredictions(predictionsTen);
  }

  /**
   * This method finds the category in the input list of predictions and updates the GUI elements;
   * Will notify if the user is getting closer or further
   *
   * @param predictions a list of predictions from highest to lowest percentages
   */
  private void trackCategory(List<Classifications.Classification> predictions) {
    for (int i = 0; i < predictions.size(); i++) {
      if (predictions.get(i).getClassName().replaceAll("_", " ").equals(randomCategory)) {
        int finalI = i;
        Platform.runLater(
            () -> {
              // if category has gone up predictions list show green arrow
              if (finalI < categoryIndex) {
                greenPolygon.setVisible(true);
                redPolygon.setVisible(false);
                neutralRectangle.setVisible(false);
                // if category has gone down predictions list show red arrow
              } else if (finalI > categoryIndex) {
                redPolygon.setVisible(true);
                greenPolygon.setVisible(false);
                neutralRectangle.setVisible(false);
                // if category hasn't moved in predictions list show neutral sign
              } else {
                neutralRectangle.setVisible(true);
                redPolygon.setVisible(false);
                greenPolygon.setVisible(false);
              }
              // doesn't show number in list for hidden mode
              if (!GameSelectionController.gameMode.equals("hidden")) {
                lblCategoryIndex.setText(String.valueOf(finalI + 1));
              }
              // sets to current value so can be compared next loop
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
   * @throws IOException when there's an error during the writing of ImageIO
   */
  @FXML
  private void onSaveDrawing() throws IOException {
    String category =
        GameSelectionController.gameMode.equals("zen")
            ? lblZenTxt.getText()
            : lblCategoryTxt.getText();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Drawing");
    fileChooser.setInitialFileName(category.replaceAll(" ", "_")); // initially meaningful name
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
    // get the current stage
    Stage stage = (Stage) paneSaveDrawing.getScene().getWindow();
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
   *
   * @param isWinner boolean variable for if user has won (true) or lost (false)
   */
  private void onGameEnd(boolean isWinner) {

    // Stops the background thread jobs
    future.cancel(true);

    paneEditCanvas.setDisable(true);
    clearButton.setVisible(false);

    // UpdateStats
    if (isWinner) {
      gameoverString = "Congratulations! You WON!";
      currentUser.updateWin();
      currentUser.incrementWinStreak();
      if ((timerMax - canvasTimer) < currentUser.getQuickestWin()) {
        currentUser.setQuickestWin(timerMax - canvasTimer);
      }

      if (GameSelectionController.gameMode.equals("hidden")) {
        currentUser.incrementHiddenWins();
        if ((timerMax - canvasTimer) < currentUser.getHiddenQuickest()) {
          currentUser.setHiddenQuickest(timerMax - canvasTimer);
        }
      } else if (GameSelectionController.gameMode.equals("normal")) {
        currentUser.incrementNormalWins();
        if ((timerMax - canvasTimer) < currentUser.getNormalQuickest()) {
          currentUser.setNormalQuickest(timerMax - canvasTimer);
        }
      }
    } else {
      gameoverString =
          GameSelectionController.gameMode.equals("hidden")
              ? "Sorry, hidden word was " + lblCategoryTxt.getText()
              : "Sorry, better luck next time.";
      currentUser.updateLoss();
      currentUser.resetWinStreak();

      if (GameSelectionController.gameMode.equals("hidden")) {
        currentUser.incrementHiddenLosses();
      } else if (GameSelectionController.gameMode.equals("normal")) {
        currentUser.incrementNormalLosses();
      }
    }
    // Check if badges won
    currentUser.getBadges().checkBadges();

    currentUser.saveUserData();
    getNewCategory(currentUser);
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
    paneSaveDrawing.setVisible(true);
    // Hide category display information
    paneCategories.setVisible(false);
    paneDefinition.setVisible(false);
    // Disable changing the drawing

    paneButtons.setVisible(true);
    paneButtons.setDisable(false);

    // clear all images in the drawing stack
    drawingStack.clear();

    callTextToSpeech();
  }

  /**
   * This method grabs a new category from the user's available words
   *
   * @param user the current user playing the game
   */
  protected void getNewCategory(UserProfile user) {
    randomCategory = user.pickCategory();
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
    // task run by a background thread
    Task<Void> textToSpeechTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // plays the corresponding sound
            String file = isWin ? "/sounds/win.wav" : "/sounds/lose.wav";
            AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(this.getClass().getResource(file));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            TextToSpeech speech = new TextToSpeech();
            Stage stage = (Stage) canvas.getScene().getWindow();

            stage.setOnCloseRequest(
                e -> {
                  // stops the background thread actions
                  executor.shutdown();
                  Platform.exit();
                  // text to speech closes upon closing GUI
                  speech.terminate();
                });
            // plays the corresponding end game message
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
    paneSaveDrawing.setVisible(false);
    // Clear the canvas
    onClear();

    // resets pane to the right game mode
    resetMode();

    btnStartTimer.setVisible(true);
    btnStartTimer.setDisable(false);
    clearButton.setVisible(true);
    lblClickStartTimer.setVisible(true);
    txtFlow.getChildren().setAll(new Text("Your top 10 guesses to your drawing will appear here!"));
    greenPolygon.setVisible(false);
    redPolygon.setVisible(false);
    neutralRectangle.setVisible(true);
    lblCategoryIndex.setText("000");
    onPenSelected();

    // Reset the timer
    this.canvasTimer = timerMax;
    lblTimer.setText(String.format("%02d:%02d", canvasTimer / 60, canvasTimer % 60));

    isWin = false;
  }

  /**
   * This method resets the initial view of a game mode from a finished game by toggling visibility
   * of panes
   */
  protected void resetMode() {
    // resets the pane depending on the game mode
    if (GameSelectionController.gameMode.equals("hidden")) {
      paneDefinition.setVisible(true);
      paneCategories.setVisible(false);
    } else {
      paneCategories.setVisible(true);
      paneDefinition.setVisible(false);
      paneZen.setVisible(false);
      paneTimer.setVisible(true);
      btnStartTimer.setVisible(true);
    }
    // displays the randomly selected category
    lblCategoryTxt.setText(randomCategory);
  }

  /**
   * Retrieve List of predictions This is essentially the same as the printPredictions method in
   * DoodlePredictions.java, but I didn't want to mess with the class as it could potentially break
   * things. It updates the textFlow control in the game displaying the current top ten predictions
   *
   * @param predictionsTen a list of top ten predictions
   */
  private void getStringOfPredictions(List<Classifications.Classification> predictionsTen) {
    TextFlow temp = new TextFlow();
    int i = 1;
    for (Classifications.Classification classification : predictionsTen) {
      // removes the _ and replaces them with a space
      String category = classification.getClassName().replaceAll("_", " ");
      Text text =
          new Text((i + ".  " + category.substring(0, 1).toUpperCase() + category.substring(1)));
      // sets the target category to stand out
      if (category.equals(randomCategory) && !GameSelectionController.gameMode.equals("hidden")) {
        text.setFill(Color.GREEN);
        text.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 20));
      }
      temp.getChildren().add(text);
      temp.getChildren().add(new Text(System.lineSeparator()));
      i++;
    }
    // displays the top ten predictions
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
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

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
    togglePen.setSelected(true);
  }

  /**
   * Indicates to the user that the eraser has been selected This method is called when the eraser
   * toggle button is pressed and makes the eraser's circle at half opacity
   */
  @FXML
  private void onEraserSelected() {
    circlePen.setOpacity(1);
    circleEraser.setOpacity(0.5);
    toggleEraser.setSelected(true);
  }

  /**
   * This method is called when the change user button is called and changes scene to user selection
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onChangeUser(ActionEvent event) {
    Scene sceneOfButton = onBackReset(event);
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.USER_SELECTION));
  }

  /**
   * This method is called when the user profile button is called and changes scene to game
   * selection
   *
   * @param event ActionEvent of the button to get scene
   */
  @FXML
  private void onUserProfile(ActionEvent event) {
    App.gameSelectionInstance.setProfileDetails();
    Scene sceneOfButton = onBackReset(event);
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_SELECTION));
  }

  /**
   * This method called when going back to user selection or user profile from canvas scene to reset
   * panes to initial view and returns the canvas scene from the action event listener
   *
   * @param event ActionEvent of the button to get scene
   * @return the scene of the event which is the canvas
   */
  private Scene onBackReset(ActionEvent event) {
    // resets the zen mode view
    if (GameSelectionController.gameMode.equals("zen")) {
      // stops the background thread actions
      future.cancel(true);
      paneEditCanvas.setDisable(true);
      panePaint.setVisible(false);
      sliderThick.setVisible(false);
      // resets the pen settings to default
      paintColour = Color.BLACK;
      thickness = 10;
      sliderThick.setValue(10);
      circlePaint.setFill(Color.WHITE);
    }
    reset();
    Button button = (Button) event.getSource();
    return button.getScene();
  }

  /**
   * This method retrieves a binary image of the canvas and returns a binary version of the image
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

  /**
   * This method searches for a definition by calling an api from a method in the dictionary class.
   * It starts a thread and then the main thread displays definition in game
   */
  protected void searchDefinition() {
    // task run by a background thread
    Task<Void> definitionTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            String definition = "none";

            // while definition not got yet
            while (definition.equals("none")) {
              try {
                // get the def for the category
                definition = Dictionary.searchWordInfo(randomCategory);
              } catch (IOException e) {
                e.printStackTrace();
              }
              // if no definition get a new category
              if (definition.equals("none")) {
                getNewCategory(currentUser);
              }
            }

            // out _ equal to number of letters in the category
            String hidden = "   " + "_ ".repeat(randomCategory.length());

            String finalDefinition = definition;
            Platform.runLater(
                () -> {
                  lblHiddenWord.setText(hidden);
                  lblDefinition.setText(finalDefinition);
                  btnNewGame.setVisible(true);
                  imageLoad.setVisible(false);
                });
            return null;
          }
        };
    Thread definitionThread =
        new Thread(definitionTask); // creating new thread for text to speech task
    definitionThread.start();
  }

  /**
   * This method sets the game difficulty for the current user from input
   *
   * @param user current user playing
   */
  public void setGameDif(UserProfile user) {
    UserSelectionController.users[UserProfile.currentUser] = user;
    currentUser = user;

    // sets the game difficulty settings the user has chosen
    setCategory();
    setTimer();
    setAccuracy();
    setConfidence();
  }

  /**
   * This method sets the category for the current game and if hidden mode, sets the definition as
   * well
   */
  private void setCategory() {
    getNewCategory(currentUser);
    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(this.randomCategory);
    if (GameSelectionController.gameMode.equals("hidden")) {
      lblDefinition.setText("");
      lblHiddenWord.setText("");
      imageLoad.setVisible(true);
      searchDefinition();
    }
  }

  /** This method sets the timer for the current game depending on the difficulty chosen by user */
  private void setTimer() {
    if (currentUser.getTimeDifficulty() == Difficulty.EASY) {
      timerMax = 60;
    } else if (currentUser.getTimeDifficulty() == Difficulty.MEDIUM) {
      timerMax = 45;
    } else if (currentUser.getTimeDifficulty() == Difficulty.HARD) {
      timerMax = 30;
    } else if (currentUser.getTimeDifficulty() == Difficulty.MASTER) {
      timerMax = 15;
    }
    lblTimer.setText(String.format("%02d:%02d", timerMax / 60, timerMax % 60)); // updates timer
  }

  /** This method sets the accuracy (top position of the prediction list) */
  private void setAccuracy() {
    // sets if category has to be in top 1, 2 or 3
    if (currentUser.getAccuracyDifficulty() == Difficulty.EASY) {
      accuracyLevel = 3;
    } else if (currentUser.getAccuracyDifficulty() == Difficulty.MEDIUM) {
      accuracyLevel = 2;
    } else if (currentUser.getAccuracyDifficulty() == Difficulty.HARD) {
      accuracyLevel = 1;
    }
  }

  /**
   * This method sets the confidence level of the game where it must be at least the prediction
   * percentage to win the game
   */
  private void setConfidence() {
    // sets required confidence percentage level for win
    if (currentUser.getConfidenceDifficulty() == Difficulty.EASY) {
      confidenceLevel = 0.01;
    } else if (currentUser.getConfidenceDifficulty() == Difficulty.MEDIUM) {
      confidenceLevel = 0.08;
    } else if (currentUser.getConfidenceDifficulty() == Difficulty.HARD) {
      confidenceLevel = 0.15;
    } else if (currentUser.getConfidenceDifficulty() == Difficulty.MASTER) {
      confidenceLevel = 0.25;
    }
  }

  /** This method is called when zen mode is played and enables/disables the appropriate panes */
  protected void enableZenMode() {
    // resets pane to zen mode pane
    paneTimer.setVisible(false);
    panePaint.setVisible(true);
    paneCategories.setVisible(false);
    paneDefinition.setVisible(false);
    paneSaveDrawing.setVisible(true);
    paneZen.setVisible(true);
    sliderThick.setVisible(true);
    // Timer must still be run for execution so large amount of time set
    timerMax = 60 * 60;
    displayNewCategory();
    // begins the game
    onStartTimer();
  }

  /** This method is called when the user clicks the triangle button to get new word */
  @FXML
  private void onZenNextWord() {
    onClear();
    displayNewCategory();
  }

  /** This method is called to display the new word used in zen mode */
  private void displayNewCategory() {
    randomCategory = CategorySelector.getHardDifWords().get(new Random().nextInt(340));
    lblZenTxt.setText(randomCategory);
  }

  /**
   * This method changes the pen colour to the user's selected colour and also creates a nice
   * gradient to the button icon
   */
  @FXML
  private void onPaintSelected() {
    // colour selected by user
    paintColour = paintButton.getValue();
    LinearGradient paint =
        new LinearGradient(
            0.0,
            1.0,
            1.0,
            0.0,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.WHITE),
            new Stop(0.9362, paintColour));
    circlePaint.setFill(paint);
    // turns pen function on with the new colour
    onPenSelected();
  }

  /** This method updates the thickness of the pen using a slider */
  @FXML
  private void onSliderReleased() {
    thickness = sliderThick.getValue();
  }

  /** This method toggles the music button by either stopping or looping the music */
  @FXML
  private void onToggleMusic() {
    MainMenuController.toggleMusic();
  }

  /**
   * This method saves the stroke of the user's drawing by taking a snapshot of the canvas and
   * adding to a stack
   */
  private void saveStroke() {
    drawingStack.add(canvas.snapshot(null, null));
  }

  /** This method pops the stack and updates the canvas with the previous drawn image */
  @FXML
  private void onUndo() {
    if (!drawingStack.isEmpty()) {
      canvas.getGraphicsContext2D().drawImage(drawingStack.pop(), 0, 0);
    }
  }
}
