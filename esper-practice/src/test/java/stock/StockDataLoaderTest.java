package stock;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class StockDataLoaderTest {

    private final StockDataLoader stockDataLoader = new StockDataLoader();

    @Test
    public void readKospi() {
        List<StockTick> stockTickList = stockDataLoader.loadKospi();
        assertThat(stockTickList.size(), equalTo(885));
    }

    @Test
    public void readKosdaq() {
        List<StockTick> stockTickList = stockDataLoader.loadKosdaq();
        assertThat(stockTickList.size(), greaterThan(2200));
    }

}
