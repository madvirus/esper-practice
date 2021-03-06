package esper;

public class SlowResponse extends MonitorResponse {

    public SlowResponse(String url, long responseTime) {
        super(url, responseTime);
    }

    @Override
    public String toString() {
        return "SlowResponse[" +
                "url='" + getUrl() + '\'' +
                ", responseTime=" + getResponseTime() +
                ']';
    }
}
