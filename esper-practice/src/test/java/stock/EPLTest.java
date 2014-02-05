package stock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select s as tick, max(cost) as maxCost from StockTick.win:length(5) s where cost > 2000 group by code");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                    StockTick tick = (StockTick) newEvents[0].get("tick");
                    int max = (Integer) newEvents[0].get("maxCost");
                    System.out.printf("UPD: %tS.%<tL %s:%d\n", new Date(), tick.getCode(), max);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void test() {
        sendStockTick(new StockTick("name", "code1", 1000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 2000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 3000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 4000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 5000, 0, 0.0));
        SleepUtil.sleepSeconds(3);
        sendStockTick(new StockTick("name", "code1", 6000, 0, 0.0));
        SleepUtil.sleepOneSecond();
        sendStockTick(new StockTick("name", "code1", 7000, 0, 0.0));
        SleepUtil.sleepOneSecond();
    }

    private void sendStockTick(StockTick stockTick) {
        System.out.printf("GEN: %tS.%<tL %s:%d\n", new Date(), stockTick.getCode(), stockTick.getCost());
        epService.getEPRuntime().sendEvent(stockTick);
    }

    private void sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }

}
