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

/**
 * Класс, реализующий функциональность получения уведомлений
 */
public class Notificator {
    private SystemTray tray;
    private Image trayIconImage;

    public Notificator(String trayIconSource) {
        this.tray = SystemTray.getSystemTray();
        this.trayIconImage = Toolkit.getDefaultToolkit().createImage(trayIconSource);
    }

    /**
     * метод для получения событий за текущий день и отображения уведомления о них
     */
    public void sendDailyNotification() {
        if (!SystemTray.isSupported()) {
            Logger.getLogger(Notificator.class.getName())
                    .warning("Системный трей не поддерживается. Уведомление не было выведено.");
        }
        try {
            TrayIcon trayIcon = initTrayIcon();
            tray.add(trayIcon);
            Set<Entry<Integer, Event>> set = Controller.getEvents().entrySet();
            List<String> todayEventsTitles = new ArrayList<>();
            EventController ec = new EventController();
            Calendar currDate = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
            for (Entry<Integer, Event> entryElem : set) {
                if (Objects.equals(entryElem.getKey() / 100,
                        ec.getKeyForChosenDate(sdf.format(currDate.getTime())) / 100)) {
                    todayEventsTitles.add(entryElem.getValue().getEventTitle());
                }
            }
            // StringBuilder title = new StringBuilder();
            String titleString = "Ваши события на сегодня";
            StringBuilder messageText = new StringBuilder("Общее количество событий: ");
            messageText.append(todayEventsTitles.size()).append("\n");
            if (0 == todayEventsTitles.size()) {
                messageText.append("Событий нет. \nМожно отдыхать!\n");
            } else if (todayEventsTitles.size() < 4) {
                for (int index = 0; index < todayEventsTitles.size(); index++) {
                    messageText.append(todayEventsTitles.get(index)).append("\n");
                }
            } else {
                messageText.append(todayEventsTitles.get(0)).append("\n").append(todayEventsTitles.get(1)).append("\n")
                        .append("и другие события\n");
            }

            trayIcon.displayMessage(titleString, messageText.toString().substring(0, messageText.length() - 1),
                    MessageType.INFO);
            tray.remove(trayIcon);
        } catch (AWTException e) {
            Logger.getLogger(Notificator.class.getName())
                    .warning("При добавлении иконки в трей произошла ошибка. " + e.getMessage());
        }
    }

    /**
     * метод для инициализации иконки приложения в трее, если данная
     * функциональность поддеривается операционной системой
     * 
     * @return созданная иконка либо
     */
    private TrayIcon initTrayIcon() {
        TrayIcon trayIcon = new TrayIcon(this.trayIconImage, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        return trayIcon;
    }
}
