package samson.example.scalate.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItem {

    public Order order;

    @NotNull
    @NotNullId(message = "must select a product")
    public Product product;

    @NotNull
    @Min(1L)
    public Integer qty;

}
