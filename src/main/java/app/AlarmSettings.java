package app;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class AlarmSettings {
  public static double windSpeedAlarm = 0.0;
  public static boolean isAlarmActive = false;
  private static String musicFile = "src/main/resources/alarm.mp3";
  private static MediaPlayer mediaPlayer = null;

  public static void init() {
    Media sound = new Media(new File(musicFile).toURI().toString());
    mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
  }

  public static void play() {
    if (mediaPlayer == null) {
      init();
    }
    if(!(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))){
      mediaPlayer.play();
    }
  }

  public static void stop() {
    if(mediaPlayer!=null){
      if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
      }
    }



  }
}
