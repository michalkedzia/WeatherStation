package app;

import app.api.ApiCaller;
import app.api.data.WeatherData;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class StationMainPaneController {

  @FXML private Button settingsButton;

  @FXML private Label outdoorTempLabel;

  @FXML private Label outdoorHumidityLabel;

  @FXML private Label windSpeedLabel;

  @FXML private Label rainFallAccLabel;

  @FXML private BarChart<String, Double> presureTrendChart;

  @FXML private HBox midHBbox;

  @FXML private ImageView weatherIcon;

  @FXML private ImageView cityWeatherIcon;

  @FXML private Label cityLabel;

  @FXML private Label cityTempLabel;

  @FXML private ImageView moonIcon;

  @FXML private Label indoorTempLabel;

  @FXML private Label indoorHumidityLabel;

  @FXML private Label hoursMinutesLabel;

  @FXML private Label secondsLabel;

  @FXML private Label dayLabel;

  @FXML private Label monthLabel;

  @FXML private Label dayWeekLabel;

  @FXML private Button alarmButton;

  private Gauge gauge;
  private ObservableList<XYChart.Series<String, Double>> list;

  private WeatherData weatherData;
  private boolean executors = true;

  public void initialize() {

    gauge =
        GaugeBuilder.create()
            .minValue(0)
            .maxValue(359)
            .startAngle(180)
            .angleRange(360)
            .autoScale(false)
            .customTickLabelsEnabled(true)
            .customTickLabels("N")
            .customTickLabelFontSize(50)
            .minorTickMarksVisible(false)
            .mediumTickMarksVisible(false)
            .majorTickMarksVisible(false)
            .valueVisible(false)
            .needleType(Gauge.NeedleType.BIG)
            .needleShape(Gauge.NeedleShape.FLAT)
            .knobType(Gauge.KnobType.FLAT)
            .knobColor(Gauge.BRIGHT_COLOR)
            .borderPaint(Gauge.BRIGHT_COLOR)
            .build();

    gauge.setMaxSize(200, 200);

    gauge.setLayoutX(375);
    gauge.setLayoutY(150);
    gauge.setTickLabelColor(Color.WHITE);
    gauge.setValue(100);
    gauge.setTitleColor(Color.WHITE);
    gauge.setTitle("Wind direction");
    midHBbox.getChildren().add(gauge);

    initializeTimeDate();
    initializeChart();

    ApiCaller apiCaller = new ApiCaller();
    try {
      apiCaller.initialize();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    weatherData = apiCaller.getWeatherData(apiCaller.findCity("łódź"));

    cityLabel.setText(SettingsData.getCityName());

    settingsButton.setOnAction(
        actionEvent -> {
          Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
          AnchorPane root = null;
          try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/settingsPane.fxml"));
            root = loader.load();
            SettingsController settingsController = loader.getController();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Scene scene = new Scene(root, 1000, 700);
          scene
              .getStylesheets()
              .add(getClass().getResource("/custom-font-styles.css").toExternalForm());
          stage.setScene(scene);
          stage.show();
        });

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Runnable runnable =
        () -> {
          while (executors) {
            try {
              Thread.sleep(1000);
            } catch (Exception e) {
              e.printStackTrace();
            }

            double wnd = Double.parseDouble(windSpeedLabel.getText());
            if (AlarmSettings.isAlarmActive) {
              if (wnd >= AlarmSettings.windSpeedAlarm) {
                AlarmSettings.play();
              } else AlarmSettings.stop();
            }
          }
        };

    executorService.execute(runnable);

    alarmButton.setOnAction(
        actionEvent -> {
          Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
          AnchorPane root = null;
          try {
            root = FXMLLoader.load(getClass().getResource("/alarmPane.fxml"));
          } catch (IOException e) {
            e.printStackTrace();
          }
          Scene scene = new Scene(root, 1000, 700);
          executors = false;
          scene
              .getStylesheets()
              .add(getClass().getResource("/custom-font-styles.css").toExternalForm());
          stage.setScene(scene);
          stage.show();
        });
  }

  void changeIcon(ImageView imageView, String src) {
    Image image = new Image(getClass().getResource(src).toString());
    Platform.runLater(() -> imageView.setImage(image));
  }

  void updateIcons() {
    switch (SettingsData.getWeatherDataCity().getDescription()) {
      case "\"clear sky\"":
        changeIcon(cityWeatherIcon, WeatherIcons.SUN);
        break;
      case "\"shower rain\"":
      case "\"heavy intensity rain\"":
      case "\"very heavy rain\"":
      case "\"extreme rain\"":
      case "\"freezing rain\"":
      case "\"heavy intensity shower rain\"":
      case "\"ragged shower rain\"":
        changeIcon(cityWeatherIcon, WeatherIcons.MODERATE_RAIN);
        break;
      case "\"rain\"":
      case "\"moderate rain\"":
      case "\"light intensity shower rain\"":
        changeIcon(cityWeatherIcon, WeatherIcons.RAIN);
        break;
      case "\"light rain\"":
      case "\"drizzle\"":
      case "\"light intensity drizzle\"":
      case "\"heavy intensity drizzle\"":
      case "\"drizzle rain\"":
      case "\"heavy intensity drizzle rain\"":
      case "\"shower rain and drizzle\"":
      case "\"heavy shower rain and drizzle\"":
      case "\"shower drizzle\"":
        changeIcon(cityWeatherIcon, WeatherIcons.LIGHT_RAIN);
        break;
      case "\"thunderstorm\"":
      case "\"thunderstorm with light rain\"":
      case "\"thunderstorm with rain\"":
      case "\"thunderstorm with heavy rain\"":
      case "\"light thunderstorm\"":
      case "\"heavy thunderstorm\"":
      case "\"ragged thunderstorm\"":
      case "\"thunderstorm with light drizzle\"":
      case "\"thunderstorm with drizzle\"":
      case "\"thunderstorm with heavy drizzle\"":
        changeIcon(cityWeatherIcon, WeatherIcons.STORM);
        break;
      case "\"snow\"":
      case "\"heavy snow\"":
      case "\"shower snow\"":
      case "\"heavy shower snow\"":
        changeIcon(cityWeatherIcon, WeatherIcons.SNOW);
        break;
      case "\"light snow\"":
      case "\"light shower snow\"":
        changeIcon(cityWeatherIcon, WeatherIcons.LIGHT_SNOW);
        break;
      case "\"mist\"":
      case "\"haze\"":
      case "\"smoke\"":
      case "\"fog\"":
        changeIcon(cityWeatherIcon, WeatherIcons.HAZE);
        break;
      case "\"sleet\"":
      case "\"light shower sleet\"":
      case "\"shower sleet\"":
      case "\"light rain and snow\"":
      case "\"rain and snow\"":
        changeIcon(cityWeatherIcon, WeatherIcons.SLEET);
        break;
      case "\"broken clouds\"":
      case "\"scattered clouds\"":
      case "\"few clouds\"":
      case "\"overcast clouds\"":
      default:
        changeIcon(cityWeatherIcon, WeatherIcons.CLOUDS);
    }

    switch (weatherData.getDescription()) {
      case "\"clear sky\"":
        changeIcon(weatherIcon, WeatherIcons.SUN);
        break;
      case "\"shower rain\"":
      case "\"heavy intensity rain\"":
      case "\"very heavy rain\"":
      case "\"extreme rain\"":
      case "\"freezing rain\"":
      case "\"heavy intensity shower rain\"":
      case "\"ragged shower rain\"":
        changeIcon(weatherIcon, WeatherIcons.MODERATE_RAIN);
        break;
      case "\"rain\"":
      case "\"moderate rain\"":
      case "\"light intensity shower rain\"":
        changeIcon(weatherIcon, WeatherIcons.RAIN);
        break;
      case "\"light rain\"":
      case "\"drizzle\"":
      case "\"light intensity drizzle\"":
      case "\"heavy intensity drizzle\"":
      case "\"drizzle rain\"":
      case "\"heavy intensity drizzle rain\"":
      case "\"shower rain and drizzle\"":
      case "\"heavy shower rain and drizzle\"":
      case "\"shower drizzle\"":
        changeIcon(weatherIcon, WeatherIcons.LIGHT_RAIN);
        break;
      case "\"thunderstorm\"":
      case "\"thunderstorm with light rain\"":
      case "\"thunderstorm with rain\"":
      case "\"thunderstorm with heavy rain\"":
      case "\"light thunderstorm\"":
      case "\"heavy thunderstorm\"":
      case "\"ragged thunderstorm\"":
      case "\"thunderstorm with light drizzle\"":
      case "\"thunderstorm with drizzle\"":
      case "\"thunderstorm with heavy drizzle\"":
        changeIcon(weatherIcon, WeatherIcons.STORM);
        break;
      case "\"snow\"":
      case "\"heavy snow\"":
      case "\"shower snow\"":
      case "\"heavy shower snow\"":
        changeIcon(weatherIcon, WeatherIcons.SNOW);
        break;
      case "\"light snow\"":
      case "\"light shower snow\"":
        changeIcon(weatherIcon, WeatherIcons.LIGHT_SNOW);
        break;
      case "\"mist\"":
      case "\"haze\"":
      case "\"smoke\"":
      case "\"fog\"":
        changeIcon(weatherIcon, WeatherIcons.HAZE);
        break;
      case "\"sleet\"":
      case "\"light shower sleet\"":
      case "\"shower sleet\"":
      case "\"light rain and snow\"":
      case "\"rain and snow\"":
        changeIcon(weatherIcon, WeatherIcons.SLEET);
        break;
      case "\"broken clouds\"":
      case "\"scattered clouds\"":
      case "\"few clouds\"":
      case "\"overcast clouds\"":
      default:
        changeIcon(cityWeatherIcon, WeatherIcons.CLOUDS);
    }
  }

  void initializeChart() {
    list = FXCollections.observableArrayList();
    XYChart.Series<String, Double> aSeries = new XYChart.Series<String, Double>();
    aSeries.getData().add(new XYChart.Data("Now", 1000));
    aSeries.getData().add(new XYChart.Data("+1 Hour", 1574));
    aSeries.getData().add(new XYChart.Data("+2 Hour", 1451));
    aSeries.getData().add(new XYChart.Data("+3 Hour", 1100));
    aSeries.getData().add(new XYChart.Data("+4 Hour", 1300));
    aSeries.getData().add(new XYChart.Data("+5 Hour", 1000));
    list.add(aSeries);
    presureTrendChart.setData(list);
  }

  // test - > metoda do symulowania zmian
  private String add(String string, double d) {
    double result = Double.parseDouble(string) + d;
    String format = String.format("%.1f", result);
    return format.replace(",", ".");
  }

  private void initializeTimeDate() {
    Date date = new Date();
    DateFormat day = new SimpleDateFormat("dd");
    DateFormat month = new SimpleDateFormat("MM");
    Calendar calendar = Calendar.getInstance();

    dayLabel.setText(day.format(date));
    monthLabel.setText(month.format(date));
    dayWeekLabel.setText(
        new SimpleDateFormat("EE", Locale.ENGLISH)
            .format(calendar.getTime().getTime())
            .toUpperCase());

    AtomicInteger pressureCounter = new AtomicInteger();
    Timeline clock =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                e -> {
                  LocalTime currentTime = LocalTime.now();
                  Platform.runLater(
                      () -> {
                        String h = String.format("%02d", currentTime.getHour());
                        String m = String.format("%02d", currentTime.getMinute());
                        String s = String.format("%02d", currentTime.getSecond());
                        hoursMinutesLabel.setText(h + ":" + m);
                        secondsLabel.setText(s);

                        // test ->start
                        Random rand = new Random();
                        double random = rand.nextDouble();
                        random = random - 0.5;

                        gauge.setValue(weatherData.getWind().getDeg() + (rand.nextInt(6) - 3));

                        //                        int rr = (int) random * 10;
                        //                        random = random - 0.5 + random / 10;

                        updateIcons();
                        indoorTempLabel.setText(
                            Double.toString(Math.round((20 + random) * 10) / 10.0));
                        indoorHumidityLabel.setText(
                            Double.toString(Math.round((30 + random) * 10) / 10.0));
                        outdoorHumidityLabel.setText(Double.toString(weatherData.getHumidity()));
                        outdoorTempLabel.setText(
                            Double.toString(
                                Math.round((weatherData.getTemp() + random) * 10) / 10.0));
                        windSpeedLabel.setText(
                            Double.toString(
                                Math.round((weatherData.getWind().getSpeed() + random) * 10)
                                    / 10.0));
                        rainFallAccLabel.setText(Double.toString(weatherData.getRain().getHour()));
                        cityLabel.setText(SettingsData.getCityName());
                        cityTempLabel.setText(
                            Double.toString(Math.round(SettingsData.getWeatherDataCity().getTemp() * 10) / 10.0));

                        if(pressureCounter.get() == 5){
                          XYChart.Series<String, Double> aSeries =
                              new XYChart.Series<String, Double>();
                          aSeries.getData().add(new XYChart.Data("Now", weatherData.getPressure()));
                          aSeries.getData().add(new XYChart.Data("+1 Hour", 1274 + random));
                          aSeries.getData().add(new XYChart.Data("+2 Hour", 1151 + random));
                          aSeries.getData().add(new XYChart.Data("+3 Hour", 1100 + random));
                          aSeries.getData().add(new XYChart.Data("+4 Hour", 1300 + random));
                          aSeries.getData().add(new XYChart.Data("+5 Hour", 1000 + random));
                          list.clear();
                          list.add(aSeries);
                          // koniec
                        }
                        pressureCounter.set((pressureCounter.get() + 1) % 6);
                      });
                }),
            new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();
  }

  public void setCityLabel(String cityLabel) {
    this.cityLabel.setText(cityLabel.toUpperCase());
  }

  public String getCityLabel() {
    return this.cityLabel.toString();
  }
}
