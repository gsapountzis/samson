package samson.example.scalate.model;

import java.util.HashMap;
import java.util.Map;

import samson.JForm;

public class OrderForm extends ForwardingForm<Order> {

    private final JForm<Order> orderForm;
    private final JForm<?> itemsForm;

    public OrderForm(JForm<Order> delegate) {
        super(delegate);
        this.orderForm = delegate;
        this.itemsForm = orderForm.path("items");
    }

    /**
     * Validate form for duplicate items.
     * <p>
     * Implemented as a subclass of <code>JForm&lt;Order&gt;</code> in order to
     * demonstrate usage of the {@link JForm} interface. Alternatively, it could
     * be implemented as a {@link javax.validation.Constraint}.
     */
    public OrderForm validate() {
        Order order = orderForm.get();
        if (order == null) {
            return this;
        }

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        boolean containsDup = false;

        int i = 0;
        for (OrderItem item : order.items) {

            Long id = (item != null && item.product != null) ? item.product.id : null;
            if (id != null) {
                if (map.containsKey(id)) {
                    containsDup = true;
                    Integer first = map.get(id);
                    if (first != null) {
                        map.put(id, null);
                        itemsForm.index(first).dot("product").error("first item");
                    }
                    itemsForm.index(i).dot("product").error("duplicate item");
                }
                else {
                    map.put(id, i);
                }
            }

            i += 1;
        }

        if (containsDup) {
            itemsForm.error("order contains duplicate items");
        }

        return this;
    }
}
