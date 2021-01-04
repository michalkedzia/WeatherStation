package app.api.data;
import com.google.gson.annotations.SerializedName;

public class RainData {
  @SerializedName("1h")
  private double hour = 0;

  public double getHour() {
    return hour;
  }

  public void setHour(double hour) {
    this.hour = hour;
  }
}
