import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class FxmlLoader extends Application {

    /**
     *
     */
    private static final String APPLICATION_ICON = "icons/icon_128.png";

    @Override
    public void start(Stage primaryStage) {
        try {
            Controller.setEvents(EventLoader.loadEventsFromFile());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
            Scene mainScene = new Scene(root);
            primaryStage.getIcons().add(new Image(APPLICATION_ICON));
            primaryStage.setTitle("Календарь");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(windowEvent -> Controller.stopShowTime = true);
        } catch (Exception e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        Notificator notificator = new Notificator(APPLICATION_ICON);
        notificator.sendDailyNotification();
    }

    /**
     * Записываем событий в файл при завершении программы
     */
    @Override
    public void stop() {
        EventSaver eSaver = new EventSaver(Controller.getEvents());
        eSaver.saveEvents();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
