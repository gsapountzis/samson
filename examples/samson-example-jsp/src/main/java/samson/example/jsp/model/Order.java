package samson.example.jsp.model;

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
    public Long customerId;

    public Customer customer;

    @NotEmpty
    public String code;

    public Date orderDate = new Date();

    public Date shipDate;

    public OrderStatus status;

    @NotNull
    @Size(min = 1, message = "order must contain at least one item")
    @Valid
    public List<OrderItem> items = new ArrayList<OrderItem>();

    // -- Accessors

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
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

    public Date getShipDate() {
        return shipDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

}
