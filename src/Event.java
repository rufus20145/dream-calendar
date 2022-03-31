public class Event {

    private String dateEvent;
    private String titleEvent;
    private String textEvent;
//    private int numberEvent;

    public Event(String dateEvent, String titleEvent, String textEvent) {
        this.dateEvent = dateEvent;
        this.titleEvent = titleEvent;
        this.textEvent = textEvent;
    }

    public Event(String dateEvent, String titleEvent) {
        this.dateEvent = dateEvent;
        this.titleEvent = titleEvent;
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

//    public int getNumberEvent() {
//        return numberEvent;
//    }
}
