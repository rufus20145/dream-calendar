import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.net.ssl.HostnameVerifier;

public class EventController extends Controller {
    private ListView<String> eventListView;
    private boolean editingIsActive = false;
    private int numberEvent = 0; // Номер события для каждого отдельного дня
    public static SortedMap<Integer, Event> eventMemory = new TreeMap<>();
    private boolean switchEditToSaveButton = false; // Сменить кнопку edit на save
    private boolean cellSelected;
    private ImageView addNewNoteButton;
    private ImageView editChooseNoteButton;
    private ObservableList<String> eventNames = FXCollections.observableArrayList();
    private TextArea eventTextField;
    private ImageView deleteChooseNoteButton;
    private ComboBox<String> hours;
    private ComboBox<String> minutes;
    private LocalDate currentDate;
    private TextField eventNameField;
    private GridPane gridPane;
    private String chosenDateString;
    public QuickSort quickSort;

    public final Image EDIT_IMAGE = new Image("/icons/edit_icon.png");
    public final Image SAVE_IMAGE = new Image("/icons/save_icon.png");

    public void setEventListView(ListView<String> eventListView) {
        this.eventListView = eventListView;
    }

    public void setAddNewNoteButton(ImageView addNewNoteButton) {
        this.addNewNoteButton = addNewNoteButton;
    }

    public void setEditChooseNoteButton(ImageView editChooseNoteButton) {
        this.editChooseNoteButton = editChooseNoteButton;
    }

    public void setEventTextField(TextArea eventTextField) {
        this.eventTextField = eventTextField;
    }

    public void setCellSelected(boolean cellSelected) {
        this.cellSelected = cellSelected;
    }

    public void setEventNameField(TextField eventNameField) {
        this.eventNameField = eventNameField;
    }

    public void setHours(ComboBox<String> hours) {
        this.hours = hours;
    }

    public void setMinutes(ComboBox<String> minutes) {
        this.minutes = minutes;
    }

    public void setDeleteChooseNoteButton(ImageView deleteChooseNoteButton) {
        this.deleteChooseNoteButton = deleteChooseNoteButton;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public void setChosenDateString(String chosenDateString) {
        this.chosenDateString = chosenDateString;
    }

    /**
     * Добавление события
     */
    public void addNewNoteMethod(String chosenDateString, boolean chosenDayDetected) {
        if (chosenDayDetected) {
            if (hours.getSelectionModel().isEmpty() && minutes.getSelectionModel().isEmpty()) {
                hours.requestFocus();
            } else {
                Event newEvent;
                if (eventTextField.getText().isBlank()) {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), getEventHours(),
                            getEventMinutes());
                } else {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), eventTextField.getText(),
                            getEventHours(), getEventMinutes());
                }
                int keyEvent = getKeyForChosenDate(chosenDateString) + numberEvent;
                numberEvent++;
                eventMemory.put(keyEvent, newEvent);

                // Добавляем в ListView название события
                eventNames.add(eventNameField.getText());
                eventListView.setItems(eventNames);
                eventUpdateHandlers();

                switchEditToSaveButton = false;
                editChooseNoteButton.setImage(EDIT_IMAGE);

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
    Integer getKeyForChosenDate(String chosenDateString) {
        char[] chosenDateInChar = chosenDateString.toCharArray();
        String chosenDayInString = "" + chosenDateInChar[0] + chosenDateInChar[1];
        String chosenMonthInString = "" + chosenDateInChar[3] + chosenDateInChar[4];
        String chosenYearInString = "" + chosenDateInChar[6] + chosenDateInChar[7] + chosenDateInChar[8]
                + chosenDateInChar[9];
        int chosenDay = Integer.parseInt(chosenDayInString);
        int chosenMonth = Integer.parseInt(chosenMonthInString);
        int chosenYear = Integer.parseInt(chosenYearInString);
        int KEY_GENERATION_COEFF = 100;
        return chosenDay * chosenMonth * chosenYear * KEY_GENERATION_COEFF;
    }

