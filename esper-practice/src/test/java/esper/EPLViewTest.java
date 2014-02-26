package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLViewTest {

    private EsperRunner esperRunner;
    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("AccessLog", AccessLog.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

    @Test
    public void win_ext_timed() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select avg(responseTime) as avg from AccessLog.win:ext_timed(accessTime, 3 sec)");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%f\n",
                            esperRunner.elapsedTime(),
                            eb.get("avg")
                    );
                }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new StockTick("name", "code1", 1000, 0, 0.0)));

        esperRunner.startSendingAndSleepAndStop(seList, 14);

        eps.destroy();
    }
}
