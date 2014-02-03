package stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StockDataLoaderTest {

	@Test
	public void read() {
		List<Stock> stockList = loadKospi();
		System.out.println(stockList.size());
	}

	private List<Stock> loadKospi() {
		String jsonData = loadKospiStockJsonDataFromDaumStock();
		List<Stock> stocks = new ArrayList<>(256);
		ObjectMapper m = new ObjectMapper();
		m.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		try {
			JsonNode rootNode = m.readTree(jsonData);
			JsonNode listNode = rootNode.get("list");
			Iterator<JsonNode> listItems = listNode.elements();
			while (listItems.hasNext()) {
				JsonNode listItem = listItems.next();
				JsonNode itemNode = listItem.get("item");
				Iterator<JsonNode> items = itemNode.elements();
				while (items.hasNext()) {
					JsonNode item = items.next();
					stocks.add(createStock(item));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stocks;
	}

	private Stock createStock(JsonNode item) {
		try {
			String code = getNodeValue(item, "code");
			String name = getNodeValue(item, "name");
			DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");
			int cost = decimalFormat.parse(getNodeValue(item, "cost")).intValue();
			String updn = getNodeValue(item, "updn");
			int fluctuation = 0;
			if (updn.startsWith("▲")) {
				fluctuation = decimalFormat.parse(updn.substring(1)).intValue();
			} else if (updn.startsWith("▼")) {
				fluctuation = -decimalFormat.parse(updn.substring(1)).intValue();
			}
			String rate = getNodeValue(item, "rate");
			double ratevalue = 0.0;
			if (!rate.equals("0.00%")) {
				DecimalFormat decimalFormat2 = new DecimalFormat("+#,##0.00%;-#,##0.00%");
				ratevalue = decimalFormat2.parse(rate).doubleValue();
			}
			return new Stock(code, name, cost, fluctuation, ratevalue);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private String getNodeValue(JsonNode item, String nodeName) {
		String value = item.get(nodeName).toString();
		return value.substring(1, value.length() - 1);
	}

	private String loadKospiStockJsonDataFromDaumStock() {
		String jsonData = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
				"http://stock.daum.net/xml/xmlallpanel.daum?stype=P&type=U");
		try {
			HttpResponse response = httpClient.execute(getRequest);
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			StringBuilder builder = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				if (output.trim().equals("var dataset ="))
					continue;
				if (output.trim().equals(";"))
					continue;
				builder.append(output).append("\n");
			}
			jsonData = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonData;
	}
}
