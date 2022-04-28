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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

public class Controller implements Initializable {
    /**
     * Объявление констант
     */
    private static final int NUM_OF_ALL_CELLS = 42;
    public static final String CHOSEN_CELL_STYLE = "-fx-border-width: 2.5; -fx-border-color: #000000";

    /**
     * Объявление переменных
     */
    public String currentDateString; // Дата текущего дня в String
    private String chosenDateString;
    private int currentDay;
    private int numbOfCell;
    private int quickMonth;
    private int quickYear;
    public static volatile boolean stopShowTime = false;
    private boolean chosenDayDetected = false; // Найден выбранный ранее день
    public boolean currentDayLeftTopDetected = false; // Текущий день слева вверху обнаружен
    private boolean monthIncrease = false, monthReduce = false;
    public boolean cellSelected = false;
    private boolean dateIsQuick = false;
    private static HashMap<Integer, String> memoryNumbersByCells = new HashMap<>(); // Числа по номерам ячеек на выбранный месяц
    private Node cellElementCurrentDay; // Ячейка текущего дня для выделения ее "синим" цветом
    public LocalDate currentDateLD; // Дата текущего дня в LD
    public LocalDate currentDate;
    private ObservableList<Node> listOfTexts;
    private ObservableList<Node> listOfPane;
    public CurrMonthAndYear currMonthAndYear;
    public ChosenDateController chosenDate;
    private QuickDateController quickDate;
    private EventController eventController;

    @FXML
    public Button addNewNoteButton;

    @FXML
    public Label currentTime;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public Text chosenDateText;

    @FXML
    public Text currentMonthText;

    @FXML
    private Button deleteChooseNoteButton;

    @FXML
    public Button editChooseNoteButton;

    @FXML
    public GridPane gridPane;

    @FXML
    public ListView<String> eventListView;

    @FXML
    public TextField eventNameField;

    @FXML
    private TextArea eventTextField;

    @FXML
    public Text currentDayConst;

    @FXML
    public ComboBox<String> hours;

    @FXML
    public ComboBox<String> minutes;

    @FXML
    public Pane quickDatePane;

    @FXML
    public ComboBox<String> monthOfQuickDate;

    @FXML
    public ComboBox<Integer> yearOfQuickDate;

    @FXML
    void showCalendar() {
        currentDate = getCurrentDate();
        eventController.setCurrentDate(currentDate);
        currMonthAndYear = new CurrMonthAndYear(currentMonthText, currentDate);
        currMonthAndYear.setCurrentMonthText();
        quickDate = new QuickDateController(monthOfQuickDate, yearOfQuickDate, currMonthAndYear, gridPane, anchorPane,
                currentMonthText, quickDatePane, currentDate);

        int firstActiveCell = getCellNumberFirstDayMonth(currentDate);

        listOfTexts = anchorPane.getChildren();
        listOfPane = gridPane.getChildren();
        currentDay = currentDate.getDayOfMonth();

        resetCells(anchorPane);

        createActiveCalendar(firstActiveCell);
        createPassiveCalendar(firstActiveCell);

        chosenDate = new ChosenDateController(chosenDateText, currentDate, anchorPane, gridPane);
        chosenDate.showCurrentDate();
        cellElementCurrentDay = chosenDate.getCellElementCurrentDay();

        monthReduce = false;
        monthIncrease = false;
        dateIsQuick = false;

        highlightToday();
        if (cellSelected) {
            showChosenDay(firstActiveCell);
        }
    }

    /**
     * Выделение ячейки с ранее выбранным днем, если таковой есть на текущей
     * развертке календаря
     */
    private void showChosenDay(int firstActiveCell) {
        firstActiveCell--;
        for (int i = 0; i < NUM_OF_ALL_CELLS; ++i) {
            Object text = listOfTexts.get(i);
            if (text instanceof Text) {
                if (cellBeforeCurrMonth(firstActiveCell, i, text) || cellOfCurrMonth(firstActiveCell, i, text)
                        || cellAfterCurrMonth(firstActiveCell, i, text)) {
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
        return numOfCell < firstActiveCell && ((currentDate.getMonthValue() == 1
                && currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.minusYears(1).getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                || (currentDateLD.getMonthValue() == currentDate.minusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()))));
    }

    /**
     * Проверка расположения ячейки на выбранном месяце
     */
    private boolean cellOfCurrMonth(int firstActiveCell, int numOfCell, Object text) {
        return numOfCell >= firstActiveCell && numOfCell < firstActiveCell + currentDate.lengthOfMonth()
                && currentDateLD.getMonthValue() == currentDate.getMonthValue()
                && currentDateLD.getYear() == currentDate.getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth()));
    }

