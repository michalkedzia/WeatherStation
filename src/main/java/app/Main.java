package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import eu.hansolo.medusa.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {

    Font.loadFont(getClass().getResource("/DSEG14ClassicMini-Bold.ttf").toExternalForm(), 10);
    AnchorPane root = FXMLLoader.load(getClass().getResource("/stationMainPane.fxml"));
    primaryStage.setTitle("WeatherStation");

    Scene scene = new Scene(root, 1000, 700);
    scene.getStylesheets().add(getClass().getResource("/custom-font-styles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
