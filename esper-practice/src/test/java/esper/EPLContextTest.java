package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLContextTest {

    private EsperRunner esperRunner;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);

        esperRunner = new EsperRunner(epService);

        epService.getEPAdministrator().createEPL("create context CodeSegment partition by code from StockTick");

        final EPStatement eps = epService.getEPAdministrator().createEPL(
                "context CodeSegment " +
                "select code, avg(cost) as avg from StockTick.win:time(3 sec) ");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\t%f\n",
                            esperRunner.elapsedTime(),
                            eb.get("code"),
                            eb.get("avg")
                    );
                }
            }
        });
    }

    @Test
    public void test() {
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

        esperRunner.startSendingAndSleepAndStop(seList, 14);

    }

}
