import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javafx.beans.value.ChangeListener;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public static HashMap<Integer, Event> notesMemory = new HashMap<>();
    public static ObservableList<String> notesNames = FXCollections.observableArrayList();
    public static int numberEvent = 0;
    // модель выбора элементов
    public static MultipleSelectionModel<String> listViewSelectionModel;

    @FXML
    private Button addNewNoteButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text chosenDateText;

    @FXML
    private Text currentMonthText;

    @FXML
    private Button deleteChooseNoteButton;

    @FXML
    private Button editChooseNoteButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<String> listNotes;

    @FXML
    private TextField nameNote;

    @FXML
    private TextField textFieldNote;

    @FXML
    private Text currentMonthTextConst;

    // объявление переменных
    int currentDay;
    boolean monthIncrease = false, monthReduce = false;
    LocalDate currentDate;
    ObservableList<Node> listOfTexts;
    HashMap<Integer, String> nameOfTheSelectDays = new HashMap<>(); // Числа по номерам ячеек на выбранный месяц
    String currentDayString; // Дата текущего дня в String
    boolean dayIsChosen = false; // Ты нажал на какой то день?

    @FXML
    void showCalendar() {
        currentDate = getCurrentDate();

        String monthTitle = getRusMonth(currentDate.getMonthValue());
        // Вставить надпись с месяцем и годом выбранного календарного месяца
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());

        int firstActiveCell = getDayOfWeek(currentDate);

        resetDays(anchorPane);

        listOfTexts = anchorPane.getChildren();

        currentDay = currentDate.getDayOfMonth();

        createCalendar(firstActiveCell);

        showToday();

        monthReduce = false;
        monthIncrease = false;
    }

    @FXML
    void addNewNote() {
        if (dayIsChosen) {
            Event newEvent;
            if (textFieldIsNoExist()) {
                newEvent = new Event(getChosenDate(), nameNote.getText(), numberEvent);
            } else {
                newEvent = new Event(getChosenDate(), nameNote.getText(), textFieldNote.getText(), numberEvent);
            }
            int keyNotesMemory = getKeyForChosenDay() + numberEvent;
            numberEvent++;
            notesMemory.put(keyNotesMemory, newEvent);

            // Добавляем в ListView название события
            notesNames.add(nameNote.getText());
            listNotes.setItems(notesNames);

            // Очистка полей после создания нового события
            nameNote.clear();
            textFieldNote.clear();
        }
    }

    // Отображение описания события при выборе его кликом мыши
    public void selectedTextEventListener() {
        // получаем модель выбора элементов
        listViewSelectionModel = listNotes.getSelectionModel();
        // устанавливаем слушатель для отслеживания изменений
        listViewSelectionModel.selectedItemProperty().addListener(
                (changed, oldValue, newValue) ->
                textFieldNote.setText(
                        notesMemory.get(getKeyForChosenDay() + listViewSelectionModel.getSelectedIndex()).getTextEvent()
                )
        );
    }

    public Integer getChosenDayOfMonth(String chosenDate) {
        char[] chosenDateInChar = chosenDate.toCharArray();
        String chosenDayInString = "" + chosenDateInChar[0] + chosenDateInChar[1];
        int chosenDay = Integer.parseInt(chosenDayInString);
        return chosenDay;
    }

    public Integer getKeyForChosenDay() {
        return getChosenDayOfMonth(getChosenDate()) * 100;
    }

    // получение текущей даты с помощью LocalDate
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

    void createCalendar(int firstActiveCell) {
        // Расстановка чисел в текущем календарном месяце
        int count = 1;
        int numOfCell;
        firstActiveCell--;
        for (int i = firstActiveCell; i < currentDate.lengthOfMonth() + firstActiveCell; ++i) {
            numOfCell = i + 1;
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (currentDay == count) {
                    currentDay = i;
                }
                ((Text) text).setText(Integer.toString(count));
                nameOfTheSelectDays.put(numOfCell, ((Text) text).getText());
                count++;
            }
        }
    }

    // Отображение в chosenDateText сегодняшней даты
    void showToday() {
        if (LocalDate.now().getMonthValue() == currentDate.getMonthValue() && LocalDate.now().getYear() == currentDate.getYear()) {
            for (Node element : anchorPane.getChildren()) {
                if (element instanceof Text && !(((Text) element).getText()).equals("") && Integer.parseInt(((Text) element).getText()) == LocalDate.now().getDayOfMonth()) {
                    element.setStyle("-fx-underline: true; -fx-font-size: 29");
                    boolean cellSelected = false;
                    for (Node node : gridPane.getChildren()) {
                        if (node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                            cellSelected = true;
                        }
                    }
                    if (!cellSelected) {
                        String getMonthValueWithZero;
                        if (currentDate.getMonthValue() < 10) {
                            getMonthValueWithZero = "0" + currentDate.getMonthValue();
                        } else {
                            getMonthValueWithZero = "" + currentDate.getMonthValue();
                        }
                        currentDayString = LocalDate.now().getDayOfMonth() +
                                "." +
                                getMonthValueWithZero +
                                "." +
                                currentDate.getYear() +
                                " г.";
                        chosenDateText.setText(currentDayString);
                        currentMonthTextConst.setText(currentDayString);
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
            correctMonth = "" + date.getMonthValue();
        }
        DayOfWeek dow = LocalDate.parse("01-" + correctMonth + "-" + date.getYear(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).getDayOfWeek();
        return dow.getValue();
    }

    // Установка стиля выбранной ячейки по умолчанию
    void resetStylesBorder() {
        for (Node element : gridPane.getChildren()) {
            element.setStyle("-fx-border-width: 0.5; -fx-border-color: #76787a");
        }
    }

    // Установка стиля шрифта по умолчанию
    void resetStylesFont() {
        for (Node element : anchorPane.getChildren()) {
            element.setStyle("-fx-underline: false; -fx-font-size: 25");
        }
    }

    // Изменение календарного месяца при нажатии стрелки "вправо"
    @FXML
    void increaseMonth() {
        monthIncrease = true;
        dayIsChosen = false;
        resetStylesBorder();
        resetStylesFont();
        showCalendar();
    }

    // Изменение календарного месяца при нажатии стрелки "влево"
    @FXML
    void reduceMonth() {
        monthReduce = true;
        dayIsChosen = false;
        resetStylesBorder();
        resetStylesFont();
        showCalendar();
    }

    // Получение названия месяца на русском языке для currentMonthText
    String getRusMonth(int month) {
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

    // Получение строки с датой выбранного календарного дня
    String getChosenDate() {
        int count1 = 1, numbOfCell = 0;
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                count1++;
            } else {
                numbOfCell = count1;
            }
        }
        String day = nameOfTheSelectDays.get(numbOfCell);
        String getMonthValueWithZero;
        if (currentDate.getMonthValue() < 10) {
            getMonthValueWithZero = "0" + currentDate.getMonthValue();
        } else {
            getMonthValueWithZero = "" + currentDate.getMonthValue();
        }
        int getDay = Integer.parseInt(day);
        if (getDay < 10) {
            day = "0" + getDay;
        } else {
            day = "" + getDay;
        }
        return day + "." + getMonthValueWithZero + "." + currentDate.getYear() + " г.";
    }

    // "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации этих ячеек
    void setHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text && !(((Text) object).getText().isEmpty())) {
                    resetStylesBorder();
                    element.setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    changeText();
                    dayIsChosen = true;

                    // Очистка ListView при выборе другого дня
                    listNotes.getItems().clear();
                    numberEvent = 0;
                    textFieldNote.clear();

                    // Заполнение ListView для выбранного дня с помощью HashMap
                    int chosenDayKeys = getKeyForChosenDay();
                    System.out.println(chosenDayKeys + "CK");
                    int i = 0;
                    for (Integer key : notesMemory.keySet()) {
                        int sum = chosenDayKeys + i;
                        if (sum == key) {
                            System.out.println(key);
                            numberEvent++;
                            notesNames.add(notesMemory.get(sum).getTitleEvent());
                            listNotes.setItems(notesNames);
                            i++;
                        }
                    }
//                    if (notesMemory.containsKey(getKeyForChosenDay())) {
                        selectedTextEventListener();
//                    }
                }
            });
            count++;
        }
    }

    // Делать кнопку addNewNoteButton активной, если день выбран и поле nameNote заполнено, в ином случае - неактивной
    public void addListener() {
        nameNote.textProperty().addListener((observable, oldValue, newValue) -> {
            addNewNoteButton.setDisable(!dayIsChosen || nameNoteIsNoExist());
        });
    }

    // Делать поле описания для заметки редактируемым только, если введено название заметки
    public void textFieldListener() {
        nameNote.textProperty().addListener((observable, oldValue, newValue) -> {
            textFieldNote.setEditable(!nameNoteIsNoExist());
        });
    }

    public boolean textFieldIsNoExist() {
        return textFieldNote.getText().isEmpty();
    }

    public boolean nameNoteIsNoExist() {
        return nameNote.getText().isEmpty();
    }

    // Изменения даты в текстовом представлении в верхней части календаря
    void changeText() {
        chosenDateText.setText(getChosenDate());
    }

    // Метод, вызываемый автоматически при запуске программы
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCalendar();
        setHandlers();
        addListener();
        textFieldListener();
    }
}