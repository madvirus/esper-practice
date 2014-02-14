package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLJoinTest {
    private EsperRunner esperRunner;
    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("ProductView", ProductView.class);
        config.addEventType("ProductOrder", ProductOrder.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

    @Test
    public void leftOuterJoin() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select rstream v, o " +
                        "from ProductView.win:time(2 sec) as v " +
                        "left outer join ProductOrder.win:time(2 sec) as o " +
                        "on v.productId = o.productId and v.userId = o.userId " +
                        "where o is null" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                if (newEvents != null)
                    for (EventBean eb : newEvents) {
                        ProductView pv = (ProductView) eb.get("v");
                        ProductOrder po = (ProductOrder) eb.get("o");
                        System.out.printf("UPD\t%5.2f\t%s\t%s\n", esperRunner.elapsedTime(),
                                pv == null ? "null" : pv, po == null ? "null" : po);
                    }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new ProductView("V1", 1L, "user1")));
        seList.add(new ScheduledEvent(500, new ProductView("V2", 1L, "user1")));
        seList.add(new ScheduledEvent(1000, new ProductOrder("O1", 1L, "user1")));
        seList.add(new ScheduledEvent(1500, new ProductView("V3", 2L, "user2")));
        //seList.add(new ScheduledEvent(2300, new ProductView("V3", 2L, "user2")));
        seList.add(new ScheduledEvent(3800, new ProductOrder("O3", 3L, "user3")));

        esperRunner.startSendingAndSleepAndStop(seList, 5);

        eps.destroy();
    }
}
