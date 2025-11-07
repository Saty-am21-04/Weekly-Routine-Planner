package SwingGUIWork;

public class RoutineEntry<T> {
    private String day;
    private int startHour, startMinute, endHour, endMinute;
    private T activity;

    public RoutineEntry(String day, int startHour, int startMinute, int endHour, int endMinute, T activity) {
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.activity = activity;
    }

    public String getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }
    
    public int getStartMinute() {
        return startMinute;
    }
    
    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }
    
    public T getActivity() {
        return activity;
    }

    public String getFormattedEntry() {
        String startTime = String.format("%02d:%02d", getStartHour(), getStartMinute());
        String endTime = String.format("%02d:%02d", getEndHour(), getEndMinute());

        return startTime + " - " + endTime + " : " + activity.toString();
    }

    @Override
    public String toString() {
        return getFormattedEntry();
    }
}