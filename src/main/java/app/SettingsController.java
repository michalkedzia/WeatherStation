package app;

import app.api.ApiCaller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SettingsController {

  @FXML private Button homeButton;

  @FXML private TextField cityNameTextField;

  @FXML private Button cityNameButtonOK;

  @FXML private Label labelSetCityName;

  public void initialize() {

    // labelSetCityName.setText("Tracking" + stationMainPaneController.getCityLabel().toUpperCase()
    // + "...");

    homeButton.setOnAction(
        actionEvent -> {
          Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
          AnchorPane root = null;
          try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stationMainPane.fxml"));
            root = loader.load();
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

    cityNameButtonOK.setOnAction(
        actionEvent -> {
          try {
            ApiCaller apiCaller = new ApiCaller();
            apiCaller.initialize();

            if (apiCaller.findCity(cityNameTextField.getText()) != -1) {
              SettingsData.updateWeatherData(cityNameTextField.getText());
              labelSetCityName.setText(
                  "Tracking " + cityNameTextField.getText().toUpperCase() + " ...");
            } else {
              labelSetCityName.setText("Wrong city name");
            }

          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        });
  }
}
