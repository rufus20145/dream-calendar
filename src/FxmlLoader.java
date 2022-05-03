import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FxmlLoader extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
            Scene mainScene = new Scene(root);
            Controller.setEvents(EventLoader.loadEventsFromFile());
            primaryStage.getIcons().add(new Image("icons/icon_128.png"));
            primaryStage.setTitle("Календарь");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(windowEvent -> Controller.stopShowTime = true);
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
