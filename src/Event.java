public class Event extends Controller {

    private String eventDate;
    private String eventTitle;
    private String eventText;
    private String eventHours;
    private String eventMinutes;

    public Event(String eventDate, String eventTitle, String eventText, String eventHours, String eventMinutes) {
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.eventText = eventText;
        this.eventHours = eventHours;
        this.eventMinutes = eventMinutes;
    }

    public Event(String eventDate, String eventTitle, String eventHours, String eventMinutes) {
        this.eventDate = eventDate;
        this.eventTitle = eventTitle;
        this.eventHours = eventHours;
        this.eventMinutes = eventMinutes;
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
}