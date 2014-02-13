package esper;

public class SlowResponse {
    private String url;
    private long responseTime;

    public SlowResponse(String url, long responseTime) {
        this.url = url;
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public long getResponseTime() {
        return responseTime;
    }

    @Override
    public String toString() {
        return "SlowResponse[" +
                "url='" + url + '\'' +
                ", responseTime=" + responseTime +
                ']';
    }
}
