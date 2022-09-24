package nz.ac.auckland.se206;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CategorySelector {

  public static ArrayList<String> getEasyWords() throws CsvValidationException, IOException {

    CSVReader fileReader =
        new CSVReader(
            new FileReader(
                "src/main/resources/csv/category_difficulty.csv")); // reader for the csv file
    ArrayList<String> categories = new ArrayList<String>();
    String[] nextLine;

    while ((nextLine = fileReader.readNext())
        != null) { // while the next entry in the file isnt null
      if (nextLine[1].equals("E")) { // if the category is classed easy
        categories.add(nextLine[0]); // then add it to the list to be chosen from
      }
    }

    fileReader.close();
    return categories;
  }
}
