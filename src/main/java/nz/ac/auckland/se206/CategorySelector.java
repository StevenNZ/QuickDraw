package nz.ac.auckland.se206;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** This class helps select the next category for the user depending on their difficulty choices */
public class CategorySelector {

  /**
   * Get words retrieves all the words from the CSV file reader and adds them to the categories
   *
   * @param category difficulty of words
   * @return a list of all possible words of the input difficulty
   */
  private static ArrayList<String> getWords(String category) {

    ArrayList<String> categories = new ArrayList<String>();
    String[] nextLine;

    try {
      CSVReader fileReader =
          new CSVReader(
              new FileReader(
                  "src/main/resources/csv/category_difficulty.csv")); // reader for the csv file

      while ((nextLine = fileReader.readNext())
          != null) { // while the next entry in the file isnt null
        if (nextLine[1].equals(category)) { // if the category is classed easy
          categories.add(nextLine[0]); // then add it to the list to be chosen from
        }
      }

      fileReader.close();
    } catch (CsvValidationException | IOException e) {
      e.printStackTrace();
    }
    return categories;
  }

  /**
   * Retrieves the list of all of the Easy words from the CSV file
   *
   * @return and arraylist of easy words
   */
  public static ArrayList<String> getEasyDifWords() {
    return getWords("E");
  }
  /**
   * Retrieves the list of all of the Easy and Medium words from the CSV file
   *
   * @return an array list
   */
  public static ArrayList<String> getMediumDifWords() {
    ArrayList<String> returnCategories = new ArrayList<String>();

    returnCategories.addAll(getWords("E"));
    returnCategories.addAll(getWords("M"));

    return returnCategories;
  }

  /**
   * Retrieves the list of all of the Easy Medium and hard words from the CSV file
   *
   * @return an array list
   */
  public static ArrayList<String> getHardDifWords() {
    ArrayList<String> returnCategories = new ArrayList<String>();

    returnCategories.addAll(getWords("E"));
    returnCategories.addAll(getWords("M"));
    returnCategories.addAll(getWords("H"));

    return returnCategories;
  }
  /**
   * Retrieves the list of all of the Hard words from the CSV file
   *
   * @return an array list of hard words
   */
  public static ArrayList<String> getMasterDifWords() {
    return getWords("H");
  }

  public static ArrayList<String> getMediumDifWordsOnly() {
    return getWords("M");
  }
}
