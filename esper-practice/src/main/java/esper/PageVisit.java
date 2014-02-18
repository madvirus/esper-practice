package esper;

public class PageVisit {
    private String name;
    private String userId;
    private String pageUrl;

    public PageVisit(String name, String userId, String pageUrl) {
        this.name = name;
        this.userId = userId;
        this.pageUrl = pageUrl;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}
