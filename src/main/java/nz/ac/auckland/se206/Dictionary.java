package nz.ac.auckland.se206;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Dictionary {

  private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

  /**
   * This method grabs the definition of the input string from a dictionary api and returns that
   * definition or "none"
   *
   * @param query the word being searched for a definition
   * @return a string of the definition of the input
   * @throws IOException when an error occurs getting a response from the api
   */
  public static String searchWordInfo(String query) throws IOException {

    String definition;

    // calling the dictionary api
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + query).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    // returns a "none" string if there is no definition for the query
    if (jsonString.contains("title\":\"No Definitions Found")) {
      return "none";
    }

    JSONArray jsonObjects = (JSONArray) new JSONTokener(jsonString).nextValue();

    // getting the first entry of the meanings array
    JSONObject jsonEntryObj = jsonObjects.getJSONObject(0);
    JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

    // getting the first entry of the definitions array
    JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(0);
    JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");

    // getting the first definition which is usually a noun and preferred
    JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(0);
    definition = jsonDefinitionObj.getString("definition");

    // returns the definition replacing the word with a start
    return definition.replaceAll(query, "*");
  }
}
