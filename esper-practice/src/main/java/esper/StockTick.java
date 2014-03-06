package esper;

public class StockTick {
    private String name;
    private String code;
    private int cost;
    private int fluctuation;
    private double rate;
    private long accessTime;

    public StockTick(String name, String code, int cost, int fluctuation, double rate) {
        this.name = name;
        this.code = code;
        this.cost = cost;
        this.fluctuation = fluctuation;
        this.rate = rate;
    }

    public StockTick(String name, String code, int cost, int fluctuation, double rate, long accessTime) {
        this.name = name;
        this.code = code;
        this.cost = cost;
        this.fluctuation = fluctuation;
        this.rate = rate;
        this.accessTime = accessTime;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getCost() {
        return cost;
    }

    public int getFluctuation() {
        return fluctuation;
    }

    public double getRate() {
        return rate;
    }

    public long getAccessTime() {
        return accessTime;
    }

    @Override
    public String toString() {
        return "StockTick[" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", cost=" + cost +
                ", fluctuation=" + fluctuation +
                ", rate=" + rate +
                ", accessTime=" + accessTime +
                ']';
    }
}