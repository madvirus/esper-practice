package esper;

public class UseTicketCoupon {
    private String couponId;

    public UseTicketCoupon(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponId() {
        return couponId;
    }

    @Override
    public String toString() {
        return "UseTicketCoupon{" +
                "couponId='" + couponId + '\'' +
                '}';
    }
}
