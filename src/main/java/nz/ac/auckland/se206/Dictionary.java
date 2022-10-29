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

  public static String searchWordInfo(String query) throws IOException {

    String definition;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + query).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    if (jsonString.contains("title\":\"No Definitions Found")) {
      return "none";
    }

    JSONArray jsonObjects = (JSONArray) new JSONTokener(jsonString).nextValue();

    JSONObject jsonEntryObj = jsonObjects.getJSONObject(0);
    JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

    JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(0);
    JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");

    JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(0);
    definition = jsonDefinitionObj.getString("definition");

    return definition.replaceAll(query, "*");
  }
}
