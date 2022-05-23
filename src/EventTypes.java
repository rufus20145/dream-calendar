public enum EventTypes {
    DEFAULT(0, "Без категории"),
    NOTIFICATION(1, "Заметка"),
    BIRTHDAY(2, "День рождения"),
    HOLIDAY(3, "Праздник"),
    MEETING(4, "Встреча");

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

    @Override
    public String toString() {
        return getTitle();
    }
}
