package stock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.*;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

public class EPLTest {

    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        //config.addVariable("EVENT_STATE", Integer.class, 2);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);

        final EPStatement eps = epService.getEPAdministrator().createEPL(
                "select code, avg(cost) as avg from StockTick.win:time(3 sec) group by code");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%tS.%<tL\t%s\t%f\n",
                            new Date(),
                            eb.get("code"),
                            eb.get("avg")
                    );
                }
            }
        });
    }

    @Test
    public void test() {
        sendStockTick(new StockTick("name", "code1", 1000, 0, 0.0));
        sendStockTick(new StockTick("name", "code2", 100, 0, 0.0));
        SleepUtil.sleepMillis(500);
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
    }

    private void sendStockTick(StockTick stockTick) {
        System.out.printf("GEN\t%tS.%<tL\t%s\t%d\n", new Date(), stockTick.getCode(), stockTick.getCost());
        epService.getEPRuntime().sendEvent(stockTick);
    }

    private void sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }

}
