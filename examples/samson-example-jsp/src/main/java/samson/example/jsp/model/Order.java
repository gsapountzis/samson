package samson.example.jsp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

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

    public List<OrderItem> items = new ArrayList<OrderItem>();

    // -- Accessors

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCode() {
        return code;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

}
