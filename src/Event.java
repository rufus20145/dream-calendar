public class Event {

    private String dateEvent;
    private String titleEvent;
    private String textEvent;
    private int numberEvent;

    public Event(String dateEvent, String titleEvent, String textEvent, int numberEvent) {
        this.dateEvent = dateEvent;
        this.titleEvent = titleEvent;
        this.textEvent = textEvent;
        this.numberEvent = numberEvent;
    }

    public Event(String dateEvent, String titleEvent, int numberEvent) {
        this.dateEvent = dateEvent;
        this.titleEvent = titleEvent;
        this.numberEvent = numberEvent;
    }

    // Нашлось событие на выбранную дату
    public boolean dateMatch(String selectDay) {
        return selectDay.equals(dateEvent);
    }

    public String getDateEvent() {
        return dateEvent;
    }

    public String getTitleEvent() {
        return titleEvent;
    }

    public String getTextEvent() {
        return textEvent;
    }

    public int getNumberEvent() {
        return numberEvent;
    }
}
