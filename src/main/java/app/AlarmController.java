package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AlarmController {

  @FXML private Button settingsButton;

  @FXML private Button homeButton;

  @FXML private Label labelAlarmStatus;

  @FXML private Button buttonON;

  @FXML private Button buttonOFF;

  @FXML private TextField alarmTextField;

  @FXML private Button alarmButtonOK;

  @FXML private Label labelSetAlarm;

  public void initialize() {

    homeButton.setOnAction(
        actionEvent -> {
          Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
          AnchorPane root = null;
          try {
            root = FXMLLoader.load(getClass().getResource("/stationMainPane.fxml"));
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

    if (AlarmSettings.isAlarmActive) {
      labelSetAlarm.setText("Wind speed alarm: " + AlarmSettings.windSpeedAlarm + " km/h");
      labelAlarmStatus.setText("activated");
    }

    buttonON.setOnAction(
        actionEvent -> {
          if (!labelSetAlarm.getText().equals("")) {
            AlarmSettings.isAlarmActive = true;
            labelAlarmStatus.setText("activated");
          }
        });

    buttonOFF.setOnAction(
        actionEvent -> {
          AlarmSettings.isAlarmActive = false;
          labelAlarmStatus.setText("deactivated");
          labelSetAlarm.setText("");
          AlarmSettings.stop();
        });

    alarmButtonOK.setOnAction(
        actionEvent -> {
          double x;
          try {
            x = Double.parseDouble(alarmTextField.getText());
            AlarmSettings.windSpeedAlarm = x;
            labelSetAlarm.setText("Wind speed alarm: " + alarmTextField.getText() + " km/h");
            alarmTextField.setText("");
          } catch (NumberFormatException e) {
            alarmTextField.setText("");
            labelSetAlarm.setText("");
          }
        });
  }
}
