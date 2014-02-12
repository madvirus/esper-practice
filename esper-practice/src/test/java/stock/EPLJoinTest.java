package stock;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class EPLJoinTest {

    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("ProductView", ProductView.class);
        config.addEventType("ProductOrder", ProductOrder.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select /* rstream */ v, o "+
                        "from ProductView.win:time(2 sec) as v "+
                        "left outer join ProductOrder.win:time(2 sec) as o " +
                        "on v.productId = o.productId and v.userId = o.userId "+
                        "where o is null" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                if (newEvents != null)
                    printEvents(newEvents);
                if (oldEvents != null)
                    printEvents(oldEvents);
            }

            private void printEvents(EventBean[] newEvents) {
                try {
                    for (EventBean eb : newEvents) {
                        ProductView pv = (ProductView) eb.get("v");
                        ProductOrder po = (ProductOrder) eb.get("o");
                        System.out.printf("UPD\t%5.2f\t%s\t%s\n",
                                elapsedTime(),
                                pv == null ? "null" : pv,
                                po == null ? "null" : po);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private double elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    private long startTime;

    @Test
    public void test() {
        startTime = System.currentTimeMillis() - 1000;
        sendProductViewEvent("V1", 1L, "user1");
        SleepUtil.sleepMillis(500);
        sendProductViewEvent("V2", 1L, "user1");
        SleepUtil.sleepMillis(500);
        sendProductOrderEvent("O1", 1L, "user1");
        SleepUtil.sleepMillis(500);
        sendProductViewEvent("V3", 2L, "user2");
        SleepUtil.sleepMillis(800);
        sendProductOrderEvent("O2", 2L, "user2");
        SleepUtil.sleepMillis(1500);
        sendProductOrderEvent("O3", 3L, "user3");
    }

    private void sendProductViewEvent(String name, Long id, String userId) {
        System.out.printf("GEN\t%5.2f\t%S\t%d\t%s\n",
                elapsedTime(), name, id, userId);
        epService.getEPRuntime().sendEvent(new ProductView(name, id, userId));
    }

    private void sendProductOrderEvent(String name, Long id, String userId) {
        System.out.printf("GEN\t%5.2f\t%s\t%d\t%s\n",
                elapsedTime(), name, id, userId);
        epService.getEPRuntime().sendEvent(new ProductOrder(name, id, userId));
    }


}
