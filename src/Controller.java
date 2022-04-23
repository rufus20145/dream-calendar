import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
    /**
     * Объявление констант
     */
    private final int NUM_OF_ALL_CELLS = 42;
    private final int MILLIS_OF_SLEEP = 1000;
    private final String EDIT = "edit";
    private final String SAVE = "save";
    private final String CHOSEN_CELL_STYLE = "-fx-border-width: 2.5; -fx-border-color: #000000";

    /**
     * Объявление переменных
     */
    private String currentDateString; // Дата текущего дня в String
    private String chosenDateString;
    private int currentDay;
    private int numberEvent = 0; // Номер события для каждого отдельного дня
    private int numbOfCell;
    private int quickMonth;
    private int quickYear;
    public static volatile boolean stopShowTime = false;
    private boolean chosenDayDetected = false; // Найден выбранный ранее день
    private boolean switchEditToSaveButton = false; // Сменить кнопку edit на save
    private boolean currentDayLeftTopDetected = false; // Текущий день слева вверху обнаружен
    private boolean monthIncrease = false, monthReduce = false;
    private boolean cellSelected = false;
    private boolean editingIsActive = false;
    private boolean dateIsQuick = false;
    private Map<Integer, Event> eventMemory = new TreeMap<>();
    private HashMap<Integer, String> memoryNumbersByCells = new HashMap<>(); // Числа по номерам ячеек на выбранный месяц
    private Node cellElementCurrentDay; // Ячейка текущего дня для выделения ее "синим" цветом
    private LocalDate currentDateLD; // Дата текущего дня в LD
    private LocalDate currentDate;
    private ObservableList<String> eventNames = FXCollections.observableArrayList();
    private ObservableList<Node> listOfTexts;
    private ObservableList<Node> listOfPane;

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
    private Pane quickDatePane;

    @FXML
    private ComboBox<String> monthOfQuickDate;

    @FXML
    private ComboBox<Integer> yearOfQuickDate;

    @FXML
    private void showCalendar() {
        currentDate = getCurrentDate();
        String monthTitle = getRusMonth(currentDate.getMonthValue());

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
        dateIsQuick = false;

        highlightToday();

        if (cellSelected) {
            showChosenDay(firstActiveCell);
        }
    }

    /**
     * Выделение ячейки с ранее выбранным днем, если таковой есть на текущей развертке календаря
     */
    private void showChosenDay(int firstActiveCell) {
        firstActiveCell--;
        for (int i = 0; i < NUM_OF_ALL_CELLS; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (cellBeforeCurrMonth(firstActiveCell, i, text) || cellOfCurrMonth(firstActiveCell, i, text) || cellAfterCurrMonth(firstActiveCell, i, text)) {
                    listOfPane.get(i).setStyle(CHOSEN_CELL_STYLE);
                    break;
                }
            }
        }
    }

    /**
     * Проверка расположения ячейки до выбранного месяца
     */
    private boolean cellBeforeCurrMonth(int firstActiveCell, int numOfCell, Object text) {
        return numOfCell < firstActiveCell && ((currentDate.getMonthValue() == 1 && currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.minusYears(1).getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                || (currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue() && currentDateLD.getYear() == currentDate.getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()))));
    }

    /**
     * Проверка расположения ячейки на выбранном месяце
     */
    private boolean cellOfCurrMonth(int firstActiveCell, int numOfCell, Object text) {
        return numOfCell >= firstActiveCell && numOfCell < firstActiveCell + currentDate.lengthOfMonth() && currentDateLD.getMonthValue() == currentDate.getMonthValue()
                && currentDateLD.getYear() == currentDate.getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()));
    }

    /**
     * Проверка расположения ячейки после выбранного месяца
     */
    private boolean cellAfterCurrMonth(int firstActiveCell, int numOfCell, Object text) {
        return numOfCell >= firstActiveCell + currentDate.lengthOfMonth() && ((currentDate.getMonthValue() == 12 && currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.plusYears(1).getYear() && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                || ((currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue() && currentDateLD.getYear() == currentDate.getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))));
    }

    /**
     * Добавление события
     */
    @FXML
    private void addNewNote() {
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

    /**
     * Генерация ключа для первого события выбранного дня
     */
    private Integer getKeyForChosenDate(String chosenDateString) {
        char[] chosenDateInChar = chosenDateString.toCharArray();
        String chosenDayInString = "" + chosenDateInChar[0] + chosenDateInChar[1];
        String chosenMonthInString = "" + chosenDateInChar[3] + chosenDateInChar[4];
        String chosenYearInString = "" + chosenDateInChar[6] + chosenDateInChar[7] + chosenDateInChar[8] + chosenDateInChar[9];
        int chosenDay = Integer.parseInt(chosenDayInString);
        int chosenMonth = Integer.parseInt(chosenMonthInString);
        int chosenYear = Integer.parseInt(chosenYearInString);
        int KEY_GENERATION_COEFF = 100;
        return chosenDay * chosenMonth * chosenYear * KEY_GENERATION_COEFF;
    }

    /**
     * Получение даты первого дня текущего месяца вместе с годом для дальнейшего использования в проверках
     */
    private LocalDate getCurrentDateWithFirstDay(int month, int year) {
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

    /**
     * Получение полной даты текущего дня
     */
    private LocalDate getCurrentDate() {
        int month, year;
        if (monthIncrease) {
            month = currentDate.getMonthValue() + 1;
            year = currentDate.getYear();
            return getCurrentDateWithFirstDay(month, year); // Получение новой даты с учетом изменения календарного месяца
        } else if (monthReduce) {
            month = currentDate.getMonthValue() - 1;
            year = currentDate.getYear();
            return getCurrentDateWithFirstDay(month, year); // Получение новой даты с учетом изменения календарного месяца
        } else if (dateIsQuick) {
            resetStylesBorder();
            resetStylesFont();
            return getCurrentDateWithFirstDay(quickMonth, quickYear);
        }
        return LocalDate.now();
    }

    /**
     * Расстановка чисел в текущем календарном месяце
     */
    private void createActiveCalendar(int firstActiveCell) {
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

    /**
     * Расстановка чисел в предыдущем и последующем календарных месяцах
     */
    private void createPassiveCalendar(int firstActiveCell) {
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

    /**
     * Вывод сегодняшней даты в виде: число + название месяца
     */
    private void printCurrentDayLeftTopTitle() {
        if (!currentDayLeftTopDetected) {
            String currentDayConstString = "" + currentDate.getDayOfMonth() + " " + getRusMonthInclination(currentDate.getMonthValue());
            currentDayConst.setText(currentDayConstString);
            currentDayLeftTopDetected = true;
        }
    }

    /**
     * Отображение в chosenDateText сегодняшней даты
     */
    private void showCurrentDate() {
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

    /**
     * Обнуление содержимого ячейки для изменения
     */
    private void resetCells(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Text) {
                ((Text) node).setText("");
            }
        }
    }

    /**
     * Получение номера ячейки, с которой начинается первое число текущего месяца, для корректной расстановки дней месяца в ячейках
     */
    private int getCellNumberFirstDayMonth(LocalDate date) {
        String correctMonth;

        if (date.getMonthValue() < 10) {
            correctMonth = "0" + date.getMonthValue();
        } else {
            correctMonth = "" + date.getMonthValue();
        }
        DayOfWeek dow = LocalDate.parse("01-" + correctMonth + "-" + date.getYear(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).getDayOfWeek();
        return dow.getValue();
    }

    /**
     * Установка стиля выбранной ячейки по умолчанию
     */
    private void resetStylesBorder() {
        for (Node element : gridPane.getChildren()) {
            element.setStyle("-fx-border-width: 0.5; -fx-border-color: #76787a");
        }
    }

    /**
     * Установка цветов шрифта по умолчанию для чисел в ячейках
     */
    private void resetStylesFont() {
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
                element.setStyle(("-fx-fill: #000000"));
            }
        }
    }

    /**
     * Изменение календарного месяца при нажатии стрелки "вправо"
     */
    @FXML
    private void increaseMonth() {
        monthIncrease = true;
        quickDatePane.setVisible(false);
        resetStylesBorder();
        resetStylesFont();
        showCalendar();
    }

    /**
     * Изменение календарного месяца при нажатии стрелки "влево"
     */
    @FXML
    private void reduceMonth() {
        monthReduce = true;
        quickDatePane.setVisible(false);
        resetStylesBorder();
        resetStylesFont();
        showCalendar();
    }

    /**
     * Выделение сегодняшнего числа в ячейке цветом
     */
    private void highlightToday() {
        if (currentDate.getMonthValue() == LocalDate.now().getMonthValue() && currentDate.getYear() == LocalDate.now().getYear())
            cellElementCurrentDay.setStyle(("-fx-fill: #0000ff"));
    }

    /**
     * Получение названия месяца на русском языке в им. падеже для currentMonthText
     */
    private String getRusMonth(int month) {
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

    /**
     * Получение строки с датой выбранного календарного дня
     */
    private String getChosenDateString() {
        int count1 = 1;
        for (Node node : gridPane.getChildren()) {
            if (!node.getStyle().equals(CHOSEN_CELL_STYLE)) {
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

    /**
     * Получение номера месяца с нулем, если он - однозначное число, и наоборот
     */
    private String getMonthValueWithZero(LocalDate date) {
        if (date.getMonthValue() < 10) {
            return "0" + date.getMonthValue();
        } else {
            return "" + date.getMonthValue();
        }
    }

    /**
     * "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации этих ячеек и не только
     */
    private void mouseClickedHandlers() {
        int count = 0;
        for (Node element : gridPane.getChildren()) {
            Object object = listOfTexts.get(count);
            element.setOnMouseClicked(e -> {
                if (object instanceof Text) {
                    resetStylesBorder();
                    element.setStyle(CHOSEN_CELL_STYLE);
                    changeText();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

                    currentDateLD = LocalDate.parse(getChosenDateString(), formatter);
                    chosenDateString = getChosenDateString() + " г.";

                    chosenDayDetected = true;
                    cellSelected = true;
                    quickDatePane.setVisible(false);
                    clearListView();
                    clearNameAndTextEventField();
                    fillListView();

                    // Вывод в TextField описания выбранного события и заполнение времени
                    eventUpdateHandlers();

                    deleteChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setDisable(true);
                }
            });
            count++;
        }
    }

    /**
     * Вывод в TextField описания выбранного события и заполнение времени
     */
    private void eventUpdateHandlers() {
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

    /**
     * Очистка полей eventTextField и eventListView
     */
    private void clearListView() {
        eventListView.getItems().clear();
        numberEvent = 0;
        eventTextField.clear();
    }

    /**
     * Заполнение ListView для выбранного дня с помощью HashMap
     */
    private void fillListView() {
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

    /**
     * Сортировка событий по времени
     */
    private void sortEventsForChosenDay() {
        int dayFirstKey = getKeyForChosenDate(getChosenDateString());
        QuickSort.quickSortTreeMap(eventMemory, dayFirstKey, dayFirstKey + eventListView.getItems().size() - 1);
    }

    /**
     * Становление кнопки addNewNoteButton активной, если день выбран и поле eventNameField заполнено, в ином случае - неактивной
     */
    private void addListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> addNewNoteButton.setDisable(!cellSelected || eventNameField.getText().isEmpty() || editingIsActive));
    }

    /**
     * Удаление события в мапе и смещения всех остальных событий "влево"
     */
    private void deleteHandler(int key) {
        deleteChooseNoteButton.setOnMouseClicked(event -> {
            eventNames.remove(eventListView.getSelectionModel().getSelectedIndex());
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

    /**
     * Очистка полей eventNameField и eventTextField
     */
    private void clearNameAndTextEventField() {
        editChooseNoteButton.setText(EDIT);
        eventNameField.clear();
        eventTextField.clear();
        hours.setValue("");
        minutes.setValue("");
    }

    /**
     * Логика редактирования событий при нажатии на соответствующую кнопку
     */
    private void editHandler(int key) {
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

    /**
     * Становление поля описания для заметки редактируемым только если введено название заметки
     */
    private void textFieldListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> eventTextField.setEditable(!eventNameField.getText().isEmpty()));
    }

    /**
     * Изменение даты в текстовом представлении в верхней части календаря
     */
    private void changeText() {
        chosenDateText.setText(getChosenDateString() + " г.");
        currentDateString = getChosenDateString();
    }

    /**
     * Возврат ключа для события, выбранного мышкой
     */
    private Integer getKeyForChosenEvent() {
        return getKeyForChosenDate(chosenDateString) + eventListView.getSelectionModel().getSelectedIndex();
    }

    /**
     * Вставка в monthOfQuickDate и yearOfQuickDate месяца и года текущей рахвертки календаря соответственно при нажатии на currentMonthText
     */
    @FXML
    private void quickJumpToDate(MouseEvent event) {
        setMonth();
        setYear();
    }

    /**
     * Заполнение ComboBox hours часами от 00 до 23 в выпадающем списке
     */
    @FXML
    private void showAllHours(MouseEvent event) {
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

    /**
     * Заполнение ComboBox minutes минутами от 00 до 59 в выпадающем списке
     */
    @FXML
    private void showAllMinutes(MouseEvent event) {
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

    /**
     * Получение часов времени события
     */
    private String getEventHours() {
        if (hours.getSelectionModel().getSelectedItem().isEmpty()) {
            return "00";
        } else {
            return (hours.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Получение минут времени события
     */
    private String getEventMinutes() {
        if (minutes.getSelectionModel().getSelectedItem().isEmpty()) {
            return "00";
        } else {
            return (minutes.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Отображение текущего времени
     */
    private void printTimeNow() {
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

    /**
     * Выделение выбранного месяца в выпадающем списке monthOfQuickDate
     */
    @FXML
    private void showMonth(MouseEvent event) {
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

    /**
     * Выделение выбранного года в выпадающем списке yearOfQuickDate
     */
    @FXML
    private void showYear(MouseEvent event) {
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

    /**
     * "Навешивание" обработчика событий на monthOfQuickDate для отслеживания изменений в этом поле
     */
    private void addMonthComboBoxListener() {
        monthOfQuickDate.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            quickMonth = monthOfQuickDate.getSelectionModel().getSelectedIndex() + 1;
            quickYear = currentDate.getYear();
            dateIsQuick = true;
            if (quickDatePane.isVisible()) {
                showCalendar();
            }
            quickDatePane.setVisible(false);
        });
    }

    /**
     * "Навешивание" обработчика событий на yearOfQuickDate для отслеживания изменений в этом поле
     */
    private void addYearComboBoxListener() {
        yearOfQuickDate.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            quickYear = newValue;
            quickMonth = currentDate.getMonthValue();
            dateIsQuick = true;
            if (quickDatePane.isVisible()) {
                showCalendar();
            }
            quickDatePane.setVisible(false);
        });
    }

    /**
     * Заполнение выпадающего списка monthOfQuickDate названиями всех месяцев
     */
    private void fillAllMonth() {
        ObservableList<String> monthList = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; ++i) {
            monthList.add(getRusMonth(i));
        }
        monthOfQuickDate.setItems(monthList);
    }

    /**
     * Заполнение выпадающего списка yearOfQuickDate годами диапозоном [текущий год - 100; текущий год - 100]
     */
    private void fillAllYears() {
        ObservableList<Integer> yearsList = FXCollections.observableArrayList();
        int RANGE_OF_YEARS = 100;
        for (int i = currentDate.getYear() - RANGE_OF_YEARS; i <= currentDate.getYear() + RANGE_OF_YEARS; ++i) {
            yearsList.add(i);
        }
        yearOfQuickDate.setItems(yearsList);
    }

    /**
     * Прокручивание выпадающего списка к выбранному месяцу или году
     */
    private void automaticScroll(ComboBox<?> comboBox, int index) {
        ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) comboBox.getSkin();
        ((ListView<?>) skin.getPopupContent()).scrollTo(index);
    }

    /**
     * Применение текущего месяца к значению monthOfQuickDate
     */
    private void setMonth() {
        monthOfQuickDate.setValue(getRusMonth(currentDate.getMonthValue()));
        quickDatePane.setVisible(true);
    }

    /**
     * Применение текущего года к значению yearOfQuickDate
     */
    private void setYear() {
        yearOfQuickDate.setValue(currentDate.getYear());
        quickDatePane.setVisible(true);
    }

    /**
     * Метод, вызываемый автоматически при запуске программы
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCalendar();
        fillAllMonth();
        fillAllYears();
        addMonthComboBoxListener();
        addYearComboBoxListener();
        mouseClickedHandlers();
        addListener();
        textFieldListener();
        printTimeNow();
    }
}