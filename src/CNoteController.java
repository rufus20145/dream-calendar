//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.stage.Stage;
//
//import java.awt.event.ActionEvent;
//import java.io.IOException;
//
//public class CNoteController {
//
//    @FXML
//    private TextField nameNewNote;
//
//    @FXML
//    private Button saveNewNote;
//
//    @FXML
//    private TextField textNewNote;
//
//    @FXML
//    void sendToScene1Action() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
//        Parent root = loader.load();
//        Controller scene1Controller = loader.getController();
//        scene1Controller.putInformationFromNote(currDateForNewNote, getNameNewNoteString(), getTextNewNoteString());
//
//        //close scene create new note
//        Stage stage = (Stage) saveNewNote.getScene().getWindow();
//        stage.close();
//    }
//
//    String currDateForNewNote;
//    public void takeCurrDateForNewNote(String currDate) {
//        currDateForNewNote = currDate;
//    }
//
//    public String getNameNewNoteString() {
//        return nameNewNote.getText();
//    }
//
//    public String getTextNewNoteString() {
//        return textNewNote.getText();
//    }
//}
