package samson.example.scalate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Order {

    public Long id;

    @NotNull
    public Customer customer;

    @NotEmpty
    public String code;

    public Date orderDate = new Date();

    public Date shipDate;

    public OrderStatus status;

    @NotNull
    @Size(min = 1, message = "Order must contain at least one item")
    @Valid
    public List<OrderItem> items = new ArrayList<OrderItem>();

}
