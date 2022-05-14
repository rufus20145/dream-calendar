public enum EventTypes {
    NOTIFICATION(0, "Уведомление"),
    BIRTHDAY(1, "День рождения"),
    HOLIDAY(2, "Праздник"),
    MEETEING(3, "Встреча");

    private int type;
    private String title;

    private EventTypes(int type, String title) {
        this.setType(type);
        this.setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    private void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return getTitle();
    }
}
