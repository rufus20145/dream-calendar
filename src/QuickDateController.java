import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class QuickDateController extends Controller {
    public QuickDateController(ComboBox<String> monthOfQuickDate, ComboBox<Integer> yearOfQuickDate, CurrDateController currMonthAndYear, GridPane gridPane, AnchorPane anchorPane, Text currentMonthText, Pane quickDatePane, LocalDate currentDate) {
        this.monthOfQuickDate = monthOfQuickDate;
        this.yearOfQuickDate = yearOfQuickDate;
        this.currMonthAndYear = currMonthAndYear;
        this.gridPane = gridPane;
        this.anchorPane = anchorPane;
        this.currentMonthText = currentMonthText;
        this.quickDatePane = quickDatePane;
        this.currentDate = currentDate;
    }

    /**
     * Заполнение выпадающего списка monthOfQuickDate названиями всех месяцев
     */
    public void fillAllMonth() {
        ObservableList<String> monthList = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; ++i) {
            monthList.add(currMonthAndYear.getRusMonth(i));
        }
        monthOfQuickDate.setItems(monthList);
    }

    /**
     * Заполнение выпадающего списка yearOfQuickDate годами диапозоном [текущий год - 100; текущий год + 100]
     */
    public void fillAllYears(LocalDate currentDate) {
        ObservableList<Integer> yearsList = FXCollections.observableArrayList();
        int RANGE_OF_YEARS = 100;
        for (int i = currentDate.getYear() - RANGE_OF_YEARS; i <= currentDate.getYear() + RANGE_OF_YEARS; ++i) {
            yearsList.add(i);
        }
        yearOfQuickDate.setItems(yearsList);
    }

    /**
     * Применение текущего месяца к значению monthOfQuickDate
     */
    public void setMonth(LocalDate currentDate) {
        monthOfQuickDate.setValue(currMonthAndYear.getRusMonth(currentDate.getMonthValue()));
        quickDatePane.setVisible(true);
    }

    /**
     * Применение текущего года к значению yearOfQuickDate
     */
    public void setYear(LocalDate currentDate) {
        yearOfQuickDate.setValue(currentDate.getYear());
        quickDatePane.setVisible(true);
    }

    public void showMonthControl() {
        int count = 0;
        for (String month : monthOfQuickDate.getItems()) {
            if (month.equals(monthOfQuickDate.getValue())) {
                monthOfQuickDate.getSelectionModel().select(count);
                automaticScroll(monthOfQuickDate, count);
                break;
            }
            count++;
        }
    }

    public void showYearControl() {
        int count = 0;
        for (Integer year : yearOfQuickDate.getItems()) {
            if (year.equals(yearOfQuickDate.getValue())) {
                yearOfQuickDate.getSelectionModel().select(count);
                automaticScroll(yearOfQuickDate, count);
                break;
            }
            count++;
        }
    }
}
