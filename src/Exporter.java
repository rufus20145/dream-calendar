import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javafx.stage.FileChooser;

public class Exporter {

    /**
     *
     */
    private static final String DEFAULT_FILEPATH = new File(".").getAbsolutePath();
    HashMap<Integer, Event> events;

    public Exporter(HashMap<Integer, Event> map) {
        this.events = map;
    }

    public File chooseFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        Date date = new Date();

        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите путь для экспорта событий");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
        fc.setInitialFileName(sdf.format(date));
        fc.setInitialDirectory(new File(DEFAULT_FILEPATH));
        File file = fc.showSaveDialog(null);

        if (file != null) {
            return file;
        } else {
            return null; // TODO возвращать путь по умолчанию
        }
    }
}
