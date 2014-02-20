package esper;

import com.espertech.esper.client.*;

import java.util.Date;

public class StockFinder {
    private EPServiceProvider epService;
    private EPStatement eps;
    private StockFinderListener listener;

    public EPStatement getEps() {
        return eps;
    }

    public void setStockFoundListener(StockFinderListener listener) {
        this.listener = listener;
    }

    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        epService = EPServiceProviderManager.getProvider("StockTick", config);

        eps = epService.getEPAdministrator().createEPL(
                "select * from StockTick t where t.rate >= 10");
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                StockTick stockTick = (StockTick) newEvents[0].getUnderlying();
                if (listener != null) listener.found(stockTick);
            }
        });

        eps = epService.getEPAdministrator().createEPL(
                "select first(*) as tick1, last(*) as tick2 from "+
                        "StockTick.win:time(5 seconds) " +
                        "group by code " +
                        "having first(*) != last(*) and (last(cost) - first(cost)) / first(cost) > 0.05" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                StockTick tick1 = (StockTick) newEvents[0].get("tick1");
                StockTick tick2 = (StockTick) newEvents[0].get("tick2");
                System.out.printf("EPL: %tS.%<tL [%s=%d - %s=%d] %d \n",
                        new Date(), tick1.getCode(), tick1.getCost(), tick2.getCode(), tick2.getCost(), newEvents.length);
                if (listener != null) listener.foundSurgedStock(tick1, tick2);
            }
        });
    }

    public void sendStockTick(StockTick tick) {
        epService.getEPRuntime().sendEvent(tick);
    }
}