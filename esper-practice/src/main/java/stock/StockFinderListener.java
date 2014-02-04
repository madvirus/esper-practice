package stock;

public interface StockFinderListener {
    void found(StockTick stockTick);

    void foundSurgedStock(StockTick tick1, StockTick tick2);
}
