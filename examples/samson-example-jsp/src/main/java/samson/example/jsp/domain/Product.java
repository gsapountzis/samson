package samson.example.jsp.domain;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class Product {

    public Long id;

    @NotEmpty
    public String code;

    public String name;

    @NotNull
    @DecimalMin("0.01")
    public BigDecimal price;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

}
