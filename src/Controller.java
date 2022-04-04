import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller implements Initializable {

    public static Map<Integer, Event> eventMemory = new TreeMap<>();
    private static ObservableList<String> eventNames = FXCollections.observableArrayList();
    private static final String EDIT = "edit";
    private static final String SAVE = "save";
    public static volatile boolean stopShowTime = false;

    @FXML
    private Button addNewNoteButton;

    @FXML
    private Label currentTime;

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
    private ListView<String> eventListView;

    @FXML
    private TextField eventNameField;

    @FXML
    private TextArea eventTextField;

    @FXML
    private Text currentDayConst;

    @FXML
    private ComboBox<String> hours;

    @FXML
    private ComboBox<String> minutes;

    // объявление переменных
    int currentDay;
    boolean monthIncrease = false, monthReduce = false;
    LocalDate currentDate;
    ObservableList<Node> listOfTexts;
    HashMap<Integer, String> memoryNumbersByCells = new HashMap<>(); // Числа по номерам ячеек на выбранный месяц
    String currentDateString; // Дата текущего дня в String
    boolean chosenDayDetected = false; // Найден выбранный ранее день
    int numberEvent = 0; // номер события для каждого отдельного дня
    boolean switchEditToSaveButton = false; // сменить кнопку edit на save
    String saveMonthYearSelectedDay; // сохраненный месяц.год выбранного дня
    String saveCurrentMonth; // сохраненный текущий месяц.год
    boolean saveCurrentMonthReady = false; // сохранение текущего месяца.года выполнено
    Node cellElementSavedDay; // ячейка выбранного ранее дня
    Node cellElementCurrentDay; // ячейка текущего дня для выделения ее "синим" цветом
    boolean currentDayLeftTopDetected = false; // текущий день слева вверху обнаружен
    String chosenDateString;
    boolean chosenDayEventsSorted = false;

    @FXML
    void showCalendar() {
        currentDate = getCurrentDate();

        String monthTitle = getRusMonth(currentDate.getMonthValue());
        // Вставить надпись с месяцем и годом выбранного календарного месяца
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());

        int firstActiveCell = getCellNumberFirstDayMonth(currentDate);

        resetCells(anchorPane);

        listOfTexts = anchorPane.getChildren();

        currentDay = currentDate.getDayOfMonth();

        createCalendar(firstActiveCell);

        if (!chosenDayDetected) {
            showCurrentDate();
        }

        monthReduce = false;
        monthIncrease = false;

        if (chosenDayDetected) {
            checkOnEqualsMonth();
        }

        if (!saveCurrentMonthReady) {
            saveCurrentMonth = currentMonthYearString();
            saveCurrentMonthReady = true;
            fillStyleForCurrentDay();
        } else {
            fillStyleForCurrentDay();
        }
    }

    @FXML
    void addNewNote() {
        if (chosenDayDetected) {
            if (hours.getSelectionModel().isEmpty() && minutes.getSelectionModel().isEmpty()) {
                hours.requestFocus();
            } else {
                Event newEvent;
                if (textFieldIsNoExist()) {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), getEventHours(), getEventMinutes());
                } else {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), eventTextField.getText(), getEventHours(), getEventMinutes());
                }
                int keyEvent = getKeyForChosenDate(chosenDateString) + numberEvent;
                numberEvent++;
                eventMemory.put(keyEvent, newEvent);

                // Добавляем в ListView название события
                eventNames.add(eventNameField.getText());
                eventListView.setItems(eventNames);
                eventUpdateHandlers();

                switchEditToSaveButton = false;
                editChooseNoteButton.setText(EDIT);

                // Очистка полей после создания нового события
                clearNameAndTextEventField();
                sortEventsForChosenDay();
                clearListView();
                fillListView();
            }
        }
    }

    // Генерируем ключ для первого события выбранного дня
    public Integer getKeyForChosenDate(String chosenDateString) {
        char[] chosenDateInChar = chosenDateString.toCharArray();
        String chosenDayInString = "" + chosenDateInChar[0] + chosenDateInChar[1];
        String chosenMonthInString = "" + chosenDateInChar[3] + chosenDateInChar[4];
        String chosenYearInString = "" + chosenDateInChar[6] + chosenDateInChar[7] + chosenDateInChar[8] + chosenDateInChar[9];
        int chosenDay = Integer.parseInt(chosenDayInString);
        int chosenMonth = Integer.parseInt(chosenMonthInString);
        int chosenYear = Integer.parseInt(chosenYearInString);
        return chosenDay * chosenMonth * chosenYear * 100;
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
        return LocalDate.parse(stringDate, formatter);
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
                memoryNumbersByCells.put(numOfCell, ((Text) text).getText());
                count++;
            }
        }
        printCurrentDayLeftTopTitle();
    }

    public void printCurrentDayLeftTopTitle() {
        if (!currentDayLeftTopDetected) {
            String currentDayConstString = "" + currentDate.getDayOfMonth() + " " + getRusMonthInclination(currentDate.getMonthValue());
            currentDayConst.setText(currentDayConstString);
            currentDayLeftTopDetected = true;
        }
    }

    // Отображение в chosenDateText сегодняшней даты
    void showCurrentDate() {
        if (LocalDate.now().getMonthValue() == currentDate.getMonthValue() && LocalDate.now().getYear() == currentDate.getYear()) {
            for (Node element : anchorPane.getChildren()) {
                if (element instanceof Text && !(((Text) element).getText()).equals("") && Integer.parseInt(((Text) element).getText()) == LocalDate.now().getDayOfMonth()) {
                    cellElementCurrentDay = element;
                    boolean cellSelected = false;
                    for (Node node : gridPane.getChildren()) {
                        if (node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                            cellSelected = true;
                        }
                    }
                    if (!cellSelected) {
                        String getMonthValueWithZero, getDayValueWithZero;
                        if (currentDate.getMonthValue() < 10) {
                            getMonthValueWithZero = "0" + currentDate.getMonthValue();
                        } else {
                            getMonthValueWithZero = "" + currentDate.getMonthValue();
                        }
                        if (currentDate.getDayOfMonth() < 10) {
                            getDayValueWithZero = "0" + currentDate.getDayOfMonth();
                        } else {
                            getDayValueWithZero = "" + currentDate.getDayOfMonth();
                        }
                        currentDateString = getDayValueWithZero + "." + getMonthValueWithZero + "." + currentDate.getYear() + " г.";
                        chosenDateText.setText(currentDateString);
                    }
                }
            }
        }
    }

    // Обнуление содержимого ячейки для изменения
    void resetCells(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Text) {
                ((Text) node).setText("");
            }
        }
    }

    // Получение номера ячейки, с которой начинается первое число текущего месяца, для корректной расстановки дней месяца в ячейках
    int getCellNumberFirstDayMonth(LocalDate date) {
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

    // Установка цветов шрифта по умолчанию для чисел в ячейках
    void resetStylesFont() {
        // Для субботы
        for (int i = 5; i < anchorPane.getChildren().size(); i += 7) {
            anchorPane.getChildren().get(i).setStyle(("-fx-fill: #ff0000"));
        }
        // Для воскресения
        for (int i = 6; i < anchorPane.getChildren().size(); i += 7) {
            anchorPane.getChildren().get(i).setStyle(("-fx-fill: #ff0000"));
        }
        // Для будних дней
        for (Node element : anchorPane.getChildren()) {
            if (!element.getStyle().equals("-fx-fill: #ff0000")) {
                element.setStyle(("-fx-fill: #000"));
            }
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

    public void checkOnEqualsMonth() {
        if (saveMonthYearSelectedDay.equalsIgnoreCase(currentMonthYearString())) {
            cellElementSavedDay.setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
        }
    }

    public void fillStyleForCurrentDay() {
        if (saveCurrentMonth.equalsIgnoreCase(currentMonthYearString())) {
            cellElementCurrentDay.setStyle(("-fx-fill: #0000ff"));
        }
    }

    public String currentMonthYearString() {
        String currentMonthYear;
        if (currentDate.getMonthValue() < 10) {
            currentMonthYear = "0" + currentDate.getMonthValue() + "." + currentDate.getYear();
        } else {
            currentMonthYear = "" + currentDate.getMonthValue() + "." + currentDate.getYear();
        }
        return currentMonthYear;
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

    String getRusMonthInclination(int month) {
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

    // Получение строки с датой выбранного календарного дня
    String getChosenDateString() {
        int count1 = 1, numbOfCell = 0;
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                count1++;
            } else {
                numbOfCell = count1;
            }
        }
        String day = memoryNumbersByCells.get(numbOfCell);
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
    void mouseClickedHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text && !(((Text) object).getText().isEmpty())) {
                    resetStylesBorder();
                    element.setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    changeText();

                    chosenDateString = getChosenDateString();
                    chosenDayDetected = true;

                    if (cellElementSavedDay != element) {
                        clearListView();
                        clearNameAndTextEventField();
                        fillListView();
                    }

                    // Сохранение выбранного дня при перелистывании месяцев
                    String chosenDate = getChosenDateString();
                    char[] chosenDateChar = chosenDate.toCharArray();
                    saveMonthYearSelectedDay = "" + chosenDateChar[3] + chosenDateChar[4] + "." + chosenDateChar[6] + chosenDateChar[7] + chosenDateChar[8] + chosenDateChar[9];
                    cellElementSavedDay = element;

                    // Вывод в TextField описания выбранного события и заполнение времени
                    eventUpdateHandlers();

                    deleteChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setDisable(true);
                }
            });
            count++;
        }
    }

    // Вывод в TextField описания выбранного события и заполнение времени
    void eventUpdateHandlers() {
        if (!eventNames.isEmpty()) {
            eventListView.setOnMouseClicked(event -> {
                clearNameAndTextEventField();
                switchEditToSaveButton = false;
                int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
                if (eventListView.getSelectionModel().isSelected(selectedIndex)) {
                    int key = getKeyForChosenEvent();
                    eventTextField.setText(eventMemory.get(key).getEventText());
                    hours.setValue(eventMemory.get(key).getEventHours());
                    minutes.setValue(eventMemory.get(key).getEventMinutes());
                    deleteChooseNoteButton.setDisable(false);
                    deleteHandler(key);
                    editChooseNoteButton.setDisable(false);
                    editHandler(getKeyForChosenEvent());
                }
            });
        }
    }

    public void clearListView() {
        eventListView.getItems().clear();
        numberEvent = 0;
        eventTextField.clear();
    }

    // Заполнение ListView для выбранного дня с помощью HashMap
    public void fillListView() {
        int count = 0;
        for (Integer key : eventMemory.keySet()) {
            int sum = getKeyForChosenDate(getChosenDateString()) + count;
            if (sum == key) {
                eventNames.add(eventMemory.get(sum).getEventTitle());
                eventListView.setItems(eventNames);
                numberEvent++;
                count++;
            }
        }
    }

    public void sortEventsForChosenDay() {
        int dayFirstKey = getKeyForChosenDate(getChosenDateString());
        QuickSort.quickSortTreeMap(eventMemory, dayFirstKey, dayFirstKey + eventListView.getItems().size() - 1);
        chosenDayEventsSorted = true;
    }

    public void eventNameFieldHandlers() {
        eventNameField.setOnMouseClicked(event -> {
            deleteChooseNoteButton.setDisable(true);
            editChooseNoteButton.setDisable(true);
            if (eventNameField.getText().trim().isEmpty()) {
                eventTextField.clear();
                hours.setValue("");
                minutes.setValue("");
            } else {
                editChooseNoteButton.setDisable(false);
            }
        });
    }

    // Делать кнопку addNewNoteButton активной, если день выбран и поле eventNameField заполнено, в ином случае - неактивной
    public void addListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> addNewNoteButton.setDisable(!chosenDayDetected || eventNameFieldIsNoExist()));
    }

    public void deleteHandler(int key) {
        deleteChooseNoteButton.setOnMouseClicked(event -> {
            eventNames.remove(eventListView.getSelectionModel().getSelectedIndex());
            // Удаление события в мапе и смещения всех остальных событий "влево"
            eventMemory.remove(key);
            int countKey = key + 1;
            int keyCopy = key;
            while (eventMemory.containsKey(countKey)) {
                eventMemory.put(keyCopy, eventMemory.get(countKey));
                keyCopy++;
                countKey++;
            }
            eventMemory.remove(keyCopy);
            numberEvent--;

            clearNameAndTextEventField();
            editChooseNoteButton.setDisable(true);
            deleteChooseNoteButton.setDisable(true);
        });
    }

    void clearNameAndTextEventField() {
        editChooseNoteButton.setText(EDIT);
        eventNameField.clear();
        eventTextField.clear();
        hours.setValue("");
        minutes.setValue("");
    }

    void editHandler(int key) {
        editChooseNoteButton.setOnMouseClicked(event2 -> {
            if (!switchEditToSaveButton) {
                eventNameField.setText(eventMemory.get(key).getEventTitle());
                editChooseNoteButton.setText(SAVE);
                eventTextField.requestFocus();
                eventTextField.deselect();
                deleteChooseNoteButton.setDisable(true);
                addNewNoteButton.setDisable(true);
                switchEditToSaveButton = true;
            } else {
                int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
                eventNames.set(selectedIndex, eventNameField.getText());
                Event newEvent;
                if (textFieldIsNoExist()) {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), getEventHours(), getEventMinutes());
                } else {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), eventTextField.getText(), getEventHours(), getEventMinutes());
                }
                eventMemory.put(key, newEvent);
                switchEditToSaveButton = false;
                editChooseNoteButton.setText(EDIT);
            }
        });
    }

    // Делать поле описания для заметки редактируемым только, если введено название заметки
    void textFieldListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> eventTextField.setEditable(!eventNameFieldIsNoExist()));
    }

    public boolean textFieldIsNoExist() {
        return eventTextField == null;
    }

    public boolean eventNameFieldIsNoExist() {
        return eventNameField == null;
    }

    // Изменения даты в текстовом представлении в верхней части календаря
    void changeText() {
        chosenDateText.setText(getChosenDateString());
    }

    // Вернуть ключ для события, выбранного мышкой
    Integer getKeyForChosenEvent() {
        return getKeyForChosenDate(chosenDateString) + eventListView.getSelectionModel().getSelectedIndex();
    }

    // Заполнить ComboBox hours часами от 00 до 23 в выпадающем списке
    @FXML
    void showAllHours(MouseEvent event) {
        ObservableList<String> hoursList = FXCollections.observableArrayList();
//        hoursList.add("-");
        for (int i = 0; i <= 23; ++i) {
            if (i <= 9) {
                hoursList.add("0" + i);
            } else {
                hoursList.add(Integer.toString(i));
            }
        }
        hours.setItems(hoursList);
    }

    // Заполнить ComboBox minutes минутами от 00 до 59 в выпадающем списке
    @FXML
    void showAllMinutes(MouseEvent event) {
        ObservableList<String> minutesList = FXCollections.observableArrayList();
//        minutesList.add("-");
        for (int i = 0; i <= 59; ++i) {
            if (i <= 9) {
                minutesList.add("0" + i);
            } else {
                minutesList.add(Integer.toString(i));
            }
        }
        minutes.setItems(minutesList);
    }

    String getEventHours() {
        if (hours.getSelectionModel().getSelectedItem().equals("")) {
            return "00";
        } else {
            return (hours.getSelectionModel().getSelectedItem());
        }
    }

    String getEventMinutes() {
        if (minutes.getSelectionModel().getSelectedItem().equals("")) {
            return "00";
        } else {
            return (minutes.getSelectionModel().getSelectedItem());
        }
    }

    public void printTimeNow() {
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            while (!stopShowTime) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                final String timeNow = sdf.format(new Date());
                Platform.runLater(() -> currentTime.setText(timeNow));
            }
        });
        thread.start();
    }

    // Метод, вызываемый автоматически при запуске программы
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCalendar();
        mouseClickedHandlers();
        addListener();
        textFieldListener();
        printTimeNow();
    }
}