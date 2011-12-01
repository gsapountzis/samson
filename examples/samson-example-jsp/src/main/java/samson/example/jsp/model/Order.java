package samson.example.jsp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

    public Long id;

    public Customer customer;

    public String code;

    public Date placementDate;

    public OrderStatus status;

    public List<OrderItem> items = new ArrayList<OrderItem>();

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCode() {
        return code;
    }

    public Date getPlacementDate() {
        return placementDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

}
