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

    boolean monthIncrease = false, monthReduce = false;
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
        currentDate = getCurrentDate();

        String monthTitle = getRusMonth(currentDate.getMonthValue());
        // Вставить надпись с месяцем и годом выбранного календарного месяца
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());

        int firstMonthDay = getDayOfWeek(currentDate);

        resetDays(anchorPane);

        listOfTexts = anchorPane.getChildren();

        currentDay = currentDate.getDayOfMonth();

        createCalendar(firstMonthDay);

        showToday();

        monthReduce = false;
        monthIncrease = false;
    }

    LocalDate getCurrentDate() {
        if (monthIncrease) {
            int year = currentDate.getYear();
            // Увеличенный на еденицу номер месяца
            int month = currentDate.getMonthValue() + 1;
            // Получение новой даты с учетом изменения календарного месяца
            return getCurrentDate(year, month);
        } else if (monthReduce) {
            int year = currentDate.getYear();
            // Уменьшенный на еденицу номер месяца
            int month = currentDate.getMonthValue() - 1;
            return getCurrentDate(year, month);
        } else {
            return LocalDate.now();
        }
    }

    void createCalendar(int firstMonthDay) {
        // Расстановка чисел в текущем календарном месяце
        int count = 1;
        for (int i = firstMonthDay - 1; i < currentDate.lengthOfMonth() + firstMonthDay - 1; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (currentDay == count) {
                    currentDay = i;
                }
                ((Text) text).setText(Integer.toString(count));
                count++;
            }
        }
    }

    // Отображение в chosenDateText сегодняшней даты
    void showToday() {
        if (LocalDate.now().getMonthValue() == currentDate.getMonthValue() && LocalDate.now().getYear() == currentDate.getYear()) {
            for (Node element: anchorPane.getChildren()) {
                if (element instanceof Text && !(((Text)element).getText()).equals("") && Integer.parseInt(((Text)element).getText()) == LocalDate.now().getDayOfMonth()) {
                    element.setStyle("-fx-underline: true; -fx-font-size: 29");
                    boolean cellSelected = false;
                    for (Node node: gridPane.getChildren()) {
                        if (node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                            cellSelected = true;
                        }
                    }
                    if (!cellSelected) {
                        System.out.println(true);
                        chosenDateText.setText(LocalDate.now().getDayOfMonth() + "-" + currentDate.getMonthValue() + "-" + currentDate.getYear() +" г.");
                    }
                }
            }
        }
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
    void resetStylesBorder() {
        for (Node element: gridPane.getChildren()) {
            element.setStyle("-fx-border-width: 0.5; -fx-border-color: #76787a");
        }
    }
    // Установка стиля шрифта по умолчанию
    void resetStylesFont() {
        for (Node element: anchorPane.getChildren()) {
            element.setStyle("-fx-underline: false; -fx-font-size: 25");
        }
    }

    // Изменение календарного месяца при нажатии стрелки "вправо"
    @FXML
    void increaseMonth() {
        monthIncrease = true;
        resetStylesBorder();
        resetStylesFont();
        showCalendar();
    }

    // Изменение календарного месяца при нажатии стрелки "влево"
    @FXML
    void reduceMonth() {
        monthReduce = true;
        resetStylesBorder();
        resetStylesFont();
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

    // Получение строки с датой выбранного календарного дня
    StringBuilder getChosenDate() {
        int count1 = 0, numbOfCell = 0;
        int chosenDay = currentDate.getDayOfMonth();
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                count1++;
            } else {
                numbOfCell = count1;
            }
        }
        int count2 = 0;
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Text) {
                if (numbOfCell != count2 || ((Text) node).getText().equals("")) {
                    count2++;
                } else {
                    chosenDay = Integer.parseInt(((Text) node).getText());
                    break;
                }
            }
        }
        return new StringBuilder(chosenDay + "-" + currentDate.getMonthValue() + "-" + currentDate.getYear() + " г.");
    }

    // "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации этих ячеек
    void setHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text && !(((Text) object).getText().equals(""))) {
                    resetStylesBorder();
                    element.setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
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
        showCalendar();
        setHandlers();
    }
}