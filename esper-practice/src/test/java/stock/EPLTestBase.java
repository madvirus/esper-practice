package stock;

import com.espertech.esper.client.EPServiceProvider;
import org.junit.Before;

public abstract class EPLTestBase {
    private EPServiceProvider epService;
    private long startTime;

    @Before
    public void setUp() {
        epService = initEPService();
        startTime = System.currentTimeMillis() - 1000;
    }

    protected abstract EPServiceProvider initEPService();

    protected double elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    protected void sendEvent(Object event) {
        System.out.printf("GEN\t%5.2f\t%s\n", elapsedTime(), event);
        epService.getEPRuntime().sendEvent(event);
    }

}
