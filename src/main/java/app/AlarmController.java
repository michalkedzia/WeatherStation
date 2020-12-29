package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AlarmController {

  @FXML private Button settingsButton;

  @FXML private Button homeButton;

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
  }
}
