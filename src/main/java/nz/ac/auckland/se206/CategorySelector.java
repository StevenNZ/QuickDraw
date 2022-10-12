package nz.ac.auckland.se206;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CategorySelector {

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

  public static ArrayList<String> getEasyDifWords() {
    return getWords("E");
  }

  public static ArrayList<String> getMediumDifWords() {
    ArrayList<String> returnCategories = new ArrayList<String>();

    returnCategories.addAll(getWords("E"));
    returnCategories.addAll(getWords("M"));

    return returnCategories;
  }

  public static ArrayList<String> getHardDifWords() {
    ArrayList<String> returnCategories = new ArrayList<String>();

    returnCategories.addAll(getWords("E"));
    returnCategories.addAll(getWords("M"));
    returnCategories.addAll(getWords("H"));

    return returnCategories;
  }

  public static ArrayList<String> getMasterDifWords() {
    return getWords("H");
  }
}
