public class Event {

    private EventTypes type;
    private String eventDate;
    private String eventTitle;
    private String eventText;
    private String eventHours;
    private String eventMinutes;

    public Event(EventTypes type, String eventDate, String eventTitle, String eventText, String eventHours,
            String eventMinutes) {
        this.setType(type);
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.eventText = eventText;
        this.eventHours = eventHours;
        this.eventMinutes = eventMinutes;
    }

    public Event(String eventDate, String eventTitle, String eventText, String eventHours, String eventMinutes) {
        this(EventTypes.NOTIFICATION, eventDate, eventTitle, eventText, eventHours, eventMinutes);
    }

    public Event(String eventDate, String eventTitle, String eventHours, String eventMinutes) {
        this(eventDate, eventTitle, "", eventHours, eventMinutes);
    }

    public Event(EventTypes type, String eventDate, String eventTitle, String eventHours, String eventMinutes) {
        this(type, eventDate, eventTitle, "", eventHours, eventMinutes);
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventText() {
        return eventText;
    }

    public String getEventHours() {
        return eventHours;
    }

    public String getEventMinutes() {
        return eventMinutes;
    }

    public EventTypes getType() {
        return type;
    }

    private void setType(EventTypes type) {
        this.type = type;
    }

}