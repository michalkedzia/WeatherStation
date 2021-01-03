package app.api;

import app.api.data.CityListData;
import app.api.data.RainData;
import app.api.data.WeatherData;
import app.api.data.WindData;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ApiCaller {
  private static ArrayList<CityListData> cityListData = null;
  private static final String API_KEY = "565fdbe3a53559964ae424a55e25b9f3";
  private static final String API_URL =
      "https://api.openweathermap.org/data/2.5/weather?id={city id}&appid=";
  private static final int Kelvin = 273;

  public void initialize() throws FileNotFoundException {
    JsonReader jsonReader =
        new JsonReader(
            new FileReader(
                "C:/Users/mrmro/Desktop/java/KCK/WeatherStation/src/main/resources/city.list.json"));
    JsonArray entries = (JsonArray) new JsonParser().parse(jsonReader);

    cityListData = new ArrayList<>();
    Gson gson = new Gson();

    for (int i = 0; i < entries.size(); i++) {
      cityListData.add(
          gson.fromJson(entries.get(i).getAsJsonObject().toString(), CityListData.class));
    }
  }

  public int findCity(String name) {
    for (CityListData cityListDatu : cityListData) {
      if (name.equalsIgnoreCase(cityListDatu.name)) {
        return cityListDatu.id;
      }
    }

    return -1;
  }

  public WeatherData getWeatherData(int cityId) {
    String apiRequest = API_URL + API_KEY;
    apiRequest = apiRequest.replace("{city id}", Integer.toString(cityId));

    try {
      URL url = new URL(apiRequest);
      URLConnection request = url.openConnection();
      request.connect();

      JsonParser jp = new JsonParser();
      JsonObject rootObject = null;

      rootObject = jp.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();


      if(rootObject.has("weather") && rootObject.has("main")){
        Gson gson = new Gson();
        JsonArray weather = (JsonArray) rootObject.get("weather");
        JsonElement main = rootObject.get("main");

        RainData rainData = new RainData();
        if(rootObject.has("rain")){
          rainData = gson.fromJson(rootObject.get("rain").toString(), RainData.class);
        }
        WindData windData = gson.fromJson(rootObject.get("wind").toString(), WindData.class);
        //WeatherData weatherData = gson.fromJson(weather.get(0).toString(), WeatherData.class);
        WeatherData weatherData = gson.fromJson(main.toString(), WeatherData.class);
        weatherData.setCityId(cityId);
        weatherData.setTemp(weatherData.getTemp() - Kelvin);
        weatherData.setRain(rainData);
        weatherData.setWind(windData);
        weatherData.setMain(weather.get(0).getAsJsonObject().get("main").toString());

        return weatherData;
      }

      System.out.println(apiRequest);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
