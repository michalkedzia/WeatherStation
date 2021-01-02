package app;

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
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

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

  @FXML private Button alarmButton;

  private MediaPlayer mediaPlayer;
  private Gauge gauge;
  private ObservableList<XYChart.Series<String, Double>> list;
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
                        gauge.setValue(gauge.getValue() + 5);
                        Random rand = new Random();
                        double random = rand.nextInt(5);
                        int rr = (int) random * 10;
                        random = random - 2 + random / 10;

                        indoorTempLabel.setText(add(indoorTempLabel.getText(), random));
                        indoorHumidityLabel.setText(add(indoorHumidityLabel.getText(), random));
                        outdoorHumidityLabel.setText(add(outdoorHumidityLabel.getText(), random));
                        outdoorTempLabel.setText(add(outdoorTempLabel.getText(), random));
                        windSpeedLabel.setText(add(windSpeedLabel.getText(), random));
                        rainFallAccLabel.setText(add(rainFallAccLabel.getText(), random));

                        XYChart.Series<String, Double> aSeries =
                            new XYChart.Series<String, Double>();
                        aSeries.getData().add(new XYChart.Data("Now", 1000 + rr));
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
