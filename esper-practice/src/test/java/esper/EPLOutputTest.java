package esper;

import com.espertech.esper.client.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EPLOutputTest {

    private EsperRunner esperRunner;
    private EPServiceProvider epService;

    @Before
    public void setup() {
        Configuration config = new Configuration();
        config.addEventType("StockTick", StockTick.class);
        epService = EPServiceProviderManager.getProvider("EPLTest", config);
        esperRunner = new EsperRunner(epService);
    }

    @Test
    @Ignore
    public void output_all_no_group_by() {
        runOutputNoGroup("select avg(cost) as avg from StockTick output all every 2 sec");
    }

    @Ignore
    @Test
    public void output_first_no_group_by() {
        runOutputNoGroup("select avg(cost) as avg from StockTick output first every 2 sec");
    }

    @Ignore
    @Test
    public void output_last_no_group_by() {
        runOutputNoGroup("select avg(cost) as avg from StockTick output last every 2 sec");
    }

    @Ignore
    @Test
    public void output_snapshot_no_group_by() {
        runOutputNoGroup("select avg(cost) as avg from StockTick output snapshot every 2 sec");
    }

    @Ignore
    @Test
    public void output_all_group_by() {
        runOutputGroup("select code, avg(cost) as avg from StockTick group by code output all every 2 sec");
    }

    @Ignore
    @Test
    public void output_first_group_by() {
        runOutputGroup("select code, avg(cost) as avg from StockTick group by code output first every 2 sec");
    }

    @Ignore
    @Test
    public void output_last_group_by() {
        runOutputGroup("select code, avg(cost) as avg from StockTick group by code output last every 2 sec");
    }

    @Ignore
    @Test
    public void output_noOption_group_by() {
        runOutputGroup("select code, avg(cost) as avg from StockTick group by code output every 2 sec");
    }

    private void runOutputGroup(String epl) {
        UpdateListener listener = new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%s\t%f\n",
                            esperRunner.elapsedTime(),
                            eb.get("code"),
                            eb.get("avg")
                    );
                }
            }
        };
        runEplWithListener(epl, listener);
    }

    private void runEplWithListener(String epl, UpdateListener listener) {
        EPStatement eps = epService.getEPAdministrator().createEPL(epl);
        eps.addListener(listener);

        List<ScheduledEvent> seList = new ArrayList<>();
        seList.add(new ScheduledEvent(0, new StockTick("name", "code1", 1000, 0, 0.0)));
        seList.add(new ScheduledEvent(0, new StockTick("name", "code2", 100, 0, 0.0)));
        seList.add(new ScheduledEvent(500, new StockTick("name", "code1", 2000, 0, 0.0)));
        seList.add(new ScheduledEvent(1500, new StockTick("name", "code1", 3000, 0, 0.0)));
        seList.add(new ScheduledEvent(1500, new StockTick("name", "code2", 200, 0, 0.0)));
        seList.add(new ScheduledEvent(2500, new StockTick("name", "code1", 4000, 0, 0.0)));
        seList.add(new ScheduledEvent(3500, new StockTick("name", "code1", 5000, 0, 0.0)));
        seList.add(new ScheduledEvent(6500, new StockTick("name", "code1", 6000, 0, 0.0)));
        seList.add(new ScheduledEvent(6500, new StockTick("name", "code2", 300, 0, 0.0)));
        seList.add(new ScheduledEvent(7500, new StockTick("name", "code1", 7000, 0, 0.0)));
        esperRunner.startSendingAndSleepAndStop(seList, 14);
        eps.destroy();
    }

    private void runOutputNoGroup(String outputEplNoGroupBy) {
        runEplWithListener(outputEplNoGroupBy, new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                for (EventBean eb : newEvents) {
                    System.out.printf("UPD\t%5.2f\t%f\n",
                            esperRunner.elapsedTime(),
                            eb.get("avg")
                    );
                }
            }
        });
    }

    @Ignore
    @Test
    public void timebatch_snapshot() {
        runOutputNoGroup("select avg(cost) as avg from StockTick.win:time_batch(3 sec) output snapshot every 1.5 sec");
    }

    @Test
    public void time_snapshot() {
        runOutputNoGroup("select avg(cost) as avg from StockTick.win:time(3 sec) output snapshot every 1.5 sec");
    }

}
