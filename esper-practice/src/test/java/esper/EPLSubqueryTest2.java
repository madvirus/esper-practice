package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLSubqueryTest2 {

    private EsperRunner esperRunner;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("ProductView", ProductView.class);
        config.addEventType("ProductOrder", ProductOrder.class);

        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select rstream v " +
                        "from ProductView.win:time(2 sec) as v " +
                        "where not exists (select * from ProductOrder.win:time(2 sec) as o where v.productId = o.productId and v.userId = o.userId)" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                    for (EventBean eb : newEvents) {
                        ProductView pv = (ProductView) eb.get("v");
                        System.out.printf("UPD\t%5.2f\t%s\n",
                                esperRunner.elapsedTime(),
                                pv);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void test() {
        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new ProductView("V1", 1L, "user1")));
        seList.add(new ScheduledEvent(500, new ProductView("V2", 1L, "user1")));
        seList.add(new ScheduledEvent(1000, new ProductOrder("O1", 1L, "user1")));
        seList.add(new ScheduledEvent(1500, new ProductView("V3", 2L, "user2")));
        //seList.add(new ScheduledEvent(2300, new ProductOrder("O2", 2L, "user2")));
        seList.add(new ScheduledEvent(3800, new ProductView("O3", 3L, "user3")));

        esperRunner.startSendingAndSleepAndStop(seList, 4);
    }

}
