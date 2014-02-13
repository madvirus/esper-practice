package esper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StockJsonParser {
    public StockJsonParser() {
    }

    public List<StockTick> parseJson(String jsonData) {
        ObjectMapper m = new ObjectMapper();
        m.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            JsonNode rootNode = m.readTree(jsonData);
            JsonNode listNode = rootNode.get("list");
            Iterator<JsonNode> listItems = listNode.elements();
            List<StockTick> stockTicks = new ArrayList<StockTick>(3000);
            while (listItems.hasNext()) {
                JsonNode listItem = listItems.next();
                JsonNode itemNode = listItem.get("item");
                Iterator<JsonNode> items = itemNode.elements();
                while (items.hasNext()) {
                    JsonNode item = items.next();
                    stockTicks.add(createStock(item));
                }
            }
            return stockTicks;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNodeValue(JsonNode item, String nodeName) {
        String value = item.get(nodeName).toString();
        return value.substring(1, value.length() - 1);
    }

    private StockTick createStock(JsonNode item) {
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
            } else if (updn.startsWith("&nbsp;")) {
                fluctuation = decimalFormat.parse(updn.substring(6)).intValue();
            }
            String rate = getNodeValue(item, "rate");
            double rateValue = 0.0;
            if (!rate.equals("0.00%")) {
                DecimalFormat decimalFormat2 = new DecimalFormat("+#,##0.00;-#,##0.00");
                rateValue = decimalFormat2.parse(rate.substring(0, rate.length() - 1)).doubleValue();
            }
            return new StockTick(code, name, cost, fluctuation, rateValue);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}