package esper;

public class ScheduledEvent {
    private Integer time;
    private Object event;

    public ScheduledEvent(Integer time, Object event) {
        this.time = time;
        this.event = event;
    }

    public Integer getTime() {
        return time;
    }

    public Object getEvent() {
        return event;
    }
}
