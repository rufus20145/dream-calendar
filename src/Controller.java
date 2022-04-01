import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Controller implements Initializable {

    public static Map<Integer, Event> notesMemory = new TreeMap<>();
    public static ObservableList<String> notesNames = FXCollections.observableArrayList();
    public static int numberEvent = 0;

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
                newEvent = new Event(getChosenDate(), nameNote.getText());
            } else {
                newEvent = new Event(getChosenDate(), nameNote.getText(), textFieldNote.getText());
            }
            int keyNotesMemory = getKeyForChosenDate(getChosenDate()) + numberEvent;
            numberEvent++;
            notesMemory.put(keyNotesMemory, newEvent);

            // Добавляем в ListView название события
            notesNames.add(nameNote.getText());
            listNotes.setItems(notesNames);
            updateHandlers();

            saveEditHandler = false;
            editChooseNoteButton.setText("edit");

            // Очистка полей после создания нового события
            clearNameAndTextField();

            // Вывод всех ключей событий
//            printAllKeysFromNotesMemoryMap();
        }
    }

    // Генерируем ключ для первого события выбранного дня
    public Integer getKeyForChosenDate(String chosenDate) {
        char[] chosenDateInChar = chosenDate.toCharArray();
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
                        currentDayString = getDayValueWithZero +
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
                    clearListView();
                    nameNote.clear();
                    textFieldNote.clear();

                    // Заполнение ListView для выбранного дня с помощью HashMap
                    fillListView();

                    // Вывод в TextField описания выбранного события
                    updateHandlers();

                    deleteChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setDisable(true);
                    editChooseNoteButton.setText("edit");
                }
            });
            count++;
        }
    }

    // Вывод в TextField описания выбранного события
    public void updateHandlers() {
        if (!notesNames.isEmpty()) {
            listNotes.setOnMouseClicked(event -> {
                clearNameAndTextField();
                saveEditHandler = false;
                editChooseNoteButton.setText("edit");
                int selectedIndex = listNotes.getSelectionModel().getSelectedIndex();
//                System.out.println(selectedIndex + " selected index");
//                System.out.println(listNotes.getSelectionModel().isSelected(selectedIndex) + " selection");
                if (listNotes.getSelectionModel().isSelected(selectedIndex)) {
                    int key = getKeyForChosenEvent();
//                System.out.println("clicked on " + listNotes.getSelectionModel().getSelectedItem());
                    textFieldNote.setText(
                            notesMemory.get(key)
                                       .getTextEvent()
                    );
                    deleteChooseNoteButton.setDisable(false);
                    deleteHandler(key);
                    editChooseNoteButton.setDisable(false);
                    editHandler(getKeyForChosenEvent());
                }
            });
        }
    }

    public void clearListView() {
        listNotes.getItems().clear();
        numberEvent = 0;
        textFieldNote.clear();
    }

    public void clearTextEventField() {
        nameNote.setOnMouseClicked(event -> {
//            System.out.println("clicked on nameNote");
//            textFieldNote.clear();
            deleteChooseNoteButton.setDisable(true);
            editChooseNoteButton.setDisable(true);
        });
    }

    // Заполнение ListView для выбранного дня с помощью HashMap
    public void fillListView() {
        int i = 0;
        for (Integer key : notesMemory.keySet()) {
            int sum = getKeyForChosenDate(getChosenDate()) + i;
//            System.out.println("getKeyForChosenDay = " + getKeyForChosenDate(getChosenDate()));
//            System.out.println("existKey = " + key);
            if (sum == key) {
                notesNames.add(notesMemory.get(sum).getTitleEvent());
                listNotes.setItems(notesNames);
                numberEvent++;
                i++;
            }
        }
    }

    // Делать кнопку addNewNoteButton активной, если день выбран и поле nameNote заполнено, в ином случае - неактивной
    public void addListener() {
        nameNote.textProperty().addListener((observable, oldValue, newValue) -> {
            addNewNoteButton.setDisable(!dayIsChosen || nameNoteIsNoExist());
        });
    }

    public void deleteHandler(int key) {
        deleteChooseNoteButton.setOnMouseClicked(event1 -> {
//            System.out.println("note will be deleted");
            notesNames.remove(listNotes.getSelectionModel().getSelectedIndex());
//            System.out.println(notesNames);

            // Удаление события в мапе и смещения всех остальных событий "влево"
            notesMemory.remove(key);
            int countKey = key + 1;
            int keyCopy = key;
            while (notesMemory.containsKey(countKey)) {
                notesMemory.put(keyCopy, notesMemory.get(countKey));
                keyCopy++;
                countKey++;
            }
            notesMemory.remove(keyCopy);
            numberEvent--;

            // Проверка корректности содержимых событий в мапе после удаления
            int count = 0;
            for (Integer asd : notesMemory.keySet()) {
                if (asd == getKeyForChosenDate(getChosenDate()) + count) {
                    System.out.println(notesMemory.get(asd));
                    count++;
                }
            }
            System.out.println("------------------------");

            clearNameAndTextField();
            editChooseNoteButton.setDisable(true);
            deleteChooseNoteButton.setDisable(true);
            editChooseNoteButton.setText("edit");
        });
    }

    public void clearNameAndTextField() {
        editChooseNoteButton.setText("edit");
        nameNote.clear();
        textFieldNote.clear();
    }

    boolean saveEditHandler = false;
    public void editHandler(int key) {
        editChooseNoteButton.setOnMouseClicked(event2 -> {
            if (!saveEditHandler) {
                System.out.println("note will be edit");
                System.out.println(key);
                nameNote.setText(notesMemory.get(key).getTitleEvent());
                editChooseNoteButton.setText("save");
                textFieldNote.requestFocus();
                textFieldNote.deselect();
                deleteChooseNoteButton.setDisable(true);
                addNewNoteButton.setDisable(true);
                saveEditHandler = true;
            } else {
                int selectedIndex = listNotes.getSelectionModel().getSelectedIndex();
                notesNames.set(selectedIndex, nameNote.getText());
                Event newEvent;
                if (textFieldIsNoExist()) {
                    newEvent = new Event(getChosenDate(), nameNote.getText());
                } else {
                    newEvent = new Event(getChosenDate(), nameNote.getText(), textFieldNote.getText());
                }
                notesMemory.put(key, newEvent);
                saveEditHandler = false;
            }
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

    // Вывести все существующие ключи из TreeMap
    public void printAllKeysFromNotesMemoryMap() {
        for (Integer key : notesMemory.keySet()) {
            System.out.println(key);
        }
    }

    // Вернуть ключ для события, выбранного мышкой
    public Integer getKeyForChosenEvent() {
        return getKeyForChosenDate(getChosenDate()) + listNotes.getSelectionModel().getSelectedIndex();
    }

    // Метод, вызываемый автоматически при запуске программы
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCalendar();
        setHandlers();
        addListener();
        textFieldListener();
//        clearTextEventField();
    }
}