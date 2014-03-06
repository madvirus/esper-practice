package esper;

public class AccessLog {
    private String url;
    private long responseTime;
    private long accessTime;

    public AccessLog(String url, long responseTime) {
        this.url = url;
        this.responseTime = responseTime;
    }

    public AccessLog(String url, long accessTime, long responseTime) {
        this.url = url;
        this.accessTime = accessTime;
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public long getAccessTime() {
        return accessTime;
    }

    @Override
    public String toString() {
        return "AccessLog{" +
                "url='" + url + '\'' +
                ", responseTime=" + responseTime +
                ", accessTime=" + accessTime +
                '}';
    }
}
