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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class StationMainPaneController {

  @FXML private Label outdoorTempLabel;

  @FXML private Label outdoorHumidityLabel;

  @FXML private Label windSpeedLabel;

  @FXML private Label rainFallAccLabel;

  @FXML private BarChart<String, Double> presureTrendChart;

  @FXML private HBox midHBbox;

  @FXML private ImageView weatherIcon;

  @FXML private ImageView moonIcon;

  @FXML private Label indoorTempLabel;

  @FXML private Label indoorHumidityLabel;

  @FXML private Label hoursMinutesLabel;

  @FXML private Label secondsLabel;

  @FXML private Label dayLabel;

  @FXML private Label monthLabel;

  @FXML private Label dayWeekLabel;

  private Gauge gauge;
  private ObservableList<XYChart.Series<String, Double>> list;

  WeatherData weatherData;

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

    weatherData = apiCaller.getWeatherData(apiCaller.findCountry("moscow"));
  }

  void changeIcon(ImageView imageView, String src) {
    Image image = new Image(getClass().getResource(src).toString());
    Platform.runLater(() -> imageView.setImage(image));
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
                        gauge.setValue(weatherData.getWind().getDeg());
                        Random rand = new Random();
                        double random = rand.nextInt(5);
                        int rr = (int) random * 10;
                        random = random - 2 + random / 10;

                        indoorTempLabel.setText(Integer.toString(20));
                        indoorHumidityLabel.setText(Integer.toString(30));
                        outdoorHumidityLabel.setText(Integer.toString(weatherData.getHumidity()));
                        outdoorTempLabel.setText(Double.toString(weatherData.getTemp()));
                        windSpeedLabel.setText(Double.toString(weatherData.getWind().getSpeed()));
                        rainFallAccLabel.setText(Double.toString(weatherData.getRain().getHour()));

                        XYChart.Series<String, Double> aSeries =
                            new XYChart.Series<String, Double>();
                        aSeries.getData().add(new XYChart.Data("Now", weatherData.getPressure()));
                        aSeries.getData().add(new XYChart.Data("+1 Hour", 1274 + rr));
                        aSeries.getData().add(new XYChart.Data("+2 Hour", 1151 + rr));
                        aSeries.getData().add(new XYChart.Data("+3 Hour", 1100 + rr));
                        aSeries.getData().add(new XYChart.Data("+4 Hour", 1300 + rr));
                        aSeries.getData().add(new XYChart.Data("+5 Hour", 1000 + rr));
                        list.clear();
                        list.add(aSeries);
                        // koniec

                      });
                }),
            new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();
  }
}
