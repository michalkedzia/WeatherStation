module WeatherStation {
  requires javafx.graphics;
  requires javafx.fxml;
  requires javafx.controls;
  requires javafx.base;
  requires javafx.media;
  requires Medusa;

  exports app;

  opens app;
}
