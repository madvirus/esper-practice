package stock;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class StockDataLoaderTest {

    private final StockDataLoader stockDataLoader = new StockDataLoader();

    @Test
	public void readKospi() {
		List<Stock> stockList = stockDataLoader.loadKospi();
		assertThat(stockList.size(), equalTo(885));
	}

    @Test
	public void readKosdaq() {
		List<Stock> stockList = stockDataLoader.loadKosdaq();
		assertThat(stockList.size(), equalTo(2226));
	}

}
