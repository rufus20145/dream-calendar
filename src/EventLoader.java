import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class EventLoader {
    private static final String DEFAULT_FILEPATH = System.getenv("APPDATA") + "\\dream-calendar\\eventsData.json";

    protected EventLoader() {
    };

    public static SortedMap<Integer, Event> loadEventsFromFile() {
        return loadEventsFromFile(DEFAULT_FILEPATH);
    }

    public static SortedMap<Integer, Event> loadEventsFromFile(String filePath) {
        File file = new File(filePath);
        SortedMap<Integer, Event> eventsFromFile = null;
        Type type = new TypeToken<TreeMap<Integer, Event>>() {
        }.getType();
        Gson gson = new Gson();
        if (file.exists()) {
            try (FileReader fReader = new FileReader(file); BufferedReader bReader = new BufferedReader(fReader)) {
                eventsFromFile = gson.fromJson(bReader.readLine(), type);
            } catch (JsonSyntaxException e) {
                Logger.getLogger(EventLoader.class.getName())
                        .warning("При парсинге JSON произошла ошибка. " + e.getMessage());
            } catch (IOException e) {
                Logger.getLogger(EventLoader.class.getName())
                        .severe("При создании файла произошла ошибка. " + e.getMessage());
            }
        }
        return eventsFromFile;
    }
}
