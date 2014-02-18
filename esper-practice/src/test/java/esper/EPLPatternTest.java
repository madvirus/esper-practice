package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
        config.addEventType("IssuedTicket", IssuedTicket.class);
        config.addEventType("Entering", Entering.class);
        config.addEventType("UseTicketCoupon", UseTicketCoupon.class);
        config.addEventType("SlowResponse", SlowResponse.class);
        config.addEventType("NormalResponse", NormalResponse.class);
        config.addEventType("OvertimeResponse", OvertimeResponse.class);

        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

    @Test
    @Ignore
    public void ticketPatternByUsingNot() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select i from pattern [" +
                        "every i=IssuedTicket -> (Entering(ticketId = i.id) and not UseTicketCoupon(couponId = i.couponId))" +
                        "]" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\n",
                            esperRunner.elapsedTime(),
                            eb.get("i"));
                }

            }
        });
        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new IssuedTicket("TICKET1", "COUPON1")));
        seList.add(new ScheduledEvent(300, new IssuedTicket("TICKET2", "COUPON2")));
        seList.add(new ScheduledEvent(1200, new UseTicketCoupon("COUPON1")));
        seList.add(new ScheduledEvent(2000, new Entering("TICKET2")));
        seList.add(new ScheduledEvent(2500, new Entering("TICKET1")));

        esperRunner.startSendingAndSleepAndStop(seList, 4);

        eps.destroy();

    }

    @Test
    @Ignore
    public void followedBy() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select L, D from pattern @SuppressOverlappingMatches [" +
                        "every(L=List -> D=Detail)" +
                        "]" +
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

        esperRunner.startSendingAndSleepAndStop(seList, 3);

        eps.destroy();
    }

    @Test
    @Ignore
    public void repeat() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select s, o from pattern [" +
                        "every [2] ((s=SlowResponse or o=OvertimeResponse) and not NormalResponse)" +
                        "]" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    SlowResponse[] responses = (SlowResponse[]) eb.get("s");
                    OvertimeResponse[] responses2 = (OvertimeResponse[]) eb.get("o");
                    System.out.printf("UPD\t%5.2f\t%s\t%s\n",
                            esperRunner.elapsedTime(),
                            Arrays.toString(responses),
                            Arrays.toString(responses2));
                }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new SlowResponse("/a", 3000)));
        seList.add(new ScheduledEvent(300, new OvertimeResponse("/a")));
        seList.add(new ScheduledEvent(500, new SlowResponse("/a", 3000)));
        seList.add(new ScheduledEvent(700, new NormalResponse("/a", 1000)));
        seList.add(new ScheduledEvent(1000, new SlowResponse("/a", 3000)));
        seList.add(new ScheduledEvent(1500, new SlowResponse("/a", 3000)));
        esperRunner.startSendingAndSleepAndStop(seList, 3);

        eps.destroy();
    }

    @Test
    @Ignore
    public void until() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select s from pattern [" +
                        "every ([3:] s=SlowResponse until NormalResponse)" +
                        "]" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    SlowResponse[] responses = (SlowResponse[]) eb.get("s");
                    OvertimeResponse[] responses2 = null; // (OvertimeResponse[]) eb.get("o");
                    System.out.printf("UPD\t%5.2f\t%s\t%s\n",
                            esperRunner.elapsedTime(),
                            Arrays.toString(responses),
                            Arrays.toString(responses2));
                }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new SlowResponse("S1", 3000)));
        seList.add(new ScheduledEvent(300, new OvertimeResponse("O1")));
        seList.add(new ScheduledEvent(500, new SlowResponse("S2", 3000)));
        seList.add(new ScheduledEvent(700, new NormalResponse("N1", 1000)));
        seList.add(new ScheduledEvent(1000, new SlowResponse("S3", 3000)));
        seList.add(new ScheduledEvent(1500, new SlowResponse("S4", 3000)));
        seList.add(new ScheduledEvent(1700, new SlowResponse("S5", 3000)));
        seList.add(new ScheduledEvent(1900, new NormalResponse("N2", 1000)));
        esperRunner.startSendingAndSleepAndStop(seList, 3);

        eps.destroy();
    }

    @Test
    public void timeGuard() {
        EPStatement eps = epService.getEPAdministrator().createEPL(
                "select s from pattern [" +
                        "every ((s=SlowResponse -> SlowResponse) where timer:within(2 sec))" +
                        "]" +
                        ""
        );
        eps.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
//                    SlowResponse[] responses = (SlowResponse[]) eb.get("s");
//                    OvertimeResponse[] responses2 = null; // (OvertimeResponse[]) eb.get("o");
                    System.out.printf("UPD\t%5.2f\t%s\n",
                            esperRunner.elapsedTime(),
                            eb.get("s"));
                }
            }
        });

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new SlowResponse("S1", 3000)));
        seList.add(new ScheduledEvent(1000, new SlowResponse("S2", 3000)));
        seList.add(new ScheduledEvent(1500, new SlowResponse("S3", 3000)));
        seList.add(new ScheduledEvent(4000, new SlowResponse("S4", 3000)));
        seList.add(new ScheduledEvent(5000, new SlowResponse("S5", 3000)));
        esperRunner.startSendingAndSleepAndStop(seList, 6);

        eps.destroy();
    }
}
