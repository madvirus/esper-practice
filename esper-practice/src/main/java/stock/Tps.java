package stock;

public class Tps {
    private int value;

    public Tps(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Tps[" +
                "value=" + value +
                ']';
    }
}
