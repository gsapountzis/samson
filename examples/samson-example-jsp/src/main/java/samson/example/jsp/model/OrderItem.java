package samson.example.jsp.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItem {

    @NotNull
    public Order order;

    @NotNull
    public Product product;

    @NotNull
    @Min(1L)
    public Integer qty;

}