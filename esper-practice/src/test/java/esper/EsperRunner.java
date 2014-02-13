package esper;

import com.espertech.esper.client.EPServiceProvider;

import java.util.*;

public class EsperRunner {

    private EPServiceProvider epServiceProvider;
    private long startTime;
    private Timer timer;

    public EsperRunner(EPServiceProvider epServiceProvider) {
        this.epServiceProvider = epServiceProvider;
    }

    public void start(List<ScheduledEvent> scheduledEvents) {
        startTime = System.currentTimeMillis() - 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new SendEventTask(scheduledEvents), 0, 100);
    }

    public void stop() {
        if (timer != null) timer.cancel();
    }

    private void sendEvent(Object event) {
        System.out.printf("GEN\t%5.2f\t%s\n", elapsedTime(), event);
        epServiceProvider.getEPRuntime().sendEvent(event);
    }

    public double elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public void startSendingAndSleepAndStop(List<ScheduledEvent> seList, int sleeptimeInSeconds) {
        start(seList);
        SleepUtil.sleepSeconds(9);
        stop();
    }

    private class SendEventTask extends TimerTask {
        private int tick = -1;
        private Map<Integer, List<Object>> scheduledMap = new TreeMap<>();

        public SendEventTask(List<ScheduledEvent> schedules) {
            for (ScheduledEvent schedule : schedules) {
                addSchedule(schedule);
            }
        }

        private void addSchedule(ScheduledEvent schedule) {
            List<Object> eventList = scheduledMap.get(schedule.getTime());
            if (eventList == null) {
                eventList = new ArrayList<>();
                scheduledMap.put(schedule.getTime(), eventList);
            }
            eventList.add(schedule.getEvent());
        }

        @Override
        public void run() {
            tick++;
            List<Object> events = scheduledMap.get(tick * 100);
            if (events != null)
                for (Object event : events)
                    sendEvent(event);

        }
    }

}
