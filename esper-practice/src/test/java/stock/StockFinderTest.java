package stock;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
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

    @Test
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
        // time code1  code2
        // 1s   100    10
        // 2s   109    9
        // 3s   104
        // 4s   112
        // 5s   113
        // 6s   114
        // 7s   117

        final int[][] stocks = new int[][]{
                {100, 10},
                {109, 9},
                {104, 9},
                {112, 9},
                {113, 9},
                {114, 9},
                {117, 9},
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int idx = 0;

            @Override
            public void run() {
                System.out.println("[TICK " + idx + "]");
                if (idx >= stocks.length * 2) {
                    idx++;
                    return;
                }

                int[] stockIdx = stocks[idx / 2];
                if (idx % 2 == 0) {
                    System.out.printf("GEN: %tM:%<tS.%<tL %s=%d \n", new Date(), "code1", stockIdx[0]);
                    stockFinder.sendStockTick(new StockTick("name1", "code1", stockIdx[0], 0, 0));
                } else {
                    System.out.printf("GEN: %tM:%<tS.%<tL %s=%d \n", new Date(), "code2", stockIdx[1]);
                    stockFinder.sendStockTick(new StockTick("name2", "code2", stockIdx[1], 0, 0));
                }
                idx++;
            }
        }, 300, 1000);

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
