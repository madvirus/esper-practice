package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class EPLSubqueryTest {

    private EsperRunner esperRunner;

    @Before
    public void setUp() {
        Configuration config = new Configuration();
        config.addEventType("SlowResponse", SlowResponse.class);
        config.addEventType("Tps", Tps.class);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);

        esperRunner = new EsperRunner(epService);

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select count(*) as count, (select value from Tps.win:time(5 sec) ) as maxTps " +
                        "from SlowResponse.win:time(3 sec) s " +
                        "having count(*) > 3 "
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                    Number count = (Number) newEvents[0].get("count");
                    Number maxTps = (Number) newEvents[0].get("maxTps");
                    System.out.printf("UPD\t%5.2f\t%d\t%d \n", esperRunner.elapsedTime(), count, maxTps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        EPStatement eps2 = epService.getEPAdministrator().createEPL(
                "select * from SlowResponse.win:time(4 sec) s " +
                        "where s.responseTime > (select avg(responseTime) from SlowResponse.win:time(2 sec))" +
                        ""
        );
        eps2.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                SlowResponse event = (SlowResponse) newEvents[0].getUnderlying();
                System.out.printf("SLO\t%5.2f\t%s\n", esperRunner.elapsedTime(), event);
            }
        });
    }

    @Test
    public void test() {
        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new SlowResponse("http://a.com/a", 1000L)));
        seList.add(new ScheduledEvent(500, new SlowResponse("http://a.com/a", 2000L)));
        seList.add(new ScheduledEvent(600, new SlowResponse("http://a.com/a", 700L)));
        seList.add(new ScheduledEvent(1300, new SlowResponse("http://a.com/a", 500L)));
        seList.add(new ScheduledEvent(1700, new SlowResponse("http://a.com/a", 900L)));
        seList.add(new ScheduledEvent(2300, new SlowResponse("http://a.com/a", 600L)));
        seList.add(new ScheduledEvent(2400, new SlowResponse("http://a.com/a", 1200L)));

        esperRunner.startSendingAndSleepAndStop(seList, 4);
    }

}
