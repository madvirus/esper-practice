package stock;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

public class EPLInsertTest {

    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("AccessLog", AccessLog.class);
        config.addEventType("SlowResponse", SlowResponse.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "insert into SlowResponse (a)" +
                        "select a from AccessLog a where responseTime > 500"
        );

        EPStatement eps2 = epService.getEPAdministrator().createEPL(
                "select count(*) as count from SlowResponse.win:time(2 sec) s having count(*) > 3"
        );
        eps2.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                int count = ((Number)newEvents[0].get("count")).intValue();
                System.out.printf("UPD\t%5.2f\t%d\n", (System.currentTimeMillis() - startTime) / 1000.,
                        count);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private long startTime;
    @Test
    public void test() {
        startTime = System.currentTimeMillis() - 1000;
        sendAccessLogEvent("http://a.com/a", 1000L);
        SleepUtil.sleepMillis(500);
        sendAccessLogEvent("http://a.com/a", 2000L);
        SleepUtil.sleepMillis(100);
        sendAccessLogEvent("http://a.com/a", 700L);

        SleepUtil.sleepMillis(700);
        sendAccessLogEvent("http://a.com/a", 500L);

        SleepUtil.sleepMillis(400);
        sendAccessLogEvent("http://a.com/a", 900L);

        SleepUtil.sleepMillis(600);
        sendAccessLogEvent("http://a.com/a", 600L);
        SleepUtil.sleepMillis(100);
        sendAccessLogEvent("http://a.com/a", 1200L);
        SleepUtil.sleepSeconds(5);
    }

    private void sendAccessLogEvent(String url, long responseTime) {
        System.out.printf("GEN\t%5.2f\t%s\t%d\n",
                (System.currentTimeMillis() - startTime) / 1000., url, responseTime);
        epService.getEPRuntime().getEventSender("AccessLog").sendEvent(new AccessLog(url, responseTime));
        //epService.getEPRuntime().sendEvent(new AccessLog(url, responseTime));
    }


}
