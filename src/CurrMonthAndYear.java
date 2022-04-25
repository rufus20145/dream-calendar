import javafx.scene.text.Text;

import java.time.LocalDate;

public class CurrMonthAndYear extends Controller {

    public CurrMonthAndYear(Text currentMonthText, LocalDate currentDate) {
        this.currentMonthText = currentMonthText;
        this.currentDate = currentDate;
    }


    public void setCurrentMonthText() {
        String monthTitle = getRusMonth(currentDate.getMonthValue());
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());
    }

    /**
     * Получение названия месяца на русском языке в им. падеже для currentMonthText
     */
    public String getRusMonth(int month) {
        return switch (month) {
            case (1) -> "ЯНВАРЬ";
            case (2) -> "ФЕВРАЛЬ";
            case (3) -> "МАРТ";
            case (4) -> "АПРЕЛЬ";
            case (5) -> "МАЙ";
            case (6) -> "ИЮНЬ";
            case (7) -> "ИЮЛЬ";
            case (8) -> "АВГУСТ";
            case (9) -> "СЕНТЯБРЬ";
            case (10) -> "ОКТЯБРЬ";
            case (11) -> "НОЯБРЬ";
            case (12) -> "ДЕКАБРЬ";
            default -> null;
        };
    }
}