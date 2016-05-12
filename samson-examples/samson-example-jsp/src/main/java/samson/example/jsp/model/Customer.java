package samson.example.jsp.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Customer {

    public Long id;

    @NotEmpty
    @Size(max = 80)
    public String name;

    // -- Accessors

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
