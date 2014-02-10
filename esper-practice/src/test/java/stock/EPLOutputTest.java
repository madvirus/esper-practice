package stock;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class EPLOutputTest {

    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        //config.addVariable("EVENT_STATE", Integer.class, 2);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);

        final EPStatement eps = epService.getEPAdministrator().createEPL(
                "select avg(cost) as avg from StockTick.win:time_batch(3 sec) output after 4 sec snapshot every 1.5 sec"
//                "select code, avg(cost) as avg from StockTick group by code output first every 2 sec"
                //"select avg(cost) as avg from StockTick output first every 2 sec"
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\t%f\n",
                            (System.currentTimeMillis() - startTime) / 1000.,
                            "",//eb.get("code"),
                            eb.get("avg")
                    );
                }
            }
        });
    }

    private long startTime;
    @Test
    public void test() {
        startTime = System.currentTimeMillis() - 1000;
        sendStockTick(new StockTick("name", "code1", 1000, 0, 0.0));
        SleepUtil.sleepMillis(500);
        sendStockTick(new StockTick("name", "code2", 100, 0, 0.0));
//        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 2000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 3000, 0, 0.0));
        sendStockTick(new StockTick("name", "code2", 200, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 4000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 5000, 0, 0.0));
        SleepUtil.sleepSeconds(3);
        sendStockTick(new StockTick("name", "code1", 6000, 0, 0.0));
        sendStockTick(new StockTick("name", "code2", 300, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 7000, 0, 0.0));
        SleepUtil.sleepOneSecond();

        SleepUtil.sleepSeconds(5);
    }

    private void sendStockTick(StockTick stockTick) {
        System.out.printf("GEN\t%5.2f\t%s\t%d\n",
                (System.currentTimeMillis() - startTime) / 1000., stockTick.getCode(), stockTick.getCost());
        epService.getEPRuntime().sendEvent(stockTick);
    }

    private void sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }

}