    /**
     * Вывод в TextField описания выбранного события и заполнение времени
     */
    void eventUpdateHandlers() {
        if (!eventNames.isEmpty()) {
            eventListView.setOnMouseClicked(event -> {
                hours.setDisable(true);
                hours.setStyle("-fx-opacity: 1.0");
                minutes.setDisable(true);
                minutes.setStyle("-fx-opacity: 1.0");
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
    public void clearListView() {
        eventListView.getItems().clear();
        numberEvent = 0;
        eventTextField.clear();
    }

    /**
     * Заполнение ListView для выбранного дня с помощью HashMap
     */
    public void fillListView() {
        int count = 0;
        for (Integer key : eventMemory.keySet()) {
            int sum = getKeyForChosenDate(getChosenDateString(0, gridPane, currentDate)) + count;
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
        int dayFirstKey = getKeyForChosenDate(getChosenDateString(0, gridPane, currentDate));

        quickSort = new QuickSort();
        quickSort.quickSortTreeMap(eventMemory, dayFirstKey, dayFirstKey + eventListView.getItems().size() - 1);
    }

    /**
     * Становление кнопки addNewNoteButton активной, если день выбран и поле
     * eventNameField заполнено, в ином случае - неактивной
     * и становление кнопки editChooseNoteButton активной, если запущен режим
     * редактирования события и поле eventNameField заполнено
     */
    public void addEventNameFieldListener() {
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> addNewNoteButton
                .setDisable(!cellSelected || eventNameField.getText().isBlank()));
        eventNameField.textProperty().addListener((observable, oldValue, newValue) -> editChooseNoteButton
                .setDisable(eventNameField.getText().isBlank() || !editingIsActive));
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
            if (numberEvent == 0) {
                ((Pane) Controller.chosenCell).getChildren().get(0).setVisible(false);
            }

            clearNameAndTextEventField();
            editChooseNoteButton.setDisable(true);
            deleteChooseNoteButton.setDisable(true);
        });
    }

    /**
     * Очистка полей eventNameField и eventTextField
     */
    void clearNameAndTextEventField() {
        editChooseNoteButton.setImage(EDIT_IMAGE);
        eventNameField.clear();
        eventTextField.clear();
        hours.setValue("");
        minutes.setValue("");
    }

    /**
     * Логика редактирования событий при нажатии на соответствующую кнопку
     */
    private void editHandler(int key) {
        editChooseNoteButton.setOnMouseClicked(event -> {
            editingIsActive = true;
            if (!switchEditToSaveButton) {
                eventNameField.setText(eventMemory.get(key).getEventTitle());
                editChooseNoteButton.setImage(SAVE_IMAGE);
                eventTextField.deselect();
                deleteChooseNoteButton.setDisable(true);
                addNewNoteButton.setDisable(true);
                switchEditToSaveButton = true;
            } else {

                int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
                eventNames.set(selectedIndex, eventNameField.getText());
                Event newEvent;
                if (eventNameField.getText().isBlank()) {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), getEventHours(),
                            getEventMinutes());
                } else {
                    newEvent = new Event(chosenDateString, eventNameField.getText(), eventTextField.getText(),
                            getEventHours(), getEventMinutes());
                }
                eventMemory.put(key, newEvent);
                switchEditToSaveButton = false;
                editChooseNoteButton.setImage(EDIT_IMAGE);
                eventListView.getSelectionModel().select(selectedIndex);
                deleteChooseNoteButton.setDisable(false);
                eventNameField.clear();
                eventTextField.clear();
                hours.setValue("");
                minutes.setValue("");
            }
            hours.setDisable(false);
            minutes.setDisable(false);
        });
        editingIsActive = false;
    }

    /**
     * Возврат ключа для события, выбранного мышкой
     */
    private Integer getKeyForChosenEvent() {
        return getKeyForChosenDate(chosenDateString) + eventListView.getSelectionModel().getSelectedIndex();
    }

    /**
     * Становление поля описания для заметки редактируемым только если введено
     * название заметки
     */
    public void textFieldListener() {
        eventNameField.textProperty().addListener(
                (observable, oldValue, newValue) -> eventTextField.setEditable(!eventNameField.getText().isBlank()));
    }

    /**
     * Отобразить все часы в выпадающем списке
     */
    public void showAllHoursMethod() {
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
     * Отобразить все минуты в выпадающем списке
     */
    public void showAllMinutesMethod() {
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
     * @return часы
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
     * @return минуты
     */
    private String getEventMinutes() {
        if (minutes.getSelectionModel().getSelectedItem().isEmpty()) {
            return "00";
        } else {
            return (minutes.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Изменение свойств и полей при нажатии на область ввода названия события
     */
    public void eventNameFieldHandlersControl() {
        eventNameField.setOnMouseClicked(event -> {
            hours.setDisable(false);
            minutes.setDisable(false);
            deleteChooseNoteButton.setDisable(true);
            editChooseNoteButton.setDisable(true);
            if (eventNameField.getText().trim().isEmpty()) {
                eventTextField.clear();
                hours.setValue("");
                minutes.setValue("");
            }
        });
    }
}