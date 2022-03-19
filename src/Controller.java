import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Text chosenDateText;

    @FXML
    private Text currentMonthText;

    boolean monthIncrease, monthReduce = false;
    LocalDate currentDate;
    ObservableList listOfTexts;

    LocalDate getCurrentDate(int year, int month) {
        StringBuilder stringDate;
        if (month == 0) {
            year--;
            month = 12;
            stringDate = new StringBuilder(year + "-" + month + "-01");
        } else if (month >= 1 && month <= 9) {
            stringDate = new StringBuilder(year + "-0" + month + "-01");
        } else if (month >= 10 && month <= 12) {
            stringDate = new StringBuilder(year + "-" + month + "-01");
        } else {
            year++;
            month = 1;
            stringDate = new StringBuilder(year + "-0" + month + "-01");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        currentDate = LocalDate.parse(stringDate, formatter);
        return currentDate;
    }

    int currentDay;

    @FXML
    void showCalendar() {
        if (monthIncrease) {
            int year = currentDate.getYear();
            // Увеличенный на еденицу номер мсяца
            int month = currentDate.getMonthValue() + 1;
            // Получение новой даты с учетом изменения календарного месяца
            currentDate = getCurrentDate(year, month);
        } else if (monthReduce) {
            int year = currentDate.getYear();
            // Уменьшенный на еденицу номер месяца
            int month = currentDate.getMonthValue() - 1;
            currentDate = getCurrentDate(year, month);
        } else {
            currentDate = LocalDate.now();
        }

        String monthTitle = getRusMonth(currentDate.getMonthValue());
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());

        int firstMonthDay = getDayOfWeek(currentDate);

        resetDays(anchorPane);

        listOfTexts = anchorPane.getChildren();

        int j = 1;
        currentDay = currentDate.getDayOfMonth();

        for (int i = firstMonthDay - 1; i < currentDate.lengthOfMonth() + firstMonthDay - 1; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (currentDay == j) {
                    currentDay = i;
//                    if (firstStartCalendar) {
//                        highlightToday();
//                        changeText();
//                    }
//                    firstStartCalendar = false;
                }
                ((Text) text).setText(Integer.toString(j));
                j++;
            }
        }

        monthReduce = false;
        monthIncrease = false;
    }

    // Обнуление содержимого ячейки для изменения
    void resetDays(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Text) {
                ((Text) node).setText("");
            }
        }
    }

    // Получение номера ячейки, с которой начинается первое число текущего месяца, для корректной расстановки дней месяца в ячейках
    int getDayOfWeek(LocalDate date) {
        String correctMonth;

        if (date.getMonthValue() < 10) {
            correctMonth = "0" + date.getMonthValue();
        } else {
            correctMonth = Integer.toString(date.getMonthValue());
        }
        DayOfWeek dow = LocalDate.parse("01-" + correctMonth + "-" + date.getYear(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).getDayOfWeek();
        return dow.getValue();
    }

    // Установка стиля выбранной ячейки по умолчанию
    void resetStyles() {
        for (Node node : gridPane.getChildren()) {
            node.setStyle("-fx-border-width: 0.5; -fx-border-color: #76787a");
        }
    }

    // Изменение календарного месяца при нажатии стрелки "вправо"
    @FXML
    void increaseMonth() {
        monthIncrease = true;
        resetStyles();
        showCalendar();
    }

    // Изменение календарного месяца при нажатии стрелки "влево"
    @FXML
    void reduceMonth() {
        monthReduce = true;
        resetStyles();
        showCalendar();
    }

    // Получение названия месяца на русском языке для currentMonthText
    String getRusMonth(int month) {
        String monthTitle = null;
        switch (month) {
            case (1):
                monthTitle = "ЯНВАРЬ";
                break;
            case (2):
                monthTitle = "ФЕВРАЛЬ";
                break;
            case (3):
                monthTitle = "МАРТ";
                break;
            case (4):
                monthTitle = "АПРЕЛЬ";
                break;
            case (5):
                monthTitle = "МАЙ";
                break;
            case (6):
                monthTitle = "ИЮНЬ";
                break;
            case (7):
                monthTitle = "ИЮЛЬ";
                break;
            case (8):
                monthTitle = "АВГУСТ";
                break;
            case (9):
                monthTitle = "СЕНТЯБРЬ";
                break;
            case (10):
                monthTitle = "ОКТЯБРЬ";
                break;
            case (11):
                monthTitle = "НОЯБРЬ";
                break;
            case (12):
                monthTitle = "ДЕКАБРЬ";
                break;
        }
        return monthTitle;
    }

    StringBuilder getChosenDate() {
        int i = 0, numbOfCell = 0;
        int chosenDay = currentDate.getDayOfMonth();
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals("-fx-border-width: 3; -fx-border-color: #000000")) {
                i++;
            } else {
                numbOfCell = i;
            }
        }
        int j = 0;
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Text) {
                if (numbOfCell != j || ((Text) node).getText().equals("")) {
                    j++;
                } else {
                    chosenDay = Integer.parseInt(((Text) node).getText());
                    break;
                }
            }
        }

        String monthString;
        if (currentDate.getMonthValue() <= 9) {
            monthString = "0" + currentDate.getMonthValue();
        } else {
            monthString = Integer.toString(currentDate.getMonthValue());
        }
        return new StringBuilder(chosenDay + "." + monthString + "." + currentDate.getYear() + " г.");
    }

    // "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации этих ячеек
    void setHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text && !(((Text) object).getText().equals(""))) {
                    resetStyles();
                    element.setStyle("-fx-border-width: 3; -fx-border-color: #000000");
                    changeText();
                }
            });
            count++;
        }
    }

    // Изменения даты в текстовом представлении в верхней части календаря
    void changeText() {
        chosenDateText.setText(getChosenDate().toString());
    }

    // Метод, вызываемый автоматически при запуске программы
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Показать календарь
        showCalendar();
        setHandlers();
    }
}