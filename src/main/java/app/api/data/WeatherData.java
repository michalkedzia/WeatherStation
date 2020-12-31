package app.api.data;

public class WeatherData {
  private String main;
  private double temp;
  private int pressure;
  private int humidity;
  private WindData wind;
  private RainData rain;

  public String getMain() {
    return main;
  }

  public void setMain(String description) {
    this.main = description;
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
