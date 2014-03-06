package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EPLViewTest {

    private EsperRunner esperRunner;
    private EPServiceProvider epService;
    private UpdateListener avgListener;
    private UpdateListener eventListener;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("AccessLog", AccessLog.class);
        config.addEventType("StockTick", StockTick.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);

        avgListener = new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%f\n",
                            esperRunner.elapsedTime(),
                            eb.get("avg")
                    );
                }
            }
        };
        eventListener = new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\n",
                            esperRunner.elapsedTime(),
                            eb.getUnderlying()
                    );
                }
            }
        };
    }

    @Test
    @Ignore
    public void win_ext_timed() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select avg(responseTime) as avg from AccessLog.win:ext_timed(accessTime, 3 seconds)");
        eps.addListener(avgListener);

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new AccessLog("url", 100, 100)));
        seList.add(new ScheduledEvent(1000, new AccessLog("url", 2500, 200)));
        seList.add(new ScheduledEvent(2000, new AccessLog("url", 3400, 300)));
        seList.add(new ScheduledEvent(3000, new AccessLog("url", 3700, 400)));
        seList.add(new ScheduledEvent(4000, new AccessLog("url", 5300, 500)));
        seList.add(new ScheduledEvent(5000, new AccessLog("url", 5700, 600)));

        esperRunner.startSendingAndSleepAndStop(seList, 6);

        eps.destroy();
    }

    @Test
    @Ignore
    public void win_ext_timed_batch() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select avg(responseTime) as avg from AccessLog.win:ext_timed_batch(accessTime, 2 seconds)");
        eps.addListener(avgListener);

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new AccessLog("url", 100, 100)));
        seList.add(new ScheduledEvent(1000, new AccessLog("url", 2500, 200)));
        seList.add(new ScheduledEvent(2000, new AccessLog("url", 3400, 300)));
        seList.add(new ScheduledEvent(3000, new AccessLog("url", 3700, 400)));
        seList.add(new ScheduledEvent(4000, new AccessLog("url", 5300, 500)));
        seList.add(new ScheduledEvent(5000, new AccessLog("url", 5700, 600)));
        seList.add(new ScheduledEvent(6000, new AccessLog("url", 7400, 700)));

        esperRunner.startSendingAndSleepAndStop(seList, 7);

        eps.destroy();
    }

    @Test
    @Ignore
    public void win_time_accum() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select rstream * from AccessLog.win:time_accum(2 sec)");
        eps.addListener(eventListener);

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new AccessLog("url", 100)));
        seList.add(new ScheduledEvent(500, new AccessLog("url", 200)));
        seList.add(new ScheduledEvent(1000, new AccessLog("url", 300)));
        seList.add(new ScheduledEvent(3500, new AccessLog("url", 400)));
        seList.add(new ScheduledEvent(4000, new AccessLog("url", 500)));
        seList.add(new ScheduledEvent(4500, new AccessLog("url", 600)));
        seList.add(new ScheduledEvent(6000, new AccessLog("url", 700)));

        esperRunner.startSendingAndSleepAndStop(seList, 9);

        eps.destroy();
    }

    @Test
    @Ignore
    public void win_firsttime() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select * from AccessLog.win:firsttime(2 sec)");
        eps.addListener(eventListener);

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new AccessLog("url", 100)));
        seList.add(new ScheduledEvent(500, new AccessLog("url", 200)));
        seList.add(new ScheduledEvent(1000, new AccessLog("url", 300)));
        seList.add(new ScheduledEvent(3500, new AccessLog("url", 400)));
        seList.add(new ScheduledEvent(4000, new AccessLog("url", 500)));
        seList.add(new ScheduledEvent(4500, new AccessLog("url", 600)));
        seList.add(new ScheduledEvent(6000, new AccessLog("url", 700)));

        esperRunner.startSendingAndSleepAndStop(seList, 9);

        eps.destroy();
    }

    @Test
    public void std_unique() {
        final EPStatement eps = epService.getEPAdministrator().createEPL(
                "select * from StockTick.std:unique(code)");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\n",
                            esperRunner.elapsedTime(),
                            eb.getUnderlying()
                    );
                }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(400, new StockTick("name", "code1", 1000, 0, 0.0)));
        seList.add(new ScheduledEvent(500, new StockTick("name", "code1", 2000, 0, 0.0)));
        seList.add(new ScheduledEvent(600, new StockTick("name", "code2", 100, 0, 0.0)));

        seList.add(new ScheduledEvent(1500, new StockTick("name", "code1", 3000, 0, 0.0)));
        seList.add(new ScheduledEvent(1500, new StockTick("name", "code2", 200, 0, 0.0)));

        seList.add(new ScheduledEvent(2500, new StockTick("name", "code1", 4000, 0, 0.0)));

        seList.add(new ScheduledEvent(3500, new StockTick("name", "code1", 5000, 0, 0.0)));

        seList.add(new ScheduledEvent(6500, new StockTick("name", "code1", 6000, 0, 0.0)));
        seList.add(new ScheduledEvent(6500, new StockTick("name", "code2", 300, 0, 0.0)));

        seList.add(new ScheduledEvent(7500, new StockTick("name", "code1", 7000, 0, 0.0)));

        seList.add(new ScheduledEvent(10000, new StockTick("name", "code2", 800, 0, 0.0)));

        esperRunner.start(seList);
        //esperRunner.startSendingAndSleepAndStop(seList, 14);

        SleepUtil.sleepSeconds(3);

        SafeIterator<EventBean> iter = eps.safeIterator();
        while (iter.hasNext()) {
            EventBean bean = iter.next();
            System.out.printf("LLL:\t%5.2f\t%s\n", esperRunner.elapsedTime(), bean.getUnderlying());
        }
        iter.close();

        SleepUtil.sleepSeconds(3);

        SafeIterator<EventBean> iter2 = eps.safeIterator();
        while (iter2.hasNext()) {
            EventBean bean = iter2.next();
            System.out.printf("LLL:\t%5.2f\t%s\n", esperRunner.elapsedTime(), bean.getUnderlying());
        }
        iter2.close();

        SleepUtil.sleepSeconds(6);

        esperRunner.stop();



        eps.destroy();
    }
}
