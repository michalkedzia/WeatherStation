package app.api.data;

public class WeatherData {
  private int cityId = -1;
  private String description = "";
  private double temp = 0;
  private int pressure = 0;
  private int humidity = 0;
  private WindData wind;
  private RainData rain;

  public int getCityId() {
    return cityId;
  }

  public void setCityId(int cityId) {
    this.cityId = cityId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }

  public int getPressure() {
    return pressure;
  }

  public void setPressure(int pressure) {
    this.pressure = pressure;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public WindData getWind() {
    return wind;
  }

  public void setWind(WindData wind) {
    this.wind = wind;
  }

  public RainData getRain() {
    return rain;
  }

  public void setRain(RainData rain) {
    this.rain = rain;
  }
}
