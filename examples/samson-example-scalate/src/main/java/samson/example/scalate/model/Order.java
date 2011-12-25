package samson.example.scalate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Order implements Identifiable<Long> {

    public Long id;

    @NotNull
    @ValidId
    public Customer customer;

    @NotEmpty
    public String code;

    public Date orderDate = new Date();

    public Date shipDate;

    public OrderStatus status;

    @NotNull
    @Valid
    @Size(min = 1, message = "order must contain at least one item")
    public List<OrderItem> items = new ArrayList<OrderItem>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isIdValid() {
        return (id != null) && (id > 0);
    }

}
