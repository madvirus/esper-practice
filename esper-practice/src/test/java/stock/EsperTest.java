package stock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

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

		EPStatement eps = epService.getEPAdministrator().createEPL(
				"select * from ProcessEvent(state=EVENT_STATE)");

		CompletedOrderSubscriber exampleSelect = new CompletedOrderSubscriber();
		eps.setSubscriber(exampleSelect);

		EPStatement eps2 = epService.getEPAdministrator().createEPL(
				"select avg(totalPrice) " +
						"from ProcessEvent(state=2).win:time(3 sec) " +
						"output snapshot every 3 seconds");
		eps2.setSubscriber(new Object() {
			public void update(Double avgPrice) {
				System.out.println(new Date() + "Average orders for last 3 seconds: " + avgPrice);
			}
		});
	}

	@Test
	public void test() {
		sleep(2000);
		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 1, 5000, 1));
		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 2, 10000, 1));
		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 3, 15000, 1));

		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 1, 5000, 2));
		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 2, 10000, 2));

		// epService.getEPRuntime().setVariableValue("EVENT_STATE", 1);
		sleep(5000);

		epService.getEPRuntime().sendEvent(new ProcessEvent("PurchaseOrder", 1L, 3, 15000, 2));

		sleep(8000);
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
		}

		assertThat(epService.getEPRuntime().getNumEventsEvaluated(), equalTo(6L));
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
			System.out.printf(new Date() + "*** New Event Arrived, prcName=%s, procInstId=%d\n",
					event.getProcessName(), event.getProcessInstanceId());
		}
	}
}
