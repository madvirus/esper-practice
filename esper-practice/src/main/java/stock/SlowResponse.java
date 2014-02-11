package stock;

public class SlowResponse {
    private AccessLog accessLog;

    public SlowResponse(AccessLog accessLog) {
        this.accessLog = accessLog;
    }

    public AccessLog getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(AccessLog accessLog) {
        this.accessLog = accessLog;
    }
}
