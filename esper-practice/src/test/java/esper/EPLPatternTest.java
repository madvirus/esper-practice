package esper;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import org.junit.Before;

public class EPLPatternTest {
    private EsperRunner esperRunner;
    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

}
