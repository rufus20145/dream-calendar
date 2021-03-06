import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import events.Event;

/**
 * Класс, реализующий функциональность получения уведомлений
 */
public class Notificator {
    private static TrayIcon trayIcon;
    private static SystemTray tray;
    private Image trayIconImage;

    public Notificator(String trayIconSource) {
        tray = SystemTray.getSystemTray();
        this.trayIconImage = Toolkit.getDefaultToolkit().createImage(trayIconSource);
    }

    /**
     * метод для получения событий за текущий день и отображения уведомления о них
     */
    public void sendDailyNotification() {
        if (!SystemTray.isSupported()) {
            Logger.getLogger(Notificator.class.getName())
                    .warning("Системный трей не поддерживается. Уведомление не было выведено.");
        } else {
            try {
                trayIcon = initTrayIcon();
                tray.add(trayIcon);
                Set<Entry<Integer, Event>> set = Controller.getEvents().entrySet();
                List<String> todayEventsTitles = new ArrayList<>();
                EventController ec = new EventController();
                Calendar currDate = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                for (Entry<Integer, Event> entryElem : set) {
                    if (Objects.equals(entryElem.getKey() / 100,
                            ec.getKeyForChosenDate(sdf.format(currDate.getTime())) / 100)) {
                        todayEventsTitles.add(entryElem.getValue().getEventTitle());
                    }
                }
                String titleString = "Ваши события на сегодня";
                StringBuilder messageText = new StringBuilder("Общее количество событий: ");
                messageText.append(todayEventsTitles.size()).append("\n");
                if (todayEventsTitles.isEmpty()) {
                    messageText.append("Событий нет. \nМожно отдыхать!\n");
                } else if (todayEventsTitles.size() < 4) {
                    for (int index = 0; index < todayEventsTitles.size(); index++) {
                        messageText.append(todayEventsTitles.get(index)).append("\n");
                    }
                } else {
                    messageText.append(todayEventsTitles.get(0)).append("\n").append(todayEventsTitles.get(1))
                            .append("\n")
                            .append("и другие события\n");
                }

                trayIcon.displayMessage(titleString, messageText.toString().substring(0, messageText.length() - 1),
                        MessageType.INFO);
            } catch (AWTException e) {
                Logger.getLogger(Notificator.class.getName())
                        .warning("При добавлении иконки в трей произошла ошибка. " + e.getMessage());
            }
        }
    }

    /**
     * метод для инициализации иконки приложения в трее, если данная
     * функциональность поддерживается операционной системой
     * 
     * @return созданная иконка либо
     */
    private TrayIcon initTrayIcon() {
        TrayIcon newTrayIcon = new TrayIcon(this.trayIconImage, "Dream Calendar");
        newTrayIcon.setImageAutoSize(true);

        return newTrayIcon;
    }

    /**
     * удаление иконки приложения из системного трея
     */
    public static void removeTrayIcon() {
        tray.remove(trayIcon);
    }

}
