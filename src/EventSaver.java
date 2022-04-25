import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class EventSaver {

    /**
     *
     */
    private static final String FOLDERNAME = "\\dream-calendar\\";
    /**
     *
     */
    private static final String DEFAULT_FILENAME = "eventsData.json";
    private Map<Integer, Event> events;

    public EventSaver(Map<Integer, Event> events) {
        this.events = events;
    }

    public void saveEvents() {
        String appDataPath = System.getenv("APPDATA") + FOLDERNAME;
        File folder = new File(appDataPath);
        folder.mkdir();
        File file = new File(appDataPath + DEFAULT_FILENAME);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Logger.getLogger(EventSaver.class.getName())
                            .info("Успешно создан файл для сохранения событий.");
                }
            } catch (IOException e) {
                Logger.getLogger(EventSaver.class.getName())
                        .severe("При создании файла произошла ошибка. " + e.getMessage());
                return;
            }
        }

        Gson gson = new Gson();
        try (FileWriter fWriter = new FileWriter(file); BufferedWriter bWriter = new BufferedWriter(fWriter)) {
            bWriter.write(gson.toJson(events, events.getClass()));
        } catch (IOException e) {
            Logger.getLogger(EventSaver.class.getName())
                    .warning("При записи в файл произошла ошибка. " + e.getMessage());
        }
    }
}
