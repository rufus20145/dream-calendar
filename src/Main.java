import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            Scene mainScene = new Scene(root);

            mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().toString().equalsIgnoreCase("E") && event.isControlDown()) {
                        System.out.println("Start export to file.");
                        Exporter.exportToFile(primaryStage, Controller.eventMemory);
                    }
                }
            });

            primaryStage.getIcons().add(new Image("icons/icon_128.png"));
            primaryStage.setTitle("Календарь");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
