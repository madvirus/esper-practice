package esper;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class StockFinderTest {

    private final StockFinder stockFinder = new StockFinder();

    private StockTick lastFound = null;
    private int lastSurgedStockDiff = 0;

    private StockFinderListener listener = new StockFinderListener() {
        @Override
        public void found(StockTick stockTick) {
            lastFound = stockTick;
        }

        @Override
        public void foundSurgedStock(StockTick tick1, StockTick tick2) {
            lastSurgedStockDiff = tick2.getCost() - tick1.getCost();
        }
    };

    @Before
    public void setup() {
        stockFinder.setup();
        stockFinder.setStockFoundListener(listener);
    }

//    @Test
    public void shouldFound() {
        StockTick tick1 = new StockTick("name", "code", 109, 9, 9.0);
        stockFinder.sendStockTick(tick1);
        assertThat(lastFound, nullValue());

        StockTick tick2 = new StockTick("name", "code", 110, 10, 10.0);
        stockFinder.sendStockTick(tick2);
        assertThat(lastFound, equalTo(tick2));
    }

    @Test
    public void foundSurgedStock() {
        final int[] stocks = new int[]{
                100, 300,
                109, 301,
                104, 302,
                112, 305,
                113, 324,
                120, 323,
                117, 330
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int idx = 0;

            @Override
            public void run() {
                System.out.printf("T%02d: %tS.%<tL\n" , idx, new Date());
                if (idx >= stocks.length) {
                    idx++;
                    return;
                }

                if (idx % 2 == 0) {
                    System.out.printf("GEN: %tS.%<tL %s=%d \n", new Date(), "code1", stocks[idx]);
                    stockFinder.sendStockTick(new StockTick("name1", "code1", stocks[idx], 0, 0));
                } else {
                    System.out.printf("GEN: %tS.%<tL %s=%d \n", new Date(), "code2", stocks[idx]);
                    stockFinder.sendStockTick(new StockTick("name2", "code2", stocks[idx], 0, 0));
                }
                idx++;
            }
        }, 500, 1000);
//        stockFinder.sendStockTick(new StockTick("name1", "code1", stocks[0], 0, 0));
        sleep(16, TimeUnit.SECONDS);
        timer.cancel();
    }

    private void sleep(long i, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(i));
        } catch (InterruptedException e) {
        }
    }

}
