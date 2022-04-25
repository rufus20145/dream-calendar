import javafx.scene.text.Text;

import java.time.LocalDate;

public class ConstCurrentDate extends Controller {

    public ConstCurrentDate(Text currentDayConst, LocalDate currentDate) {
        this.currentDayConst = currentDayConst;
        this.currentDate = currentDate;
    }

    /**
     * Вывод сегодняшней даты в виде: число + название месяца
     */
    public void printCurrentDayLeftTopTitle() {
        if (!currentDayLeftTopDetected) {
            String currentDayConstString = "" + currentDate.getDayOfMonth() + " " + getRusMonthInclination(currentDate.getMonthValue());
            currentDayConst.setText(currentDayConstString);
            currentDayLeftTopDetected = true;
        }
    }

    /**
     * Получение названия месяца на русском языке в род. падеже для currentMonthText
     */
    private String getRusMonthInclination(int month) {
        return switch (month) {
            case (1) -> "ЯНВАРЯ";
            case (2) -> "ФЕВРАЛЯ";
            case (3) -> "МАРТА";
            case (4) -> "АПРЕЛЯ";
            case (5) -> "МАЯ";
            case (6) -> "ИЮНЯ";
            case (7) -> "ИЮЛЯ";
            case (8) -> "АВГУСТА";
            case (9) -> "СЕНТЯБРЯ";
            case (10) -> "ОКТЯБРЯ";
            case (11) -> "НОЯБРЯ";
            case (12) -> "ДЕКАБРЯ";
            default -> null;
        };
    }
}
