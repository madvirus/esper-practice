package esper;

public class MonitorResponse {
    protected String url;
    protected long responseTime;

    public MonitorResponse(String url, long responseTime) {
        this.url = url;
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public long getResponseTime() {
        return responseTime;
    }
}
