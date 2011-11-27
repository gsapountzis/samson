package samson.example.jsp.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItem {

    public Order order;

    public Product product;

    @NotNull
    @Min(1L)
    public Integer qty;

}
