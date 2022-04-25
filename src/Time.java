import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time extends Controller {
    private final int MILLIS_OF_SLEEP = 1000;

    public Time(Label currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Отображение текущего времени
     */
    public void printTimeNow() {
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            while (!stopShowTime) {
                try {
                    Thread.sleep(MILLIS_OF_SLEEP);
                } catch (Exception e) {
                    System.out.println("Error.");
                }
                final String timeNow = sdf.format(new Date());
                Platform.runLater(() -> currentTime.setText(timeNow));
            }
        });
        thread.start();
    }
}