    /**
     * Проверка расположения ячейки после выбранного месяца
     */
    private boolean cellAfterCurrMonth(int firstActiveCell, int numOfCell, Object text) {
        return numOfCell >= firstActiveCell + currentDate.lengthOfMonth() && ((currentDate.getMonthValue() == 12
                && currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.plusYears(1).getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))
                || ((currentDateLD.getMonthValue() == currentDate.plusMonths(1).getMonthValue()
                && currentDateLD.getYear() == currentDate.getYear()
                && Objects.equals(((Text) text).getText(), Integer.toString(currentDateLD.getDayOfMonth())))));
    }

    /**
     * Добавление события
     */
    @FXML
    private void addNewNote() {
        eventController.addNewNoteMethod(chosenDateString, chosenDayDetected);
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
    }

    /**
     * Расстановка чисел в предыдущем и следующем календарных месяцах
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
        resetStylesBorder();
        resetStylesFont();
        quickDatePane.setVisible(false);
        showCalendar();
    }

    /**
     * Изменение календарного месяца при нажатии стрелки "влево"
     */
    @FXML
    private void reduceMonth() {
        monthReduce = true;
        resetStylesBorder();
        resetStylesFont();
        quickDatePane.setVisible(false);
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
     * Получение строки с датой выбранного календарного дня
     */
    String getChosenDateString(GridPane gridPane, LocalDate currentDate) {
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
     * "Навешивание" обработчиков событий (кликов мыши) на ячейки для стилизации
     * этих ячеек и не только
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
                    currentDateLD = LocalDate.parse(getChosenDateString(gridPane, currentDate), formatter);
                    chosenDateString = getChosenDateString(gridPane, currentDate) + " г.";
                    eventController.setChosenDateString(chosenDateString);
                    chosenDayDetected = true;
                    cellSelected = true;
                    eventController.setCellSelected(true);
                    quickDatePane.setVisible(false);
                    eventController.clearListView();
                    eventController.clearNameAndTextEventField();
                    eventController.fillListView();
                    eventController.eventUpdateHandlers();  // Вывод в TextField описания выбранного события и заполнение времени
                    deleteChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setDisable(true);
                }
            });
            count++;
        }
    }

    /**
     * Изменение даты в текстовом представлении в верхней части календаря
     */
    private void changeText() {
        chosenDateText.setText(getChosenDateString(gridPane, currentDate) + " г.");
        currentDateString = getChosenDateString(gridPane, currentDate);
    }

    /**
     * Вставка в monthOfQuickDate и yearOfQuickDate месяца и года текущей рахвертки
     * календаря соответственно при нажатии на currentMonthText
     */
    @FXML
    private void quickJumpToDate(MouseEvent event) {
        quickDate.setMonth(currentDate);
        quickDate.setYear(currentDate);
    }

    /**
     * Заполнение ComboBox hours часами от 00 до 23 в выпадающем списке
     */
    @FXML
    private void showAllHours(MouseEvent event) {
        eventController.showAllHoursMethod();
    }

    /**
     * Заполнение ComboBox minutes минутами от 00 до 59 в выпадающем списке
     */
    @FXML
    private void showAllMinutes(MouseEvent event) {
        eventController.showAllMinutesMethod();
    }

    /**
     * Выделение выбранного месяца в выпадающем списке monthOfQuickDate
     */
    @FXML
    private void showMonth(MouseEvent event) {
        quickDate.showMonthControl();
    }

    /**
     * Выделение выбранного года в выпадающем списке yearOfQuickDate
     */
    @FXML
    private void showYear(MouseEvent event) {
        quickDate.showYearControl();
    }

    /**
     * "Навешивание" обработчика событий на monthOfQuickDate для отслеживания
     * изменений в этом поле
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
     * "Навешивание" обработчика событий на yearOfQuickDate для отслеживания
     * изменений в этом поле
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
     * Прокручивание выпадающего списка к выбранному месяцу или году
     */
    void automaticScroll(ComboBox<?> comboBox, int index) {
        ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>) comboBox.getSkin();
        ((ListView<?>) skin.getPopupContent()).scrollTo(index);
    }

    public void eventNameFieldHandlers() {
        eventNameField.setOnMouseClicked(event -> {
            eventController.eventNameFieldHandlersControl();
        });
    }

    public static Map<Integer, Event> getEvents() {
        return EventController.eventMemory;
    }

    public static void setEvents(SortedMap<Integer, Event> eventsFromFile) {
        EventController.eventMemory = (eventsFromFile != null) ? eventsFromFile : new TreeMap<>();
    }

    /**
     * Метод, вызываемый автоматически при запуске программы
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventController = new EventController();
        showCalendar();
        quickDate.fillAllMonth();
        quickDate.fillAllYears(currentDate);
        addMonthComboBoxListener();
        addYearComboBoxListener();
        mouseClickedHandlers();
        eventNameFieldHandlers();
        eventController.setGridPane(gridPane);
        eventController.setEventNameField(eventNameField);
        eventController.setAddNewNoteButton(addNewNoteButton);
        eventController.setEditChooseNoteButton(editChooseNoteButton);
        eventController.addEventNameFieldListener();
        eventController.setHours(hours);
        eventController.setMinutes(minutes);
        eventController.setEventTextField(eventTextField);
        eventController.setEventListView(eventListView);
        eventController.setDeleteChooseNoteButton(deleteChooseNoteButton);
        eventController.textFieldListener();
        Time time = new Time(currentTime);
        time.printTimeNow();
        ConstCurrentDate constCurrentDate = new ConstCurrentDate(currentDayConst, currentDate);
        constCurrentDate.printCurrentDayLeftTopTitle();
    }
}