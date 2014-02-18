package esper;

public class NormalResponse extends MonitorResponse {

    public NormalResponse(String url, long responseTime) {
        super(url, responseTime);
    }

    @Override
    public String toString() {
        return "NormalResponse[" +
                "url='" + getUrl() + '\'' +
                ", responseTime=" + getResponseTime() +
                ']';
    }
}
