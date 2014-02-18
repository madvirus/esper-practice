package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLPatternTest {
    private EsperRunner esperRunner;
    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("List", MemberList.class);
        config.addEventType("Detail", MemberDetail.class);
        config.addEventType("ProductView", ProductView.class);

        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

    @Test
    public void a() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select L, D from pattern @SuppressOverlappingMatches ["+
                        "every(L=List -> D=Detail)"+
                        "]"+
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                System.out.printf("UPD\t%5.2f\t%s\t%s\n",
                        esperRunner.elapsedTime(),
                        eb.get("L"),
                        eb.get("D"));
                }

            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new MemberList("L1", "bkchoi")));
        seList.add(new ScheduledEvent(500, new ProductView("V1", 1L, "user1")));
        //seList.add(new ScheduledEvent(500, new MemberList("L2", "bkchoi")));
        seList.add(new ScheduledEvent(1000, new MemberDetail("D1", "bkchoi")));
        seList.add(new ScheduledEvent(1500, new MemberDetail("D2", "bkchoi2")));
        seList.add(new ScheduledEvent(2000, new MemberList("L3", "bkchoi2")));
        seList.add(new ScheduledEvent(2500, new MemberDetail("D3", "bkchoi")));

        //seList.add(new ScheduledEvent(0, new MemberDetail("D0", "bkchoi")));
//        seList.add(new ScheduledEvent(500, new MemberDetail("D1", "bkchoi")));
//        seList.add(new ScheduledEvent(1000, new MemberList("L2", "bkchoi")));
//        seList.add(new ScheduledEvent(1500, new MemberList("L3", "bkchoi")));
//        seList.add(new ScheduledEvent(2000, new MemberDetail("D3", "bkchoi")));

        esperRunner.startSendingAndSleepAndStop(seList, 3);

        eps.destroy();

    }
}
