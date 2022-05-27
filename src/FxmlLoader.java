import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import loaders.EventLoader;
import loaders.SettingsLoader;
import savers.EventSaver;

import java.util.Objects;

public class FxmlLoader extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/DlgGeo.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Bellerose.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/MuseoSansCyrl.ttf"), 14);
            Controller.setThemeMod(SettingsLoader.loadSettingsFromFile());
            Controller.setEvents(EventLoader.loadEventsFromFile());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
            Scene mainScene = new Scene(root);
            primaryStage.getIcons().add(new Image("icons/icon_128.png"));
            primaryStage.setTitle("Календарь");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(windowEvent -> Controller.stopShowTime = true);
            Notificator notificator = new Notificator("src\\icons\\icon_128.png");
            notificator.sendDailyNotification();
        } catch (Exception e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }

    }

    /**
     * Записываем событий в файл при завершении программы
     */
    @Override
    public void stop() {
        EventSaver eSaver = new EventSaver(Controller.getEvents());
        eSaver.saveEvents();
        Theme.saveThemeMod();
        Notificator.removeTrayIcon();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
