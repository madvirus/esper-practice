package esper;

public class ProductView {
    private String name;
    private Long productId;
    private String userId;

    public ProductView(String name, Long productId, String userId) {
        this.name = name;
        this.productId = productId;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public Long getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ProductView[" +
                "name='" + name + '\'' +
                ", productId=" + productId +
                ", userId='" + userId + '\'' +
                ']';
    }
}
