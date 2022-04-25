import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class ChosenDate extends Controller {
    private Node cellElementCurrentDay;

    public ChosenDate(Text chosenDateText, LocalDate currentDate, AnchorPane anchorPane, GridPane gridPane) {
        this.chosenDateText = chosenDateText;
        this.currentDate = currentDate;
        this.anchorPane = anchorPane;
        this.gridPane = gridPane;
//        this.currentDateLD = currentDateLD;
    }

    /**
     * Отображение в chosenDateText сегодняшней даты
     */
    public void showCurrentDate() {
//        System.out.println(currentDateLD);
        if (LocalDate.now().getMonthValue() == currentDate.getMonthValue() && LocalDate.now().getYear() == currentDate.getYear()) {
            for (Node element : anchorPane.getChildren()) {
                if (element instanceof Text && !(((Text) element).getText()).isEmpty() && Integer.parseInt(((Text) element).getText()) == LocalDate.now().getDayOfMonth()) {
                    cellElementCurrentDay = element;
                    for (Node node : gridPane.getChildren()) {
                        if (node.getStyle().equals(CHOSEN_CELL_STYLE)) {
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
                }
            }
        }
    }

    public Node getCellElementCurrentDay() {
        return cellElementCurrentDay;
    }
}
