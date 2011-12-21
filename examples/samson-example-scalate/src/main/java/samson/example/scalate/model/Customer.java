package samson.example.scalate.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Customer implements Identifiable<Long> {

    public Long id;

    @NotEmpty
    @Size(max = 80)
    public String name;

    @Override
    public Long getId() {
        return id;
    }

}
