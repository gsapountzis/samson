package samson.example.scalate.model;

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

}
