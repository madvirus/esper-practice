package esper;

public class OvertimeResponse extends MonitorResponse {

    public OvertimeResponse(String url) {
        super(url, -1);
    }

    @Override
    public String toString() {
        return "OvertimeResponse[" +
                "url='" + getUrl() + '\'' +
                ", responseTime=" + getResponseTime() +
                ']';
    }
}
