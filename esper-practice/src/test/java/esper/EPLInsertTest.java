package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLInsertTest {

    private EsperRunner esperRunner;

    @Before
    public void setUp() {
        Configuration config = new Configuration();
        config.addEventType("AccessLog", AccessLog.class);
        config.addEventType("SlowResponse", SlowResponse.class);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);

        esperRunner = new EsperRunner(epService);

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "insert into SlowResponse (url, responseTime)" +
                        "select url, responseTime from AccessLog where responseTime > 500"
        );

        EPStatement eps2 = epService.getEPAdministrator().createEPL(
                "select count(*) as count from SlowResponse.win:time(2 sec) s having count(*) > 3"
        );
        eps2.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                    int count = ((Number) newEvents[0].get("count")).intValue();
                    System.out.printf("UPD\t%5.2f\t%d\n", esperRunner.elapsedTime(),
                            count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void test() {
        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new AccessLog("http://a.com/a", 1000L)));
        seList.add(new ScheduledEvent(500, new AccessLog("http://a.com/a", 2000L)));
        seList.add(new ScheduledEvent(600, new AccessLog("http://a.com/a", 700L)));
        seList.add(new ScheduledEvent(1300, new AccessLog("http://a.com/a", 500L)));
        seList.add(new ScheduledEvent(1700, new AccessLog("http://a.com/a", 900L)));
        seList.add(new ScheduledEvent(2300, new AccessLog("http://a.com/a", 600L)));
        seList.add(new ScheduledEvent(2400, new AccessLog("http://a.com/a", 1200L)));

        esperRunner.startSendingAndSleepAndStop(seList, 5);
    }

}
