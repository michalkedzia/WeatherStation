package app;

import app.api.ApiCaller;
import app.api.data.WeatherData;

public class SettingsData {
  private static WeatherData weatherDataCity = null;
  private static String cityName = "";

  public static void init() {
    ApiCaller apiCaller = new ApiCaller();
    SettingsData.setWeatherDataCity(apiCaller.getWeatherData(apiCaller.findCity("warsaw")));
    SettingsData.setCityName("WARSAW");
  }

  public static void updateWeatherData(String cityName){
    ApiCaller apiCaller = new ApiCaller();

    if(apiCaller.findCity(cityName) != -1){
      weatherDataCity = apiCaller.getWeatherData(apiCaller.findCity(cityName));
      SettingsData.setCityName(cityName.toUpperCase());
    }
  }

  public static WeatherData getWeatherDataCity() {
    return weatherDataCity;
  }

  public static void setWeatherDataCity(WeatherData weatherDataCity) {
    SettingsData.weatherDataCity = weatherDataCity;
  }

  public static String getCityName() {
    return cityName;
  }

  public static void setCityName(String cityName) {
    SettingsData.cityName = cityName;
  }
}
