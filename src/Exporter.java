import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Exporter implements Serializable {

    private static final String DEFAULT_FILEPATH = new File(".").getAbsolutePath();
    Map<Integer, Event> events;

    public Exporter(Map<Integer, Event> map) {
        this.events = map;
    }

    public File chooseFilePath(Window mainWindow) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        Date date = new Date();

        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите путь для экспорта событий");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
        fc.setInitialFileName(sdf.format(date));
        fc.setInitialDirectory(new File(DEFAULT_FILEPATH));
        File file = fc.showSaveDialog(mainWindow);

        if (file != null) {
            System.out.println("Successfully selected file saving path. It is: " + file.getAbsolutePath());
        } else {
            System.out.println("Selecting of file saving path was aborted. Exiting the export mode.");
        }
        return file;
    }

    public static void exportToFile(Window mainWindow, Map<Integer, Event> notes) {
        Exporter exp = new Exporter(Controller.eventMemory);
        File file = exp.chooseFilePath(mainWindow);

        StringBuilder sb = new StringBuilder();
        for (Entry<Integer, Event> entry : notes.entrySet()) {
            sb.append("{\n  \"key\": \"").append(entry.getKey()).append("\",\n");
            sb.append("  \"inner_object\":");
            sb.append("  {\n    \"date\": \"").append(entry.getValue().getEventDate()).append("\",\n");
            sb.append("    \"title\": \"").append(entry.getValue().getEventTitle()).append("\",\n");
            sb.append("    \"text\": \"").append(entry.getValue().getEventText()).append("\"\n  }\n},\n");
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try (FileOutputStream fOutStream = new FileOutputStream(file)) {

        // System.out.println("Successfully serialized and exported map with events.");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

}
