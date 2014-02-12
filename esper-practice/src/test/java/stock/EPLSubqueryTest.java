package stock;

import com.espertech.esper.client.*;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

public class EPLSubqueryTest extends EPLTestBase {

    @Override
    protected EPServiceProvider initEPService() {
        Configuration config = new Configuration();
        config.addEventType("SlowResponse", SlowResponse.class);
        config.addEventType("Tps", Tps.class);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("EPLTest", config);

        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select count(*) as count, (select value from Tps.win:time(5 sec) ) as maxTps " +
                        "from SlowResponse.win:time(3 sec) s " +
                        "having count(*) > 3 "
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                try {
                Number count = (Number) newEvents[0].get("count");
                Number maxTps = (Number) newEvents[0].get("maxTps");
                System.out.printf("UPD\t%5.2f\t%d\t%d \n", elapsedTime(), count, maxTps);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });


        EPStatement eps2 = epService.getEPAdministrator().createEPL(
                "select * from SlowResponse.win:time(4 sec) s " +
                        "where s.responseTime > (select avg(responseTime) from SlowResponse.win:time(2 sec))" +
                        ""
        );
        eps2.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                SlowResponse event = (SlowResponse) newEvents[0].getUnderlying();
                System.out.printf("SLO\t%5.2f\t%s\n", elapsedTime(), event);
            }
        });
        return epService;
    }

    @Test
    public void test() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendEvent(new Tps(200));
            }
        }, 100, 1000);

        sendSlowResponseEvent("http://a.com/a", 1000L);
        SleepUtil.sleepMillis(500);
        sendSlowResponseEvent("http://a.com/a", 2000L);
        SleepUtil.sleepMillis(100);
        sendSlowResponseEvent("http://a.com/a", 700L);

        SleepUtil.sleepMillis(700);
        sendSlowResponseEvent("http://a.com/a", 500L);

        SleepUtil.sleepMillis(400);
        sendSlowResponseEvent("http://a.com/a", 900L);

        SleepUtil.sleepMillis(600);
        sendSlowResponseEvent("http://a.com/a", 600L);
        SleepUtil.sleepMillis(100);
        sendSlowResponseEvent("http://a.com/a", 1200L);
        SleepUtil.sleepSeconds(5);
        timer.cancel();
    }

    private void sendSlowResponseEvent(String url, long responseTime) {
        sendEvent(new SlowResponse(url, responseTime));
    }

}
