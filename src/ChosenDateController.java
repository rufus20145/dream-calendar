import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class ChosenDateController extends Controller {
    private Node textElementCurrentDay;
    public static Pane cellElementCurrentDate;

    public ChosenDateController(Text chosenDateText, LocalDate currentDate, AnchorPane anchorPane, GridPane gridPane) {
        this.chosenDateText = chosenDateText;
        this.currentDate = currentDate;
        this.anchorPane = anchorPane;
        this.gridPane = gridPane;
    }

    /**
     * Отображение в chosenDateText сегодняшней даты
     */
    public void showCurrentDate() {
        if (LocalDate.now().getMonthValue() == currentDate.getMonthValue() && LocalDate.now().getYear() == currentDate.getYear()) {
            for (Node element : anchorPane.getChildren()) {
                if (element instanceof Text && !(((Text) element).getText()).isEmpty() && Integer.parseInt(((Text) element).getText()) == LocalDate.now().getDayOfMonth()) {
                    textElementCurrentDay = element;
                    for (Node node : gridPane.getChildren()) {
                        if (node.getStyle().equals(CHOSEN_CELL_STYLE_FOR_LIGHT) && node.getStyle().equals(CHOSEN_CELL_STYLE_FOR_DARK)) {
                            cellSelected = true;
                        }
                    }
                    if (cellSelected) {
                        String getMonthValueWithZero, getDayValueWithZero;
                        if (currentDateLD.getMonthValue() < 10) {
                            getMonthValueWithZero = "0" + currentDateLD.getMonthValue();
                        } else {
                            getMonthValueWithZero = "" + currentDateLD.getMonthValue();
                        }
                        if (currentDateLD.getDayOfMonth() < 10) {
                            getDayValueWithZero = "0" + currentDateLD.getDayOfMonth();
                        } else {
                            getDayValueWithZero = "" + currentDateLD.getDayOfMonth();
                        }
                        currentDateString = getDayValueWithZero + "." + getMonthValueWithZero + "." + currentDateLD.getYear() + " г.";
                        chosenDateText.setText(currentDateString);
                    }
                    break; // Выход из цикла, если ячейка найдена
                }
            }
        }
    }

    public Node getCellElementCurrentDay() {
        return textElementCurrentDay;
    }
}
