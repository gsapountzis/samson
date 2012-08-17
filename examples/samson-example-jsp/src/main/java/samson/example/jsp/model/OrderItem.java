package samson.example.jsp.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItem {

    @NotNull
    public Long productId;

    public Product product;

    @NotNull
    @Min(1L)
    public Integer qty;

    // -- Accessors

    public Long getProductId() {
        return productId;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQty() {
        return qty;
    }

}
