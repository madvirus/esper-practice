package stock;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class EPLOutputTest extends EPLTestBase {


    @Override
    protected EPServiceProvider initEPService() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        //config.addVariable("EVENT_STATE", Integer.class, 2);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);

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
                            elapsedTime(),
                            "",//eb.get("code"),
                            eb.get("avg")
                    );
                }
            }
        });
        return epService;
    }

    @Test
    public void test() {
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
        sendEvent(stockTick);
    }
}
