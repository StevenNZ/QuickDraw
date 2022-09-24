package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.ml.DoodlePrediction.printPredictions;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

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
  @FXML public ImageView penBlack;
  @FXML public ImageView eraser;
  @FXML private Button btnSaveDrawing;
  @FXML private Button btnStartTimer;
  @FXML private Canvas canvas;
  @FXML private Label lblCategoryTxt;
  @FXML private Label lblClickStartTimer;
  @FXML private Label lblTimer;
  @FXML private Label lblTopTenGuesses;
  @FXML private Label lblWinOrLoss;
  @FXML private Pane paneCategories;
  @FXML private Pane paneEditCanvas;
  @FXML private Pane paneGameEnd;
  @FXML private Button clearButton;
  @FXML private Circle circleEraser;
  @FXML private Rectangle boxBlue;
  @FXML private Rectangle boxRed;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  protected HashMap<String, ArrayList<String>> categories = new HashMap<>();
  private int secondsLeft;
  private String randomCategory;
  private Timeline timeline;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    processDataFromFile();
    generateRandomCategory();
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
                          onPredict();
                        } catch (TranslateException ex) {
                          throw new RuntimeException(ex);
                        }
                      });
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);

    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(this.randomCategory);

    graphic = canvas.getGraphicsContext2D();

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.fillOval(x, y, size, size);
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
    final long start = System.currentTimeMillis();

    printPredictions(predictions);

    lblTopTenGuesses.setText(getStringOfPredictions(predictions).toString());

    // Check the top 3 predictions whether they are what the word is.
    for (int i = 0; i < 3; i++) {
      String predictionClassName = predictions.get(i).getClassName();
      // issue arose that underscores replaced spaces so need to replace them again for both types
      predictionClassName = predictionClassName.replaceAll("_", " ");
      if (randomCategory.equals(predictionClassName)) {
        // This is the win condition.
        onGameEnd(true);
      }
    }
  }

  @FXML
  private void onSaveDrawing() throws IOException {
    // get the current stage
    Stage stage = (Stage) btnSaveDrawing.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Drawing");
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
    TextToSpeech textToSpeech = new TextToSpeech();

    Stage stage = (Stage) canvas.getScene().getWindow();
    stage.setOnCloseRequest(
        e -> {
          Platform.exit();
          textToSpeech.terminate();
        });
    String gameoverString;
    // Stop the timer
    timeline.stop();
    if (isWin) {
      gameoverString = "Congratulations! You WON!";
    } else {
      gameoverString = "Sorry, better luck next time.";
    }

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
    Platform.runLater(
        () -> {
          // Text to speak the loss or win
          textToSpeech.speak(gameoverString);
        });
  }

  /** Reset the panes and timer */
  private void reset() {
    // Hide Game End Pane
    paneGameEnd.setVisible(false);
    // Clear the canvas
    onClear();
    generateRandomCategory();
    // Replace lblCategoryTxt on the canvas
    lblCategoryTxt.setText(this.randomCategory);
    // Hide category display information
    paneCategories.setVisible(true);
    btnStartTimer.setVisible(true);
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

  /**
   * Save the current snapshot on a bitmap file.
   *
   * @return The file of the saved image.
   * @throws IOException If the image cannot be saved.
   */
  private File saveCurrentSnapshotOnFile() throws IOException {
    // You can change the location as you see fit.
    final File tmpFolder = new File("tmp");

    if (!tmpFolder.exists()) {
      tmpFolder.mkdir();
    }

    // We save the image to a file in the tmp folder.
    final File imageToClassify =
        new File(tmpFolder.getName() + "/snapshot" + System.currentTimeMillis() + ".bmp");

    // Save the image to a file.
    ImageIO.write(getCurrentSnapshot(), "bmp", imageToClassify);

    return imageToClassify;
  }

  /**
   * Takes all categories from the "category_difficulty.csv" file and places them into ArrayLists in
   * the hashmap depending on their perceived difficulty in the second column
   *
   * @throws IOException if file cannot be found
   */
  private void processDataFromFile() throws IOException {
    String splitBy = ",";
    String line;
    String[] splitColumns;
    BufferedReader bufferedReader =
        new BufferedReader(new FileReader("./src/main/resources/category_difficulty.csv"));
    while ((line = bufferedReader.readLine()) != null) {
      // Split the columns
      splitColumns = line.split(splitBy);
      categories.computeIfAbsent(splitColumns[1], k -> new ArrayList<>()).add(splitColumns[0]);
    }
  }

  /** Randomly choose a category based on level difficulty */
  private void generateRandomCategory() {
    // Initialise random
    Random rand = new Random();
    String category;
    int randomIndex;
    // LEVEL EASY

    // Get number of easy categories
    randomIndex = rand.nextInt(categories.get("E").size());
    category = categories.get("E").get(randomIndex);

    this.randomCategory = category;
  }
  // TODO: tidy this up so that there are subclasses for selecting paint colours.
  // Currently the colours are not in use as they are not needed until Zen mode.
  // Ideas:
  // Could use reflector to chose rectangles.
  /** This method is called when the Red block is clicked and changes the pen colour to red */
  @FXML
  private void onRedSelected() {
    // Set colour selected to gray
    boxRed.setOpacity(0.5);
    // Set all other box strokes to black
    boxBlue.setOpacity(1);
    // Black box removed
    circleEraser.setOpacity(1);
    // Change brush colour
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.RED);
          graphic.fillOval(x, y, size, size);
        });
  }
  /** This method is called when the blue block is clicked and changes the pen colour to blue */
  @FXML
  private void onBlueSelected() {

    // Set colour selected to gray
    boxBlue.setOpacity(0.5);
    // Set all other box strokes to black
    boxRed.setOpacity(1);
    // boxBlack.setOpacity(1);
    // boxEraser.setOpacity(1);

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLUE);
          graphic.fillOval(x, y, size, size);
        });
  }
  /** This method is called when the black block is clicked and changes the pen colour to black */
  @FXML
  private void onBlackSelected() {

    // Set colour selected to half opacity
    penBlack.setOpacity(0.5);
    // Set all other box strokes to black
    eraser.setOpacity(1.0);
    boxBlue.setOpacity(1);
    boxRed.setOpacity(1);
    // circleEraser.setOpacity(1);

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.fillOval(x, y, size, size);
        });
  }

  /** This method is called when the black block is clicked and changes the pen colour to black */
  @FXML
  private void onEraserSelected() {
    // Set colour selected to gray
    eraser.setOpacity(0.5);

    // Set all other box strokes to black
    boxBlue.setOpacity(1);
    boxRed.setOpacity(1);
    penBlack.setOpacity(1.0);

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 12.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.clearRect(x, y, size, size);
        });
  }

  /** This method is called when the back button is called and changes scene to main menu */
  @FXML
  private void onBack(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAINMENU));
  }
}
