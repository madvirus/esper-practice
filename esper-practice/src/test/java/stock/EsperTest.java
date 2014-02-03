package stock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class EsperTest {

	private EPServiceProvider epService;

	@Before
	public void setup() {
		Configuration config = new Configuration();
		config.addEventType("ProcessEvent", ProcessEvent.class);
		config.addVariable("EVENT_STATE", Integer.class, 2);
		epService = EPServiceProviderManager.getProvider("EsperTest", config);

//		EPStatement eps = epService.getEPAdministrator().createEPL(
//				"select * from ProcessEvent(state=EVENT_STATE)");
//
//		CompletedOrderSubscriber exampleSelect = new CompletedOrderSubscriber();
//		eps.setSubscriber(exampleSelect);

		EPStatement eps2 = epService.getEPAdministrator().createEPL(
				"select avg(totalPrice) " +
						"from ProcessEvent(state=2).win:time(3 sec) " +
						"output every 4 seconds");
		eps2.setSubscriber(new Object() {
			@SuppressWarnings("unused")
			public void update(Double avgPrice) {
				System.out.println(now() + ": Average orders for last 3 seconds: " + avgPrice);
			}
		});
	}

	@Test
	public void test() {
		final List<ProcessEvent> events = new ArrayList<>();
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 1000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 2000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 3000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 4000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 5000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 6000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 7000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 8000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 9000, 2));
		events.add(new ProcessEvent("PurchaseOrder", 1L, 1, 10000, 2));

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				if (i < 10) {
					System.out.printf(now() + " *** New Event Arrived, prcName=%s, price=%d\n",
							events.get(i).getProcessName(), events.get(i).getTotalPrice());
					epService.getEPRuntime().sendEvent(events.get(i));
				}
				i++;
			}
		};
		timer.scheduleAtFixedRate(task, 300, 1000);

		sleep(12000);

		timer.cancel();

		assertThat(epService.getEPRuntime().getNumEventsEvaluated(), equalTo((long) events.size()));
	}

	private void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
		}
	}

	public static class ProcessEvent {
		private String processName;
		private long processVersion;
		private long processInstanceId;
		private long totalPrice;
		private int state;

		public ProcessEvent(String processName, long processVersion, long processInstanceId, long totalPrice, int state) {
			super();
			this.processName = processName;
			this.processVersion = processVersion;
			this.processInstanceId = processInstanceId;
			this.totalPrice = totalPrice;
			this.state = state;
		}

		public String getProcessName() {
			return processName;
		}

		public long getProcessVersion() {
			return processVersion;
		}

		public long getProcessInstanceId() {
			return processInstanceId;
		}

		public long getTotalPrice() {
			return totalPrice;
		}

		public int getState() {
			return state;
		}

	}

	public static class CompletedOrderSubscriber {
		public void update(ProcessEvent event) {
			System.out.printf(now() + " *** New Event Arrived, prcName=%s, price=%d\n",
					event.getProcessName(), event.getTotalPrice());
		}
	}

	private static String now() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
}
