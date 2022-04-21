import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Controller implements Initializable {
    // Объявление констант
    private static final int NUM_OF_ALL_CELLS = 42;
    private static final int KEY_GENERATION_COEFF = 100;
    private static final int MILLIS_OF_SLEEP = 1000;
    private static final String EDIT = "edit";
    private static final String SAVE = "save";

    // Объявление переменных
    public static Map<Integer, Event> eventMemory = new TreeMap<>();
    public static ObservableList<String> eventNames = FXCollections.observableArrayList();
    public static volatile boolean stopShowTime = false;
    private static int currentDay;
    private static boolean monthIncrease = false, monthReduce = false;
    private static LocalDate currentDate;
    public static ObservableList<Node> listOfTexts;
    public static ObservableList<Node> listOfPane;
    private static HashMap<Integer, String> memoryNumbersByCells = new HashMap<>(); // Числа по номерам ячеек на выбранный месяц
    private static String currentDateString; // Дата текущего дня в String
    private static LocalDate currentDateLD; // Дата текущего дня в LD
    private static boolean chosenDayDetected = false; // Найден выбранный ранее день
    private static int numberEvent = 0; // Номер события для каждого отдельного дня
    private static boolean switchEditToSaveButton = false; // Сменить кнопку edit на save
    private static Node cellElementCurrentDay; // Ячейка текущего дня для выделения ее "синим" цветом
    private static boolean currentDayLeftTopDetected = false; // Текущий день слева вверху обнаружен
    private static String chosenDateString;
    private static int numbOfCell;
    private static boolean cellSelected = false;
    private static boolean editingIsActive = false;

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

    @FXML
    void showCalendar() {
        currentDate = getCurrentDate();

        String monthTitle = getRusMonth(currentDate.getMonthValue());
        // Вставить надпись с месяцем и годом выбранного календарного месяца
        currentMonthText.setText(monthTitle + " " + currentDate.getYear());

        int firstActiveCell = getCellNumberFirstDayMonth(currentDate);

        listOfTexts = anchorPane.getChildren();
        listOfPane = gridPane.getChildren();
        currentDay = currentDate.getDayOfMonth();

        resetCells(anchorPane);

        currentDay = currentDate.getDayOfMonth();

        createActiveCalendar(firstActiveCell);
        createPassiveCalendar(firstActiveCell);

        showCurrentDate();

        monthReduce = false;
        monthIncrease = false;

        highlightToday();

        if (cellSelected) {
            showChosenDay(firstActiveCell);
        }
    }

    // Выделение ячейки с ранее выбранным днем, если таковой есть на текущей развертке календаря
    void showChosenDay(int firstActiveCell) {
        firstActiveCell--;
        for (int i = 0; i < NUM_OF_ALL_CELLS; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                System.out.println(firstActiveCell);
                if (i < firstActiveCell && ((currentDate.getMonthValue() == 1 && currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue()
                        && currentDateLD.getYear() == currentDate.minusYears(1).getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                        || (currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue() && currentDateLD.getYear() == currentDate.getYear()
                        && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()))))) {
                    listOfPane.get(i).setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    break;
                } else if (i >= firstActiveCell && i < firstActiveCell + currentDate.lengthOfMonth() && currentDateLD.getMonthValue() == currentDate.getMonthValue()
                        && currentDateLD.getYear() == currentDate.getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()))) {
                    listOfPane.get(i).setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    break;
                } else if (i >= firstActiveCell + currentDate.lengthOfMonth() && ((currentDate.getMonthValue() == 12 && currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue()
                        && currentDateLD.getYear() == currentDate.plusYears(1).getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                        || ((currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue() && currentDateLD.getYear() == currentDate.getYear()
                        && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))))) {
                    listOfPane.get(i).setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    break;
                }
            }
        }
    }

    @FXML
    void addNewNote() {
        if (chosenDayDetected) {
            if (hours.getSelectionModel().isEmpty() && minutes.getSelectionModel().isEmpty()) {
                hours.requestFocus();
            } else {
                Event newEvent;
                if (eventNameField.getText().isEmpty()) {
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

    // Генерация ключа для первого события выбранного дня
    public Integer getKeyForChosenDate(String chosenDateString) {
        char[] chosenDateInChar = chosenDateString.toCharArray();
        String chosenDayInString = "" + chosenDateInChar[0] + chosenDateInChar[1];
        String chosenMonthInString = "" + chosenDateInChar[3] + chosenDateInChar[4];
        String chosenYearInString = "" + chosenDateInChar[6] + chosenDateInChar[7] + chosenDateInChar[8] + chosenDateInChar[9];
        int chosenDay = Integer.parseInt(chosenDayInString);
        int chosenMonth = Integer.parseInt(chosenMonthInString);
        int chosenYear = Integer.parseInt(chosenYearInString);
        return chosenDay * chosenMonth * chosenYear * KEY_GENERATION_COEFF;
    }

    // Получения даты первого дня текущего месяца вместе с годом для дальнейшего использования в проверках
    LocalDate getCurrentDateWithFirstDay(int month, int year) {
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

    // Получение полной даты текущего дня
    LocalDate getCurrentDate() {
        if (monthIncrease) {
            // Увеличенный на еденицу номер месяца
            int month = currentDate.getMonthValue() + 1;
            int year = currentDate.getYear();
            // Получение новой даты с учетом изменения календарного месяца
            return getCurrentDateWithFirstDay(month, year);
        } else if (monthReduce) {
            // Уменьшенный на еденицу номер месяца
            int month = currentDate.getMonthValue() - 1;
            int year = currentDate.getYear();
            return getCurrentDateWithFirstDay(month, year);
        } else {
            return LocalDate.now();
        }
    }

    // Расстановка чисел в текущем календарном месяце
    void createActiveCalendar(int firstActiveCell) {
        int count = 1;
        firstActiveCell--;
        for (int i = firstActiveCell; i < currentDate.lengthOfMonth() + firstActiveCell; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (currentDay == count) {
                    currentDay = i;
                }
                ((Text) text).setText(Integer.toString(count));
                memoryNumbersByCells.put(i + 1, ((Text) text).getText());
                count++;
            }
        }
        printCurrentDayLeftTopTitle();
    }

    // Расстановка чисел в предыдущем и последующем календарных месяцах
    public void createPassiveCalendar(int firstActiveCell) {
        firstActiveCell -= 2;

        int numDaysLastMonth = currentDate.minusMonths(1).lengthOfMonth();
        for (int i = firstActiveCell; i >= 0; --i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                ((Text) text).setText(Integer.toString(numDaysLastMonth));
                ((Text) text).setStyle("-fx-opacity: 0.25");
                memoryNumbersByCells.put(i + 1, ((Text) text).getText());
                numDaysLastMonth--;
            }
        }

        int numOfDay = 1;
        for (int i = currentDate.lengthOfMonth() + firstActiveCell + 1; i < NUM_OF_ALL_CELLS; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                ((Text) text).setText(Integer.toString(numOfDay));
                ((Text) text).setStyle("-fx-opacity: 0.25");
                memoryNumbersByCells.put(i + 1, ((Text) text).getText());
                numOfDay++;
            }
        }
    }

    // Вывод сегодняшней даты в виде: число + название месяца
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
                if (element instanceof Text && !(((Text) element).getText()).isEmpty() && Integer.parseInt(((Text) element).getText()) == LocalDate.now().getDayOfMonth()) {
                    cellElementCurrentDay = element;
                    for (Node node : gridPane.getChildren()) {
                        if (node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
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

    // Выделение сегодняшнего числа в ячейке цветом
    public void highlightToday() {
        if (currentDate.getMonthValue() == LocalDate.now().getMonthValue() && currentDate.getYear() == LocalDate.now().getYear())
            cellElementCurrentDay.setStyle(("-fx-fill: #0000ff"));
    }

    // Получение названия месяца на русском языке в им. падеже для currentMonthText
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

    // Получение названия месяца на русском языке в род. падеже для currentMonthText
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
        int count1 = 1;
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals("-fx-border-width: 2.5; -fx-border-color: #000000")) {
                count1++;
            } else {
                numbOfCell = count1;
            }
        }

        String monthValueWithZero;
        int year;
        if (numbOfCell < getCellNumberFirstDayMonth(currentDate)) {
            monthValueWithZero = getMonthValueWithZero(currentDate.minusMonths(1));
            year = currentDate.minusMonths(1).getYear();
        } else if (numbOfCell > getCellNumberFirstDayMonth(currentDate) + currentDate.lengthOfMonth() - 1) {
            monthValueWithZero = getMonthValueWithZero(currentDate.plusMonths(1));
            year = currentDate.plusMonths(1).getYear();
        } else {
            monthValueWithZero = getMonthValueWithZero(currentDate);
            year = currentDate.getYear();
        }

        String day = memoryNumbersByCells.get(numbOfCell);
        int getDay = Integer.parseInt(day);
        if (getDay < 10) {
            day = "0" + getDay;
        } else {
            day = "" + getDay;
        }
        return day + "." + monthValueWithZero + "." + year;
    }

    // Получение номера месяца с нулем, если он - однозначное число, и наоборот
    String getMonthValueWithZero(LocalDate date) {
        if (date.getMonthValue() < 10) {
            return "0" + date.getMonthValue();
        } else {
            return "" + date.getMonthValue();
        }
    }

    // "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации этих ячеек и не только
    void mouseClickedHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text) {
                    resetStylesBorder();
                    element.setStyle("-fx-border-width: 2.5; -fx-border-color: #000000");
                    changeText();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

                    currentDateLD = LocalDate.parse(getChosenDateString(), formatter);
                    chosenDateString = getChosenDateString() + " г.";

                    chosenDayDetected = true;
                    cellSelected = true;

                    clearListView();
                    clearNameAndTextEventField();
                    fillListView();

                    // Вывод в TextField описания выбранного события и заполнение времени
                    eventUpdateHandlers();

                    deleteChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setText("edit");
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

    // Сортировка событий по времени
    public void sortEventsForChosenDay() {
        int dayFirstKey = getKeyForChosenDate(getChosenDateString());
        QuickSort.quickSortTreeMap(eventMemory, dayFirstKey, dayFirstKey + eventListView.getItems().size() - 1);
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

    // Становление кнопки addNewNoteButton активной, если день выбран и поле eventNameField заполнено, в ином случае - неактивной
    public void addListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> addNewNoteButton.setDisable(!cellSelected || eventNameField.getText().isEmpty() || editingIsActive));
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
            editingIsActive = true;
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
                if (eventNameField.getText().isEmpty()) {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), getEventHours(), getEventMinutes());
                } else {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), eventTextField.getText(), getEventHours(), getEventMinutes());
                }
                eventMemory.put(key, newEvent);
                switchEditToSaveButton = false;
                editChooseNoteButton.setText(EDIT);
                eventListView.getSelectionModel().select(selectedIndex);
                deleteChooseNoteButton.setDisable(false);
            }
        });
        editingIsActive = false;
    }

    // Делать поле описания для заметки редактируемым только, если введено название заметки
    void textFieldListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> eventTextField.setEditable(!eventNameField.getText().isEmpty()));
    }

    // Изменения даты в текстовом представлении в верхней части календаря
    void changeText() {
        chosenDateText.setText(getChosenDateString() + " г.");
        currentDateString = getChosenDateString();
    }

    // Вернуть ключ для события, выбранного мышкой
    Integer getKeyForChosenEvent() {
        return getKeyForChosenDate(chosenDateString) + eventListView.getSelectionModel().getSelectedIndex();
    }

    // Заполнить ComboBox hours часами от 00 до 23 в выпадающем списке
    @FXML
    void showAllHours(MouseEvent event) {
        ObservableList<String> hoursList = FXCollections.observableArrayList();
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
        if (hours.getSelectionModel().getSelectedItem().isEmpty()) {
            return "00";
        } else {
            return (hours.getSelectionModel().getSelectedItem());
        }
    }

    String getEventMinutes() {
        if (minutes.getSelectionModel().getSelectedItem().isEmpty()) {
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
                    Thread.sleep(MILLIS_OF_SLEEP);
                } catch (Exception e) {
                    System.out.println("Error.");
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