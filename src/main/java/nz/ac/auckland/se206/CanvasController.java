package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.ml.DoodlePrediction.printPredictions;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.user.UserProfile;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class CanvasController {
  private static final int DEFAULT_SECONDS = 60;
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
  @FXML private Pane paneCategories;
  @FXML private Pane paneEditCanvas;
  @FXML private Pane paneGameEnd;
  @FXML private Button clearButton;
  @FXML private Button btnStats;
  @FXML private Button btnReturnCanvas;
  @FXML private Circle circleEraser;
  @FXML private Circle circlePen;
  @FXML private Pane paneButtons;
  @FXML private Pane paneCanvas;
  @FXML private Pane paneStats;
  @FXML private ToggleButton togglePen;
  @FXML private ToggleButton toggleEraser;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  private int secondsLeft;
  private String randomCategory;
  private Timeline timeline;
  private boolean isStartPredictions = false;
  private UserProfile currentUser;
  private String gameoverString;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    currentUser = UserSelectionController.users[UserProfile.currentUser];
    randomCategory = currentUser.pickEasyCategory();

    this.timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1.0),
                e -> {
                  secondsLeft--;
                  // Update the Timer label with how many seconds left
                  lblTimer.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
                  // Trigger Lose conditions if they run out of time
                  if (secondsLeft == 0) {
                    onGameEnd(false);
                  }
                  // Run ML predictions in the background
                  Platform.runLater(
                      () -> {
                        try {
                          if (isStartPredictions) {
                            onPredict();
                          }
                        } catch (TranslateException ex) {
                          throw new RuntimeException(ex);
                        }
                      });
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);

    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(this.randomCategory);

    graphic = canvas.getGraphicsContext2D();
    graphic.setLineWidth(8);
    graphic.setLineCap(StrokeLineCap.ROUND);
    circlePen.setOpacity(0.5);

    canvas.setOnMousePressed(
        e -> {
          if (togglePen.isSelected()) {
            isStartPredictions = true;
            graphic.setStroke(Color.BLACK);
            graphic.beginPath();
            graphic.lineTo(e.getX(), e.getY());
          } else if (toggleEraser.isSelected()) {
            final double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });
    canvas.setOnMouseDragged(
        e -> {
          if (togglePen.isSelected() && isStartPredictions) {
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
          } else if (toggleEraser.isSelected() && isStartPredictions) {
            final double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });

    canvas.setOnMouseReleased(
        e -> {
          if (togglePen.isSelected() && isStartPredictions) {
            graphic.lineTo(e.getX(), e.getY());
            graphic.stroke();
            graphic.closePath();
          } else if (toggleEraser.isSelected() && isStartPredictions) {
            final double lineWidth = graphic.getLineWidth();
            graphic.clearRect(
                e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
          }
        });

    model = new DoodlePrediction();
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  @FXML
  private void onStartTimer() {
    this.secondsLeft = DEFAULT_SECONDS;
    timeline.play();
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
  }

  /**
   * This method executes when the user clicks the "Predict" button. It gets the current drawing,
   * queries the DL model and prints on the console the top 5 predictions of the DL model and the
   * elapsed time of the prediction in milliseconds.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  @FXML
  private void onPredict() throws TranslateException {
    List<Classifications.Classification> predictions =
        model.getPredictions(getCurrentSnapshot(), 10);
    // final long start = System.currentTimeMillis();

    printPredictions(predictions);

    lblTopTenGuesses.setText(getStringOfPredictions(predictions).toString());

    // Check the top 3 predictions whether they are what the word is.
    for (int i = 0; i < 3; i++) {
      String predictionClassName = predictions.get(i).getClassName();
      // issue arose that underscores replaced spaces so need to replace them again for both types
      predictionClassName = predictionClassName.replaceAll("_", " ");
      if (randomCategory.equals(predictionClassName)) {
        // This is the win condition.
        isStartPredictions = false;
        Platform.runLater(() -> onGameEnd(true));
      }
    }
  }

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

  @FXML
  private void onBackToCanvas() {
    btnStats.setDisable(false);
    btnStats.setVisible(true);
    btnReturnCanvas.setVisible(false);
    btnReturnCanvas.setDisable(true);
    paneStats.setDisable(true);
    paneStats.setVisible(false);
    paneCanvas.setDisable(false);
    paneCanvas.setVisible(true);
  }

  @FXML
  private void onSaveDrawing() throws IOException {
    // get the current stage
    Stage stage = (Stage) btnSaveDrawing.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Drawing");
    fileChooser.setInitialFileName(
        randomCategory.replaceAll(" ", "_")); // initially meaningful name
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      fileChooser.setInitialDirectory(file.getParentFile()); // save the
      ImageIO.write(getCurrentSnapshot(), "bmp", file);
    }
    // Save the image to a file.

  }

  @FXML
  private void onNewGameClicked() {
    reset();
  }

  private void onGameEnd(boolean isWin) {
    // Stop the timer
    timeline.stop();

    // UpdateStats

    if (isWin) {
      gameoverString = "Congratulations! You WON!";
      currentUser.updateWin();
      if ((DEFAULT_SECONDS - secondsLeft) < currentUser.getQuickestWin()) {
        currentUser.setQuickestWin(DEFAULT_SECONDS - secondsLeft);
      }
    } else {
      gameoverString = "Sorry, better luck next time.";
      currentUser.updateLoss();
    }
    currentUser.saveUserData();
    // Change labels to display win or loss
    lblWinOrLoss.setText(gameoverString);
    // Make buttons visible to save the drawing and reset appear.
    paneGameEnd.setDisable(false);
    paneGameEnd.setVisible(true);
    // Hide category display information
    paneCategories.setVisible(false);
    // Disable changing the drawing
    paneEditCanvas.setDisable(true);
    clearButton.setVisible(false);

    paneButtons.setVisible(true);
    paneButtons.setDisable(false);

    callTextToSpeech();
  }

  private void callTextToSpeech() {
    Task<Void> textToSpeechTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            TextToSpeech speech = new TextToSpeech();
            Stage stage = (Stage) canvas.getScene().getWindow();
            stage.setOnCloseRequest(
                e -> {
                  Platform.exit();
                  speech.terminate();
                });
            speech.speak(gameoverString);

            return null;
          }
        };
    Thread textToSpeechThread =
        new Thread(textToSpeechTask); // creating new thread for text to speech
    textToSpeechThread.start();
  }

  /** Reset the panes and timer */
  private void reset() {
    // Hide Game End Pane
    paneGameEnd.setVisible(false);
    // Clear the canvas
    onClear();
    randomCategory = currentUser.pickEasyCategory();
    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(this.randomCategory);
    // Hide category display information
    paneCategories.setVisible(true);
    btnStartTimer.setVisible(true);
    btnStartTimer.setDisable(false);
    clearButton.setVisible(true);
    lblClickStartTimer.setVisible(true);
    lblTopTenGuesses.setText("Your top 10 guesses to your drawing will appear here!");

    // Reset the timer
    this.secondsLeft = DEFAULT_SECONDS;
    lblTimer.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
  }

  /**
   * Retrieve List of predictions This is essentially the same as the printPredictions method in
   * DoodlePredictions.java, but I didn't want to mess with the class as it could potentially break
   * things.
   */
  private StringBuilder getStringOfPredictions(List<Classifications.Classification> predictions) {
    StringBuilder sb = new StringBuilder();
    int i = 1;
    // Build a string with all the top 10 predictions from the ml api
    for (Classifications.Classification classification : predictions) {
      String className = classification.getClassName().replaceAll("_", " ");
      sb.append(i)
          .append(". ")
          // Include the confidence percentage
          .append(String.format("[%.0f%%] ", 100 * classification.getProbability()))
          .append(className.substring(0, 1).toUpperCase() + className.substring(1))

          // Add new line
          .append(System.lineSeparator());

      i++;
    }
    return sb;
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

  /** This method is called when the black block is clicked and changes the pen colour to black */
  @FXML
  private void onPenSelected() {
    circleEraser.setOpacity(1);
    circlePen.setOpacity(0.5);
  }

  /** This method is called when the black block is clicked and changes the pen colour to black */
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
}
