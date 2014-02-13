package esper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class StockDataLoader {
    private final StockJsonParser stockJsonParser = new StockJsonParser();

    public List<StockTick> loadKospi() {
        return loadAndParseJsonStock("http://esper.daum.net/xml/xmlallpanel.daum?stype=P&type=U");
    }

    public List<StockTick> loadKosdaq() {
        return loadAndParseJsonStock("http://esper.daum.net/xml/xmlallpanel.daum?stype=Q&type=U");

    }

    private List<StockTick> loadAndParseJsonStock(String uri) {
        String jsonData = loadJsonStockData(uri);
        return stockJsonParser.parseJson(jsonData);
    }

    private String loadJsonStockData(String uri) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(uri);
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
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}