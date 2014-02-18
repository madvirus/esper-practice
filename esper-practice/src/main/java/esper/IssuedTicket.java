package esper;

public class IssuedTicket {
    private String id;
    private String couponId;

    public IssuedTicket(String id, String couponId) {
        this.id = id;
        this.couponId = couponId;
    }

    public String getId() {
        return id;
    }

    public String getCouponId() {
        return couponId;
    }

    @Override
    public String toString() {
        return "IssuedTicket{" +
                "id='" + id + '\'' +
                ", couponId='" + couponId + '\'' +
                '}';
    }
}
