import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage stg;

    @Override
    public void start(Stage primaryStage) {
        stg = primaryStage;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            Scene mainScene = new Scene(root);
            primaryStage.getIcons().add(new Image("icons/icon_128.png"));
            primaryStage.setTitle("Календарь");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void changeScene(String fxml) throws IOException {
//        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
//        stg.getIcons().add(new Image("icons/icon_128.png"));
//        stg.setTitle("Новая заметка");
//        stg.setScene(new Scene(pane, 600, 400));
//        stg.show();
//        stg.setResizable(false);
//    }
//
//    public void startScene(String fxml) throws IOException {
//        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
//        stg.getIcons().add(new Image("icons/icon_128.png"));
//        stg.setTitle("Календарь");
//        stg.setScene(new Scene(pane, 1160, 680));
//        stg.show();
//        stg.setResizable(false);
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
