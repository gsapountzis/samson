package samson.example.scalate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import samson.JForm;

public class OrderForm extends ForwardingForm<Order> {

    private final JForm<Order> orderForm;

    public OrderForm(JForm<Order> delegate) {
        super(delegate);
        this.orderForm = delegate;
    }

    /**
     * Validate form for duplicate items.
     * <p>
     * Implemented as a subclass of <code>JForm&lt;Order&gt;</code> in order to
     * demonstrate usage of the {@link JForm} interface. Alternatively, it could
     * be implemented as a {@link javax.validation.Constraint}.
     */
    public OrderForm validate() {
        Order order = orderForm.get(); // XXX check not null
        boolean duplicates = false;
        Map<Long, List<Integer>> map = new HashMap<Long, List<Integer>>();

        int i = 0;
        for (OrderItem item : order.items) {
            Long id = item.product.id; // XXX check not null

            List<Integer> indices = map.get(id);
            if (indices == null) {
                indices = new ArrayList<Integer>();
                map.put(id, indices);
            }
            else {
                duplicates = true;
            }
            indices.add(i);

            i += 1;
        }

        if (duplicates) {
            JForm<?> itemsForm = orderForm.path("items");
            itemsForm.error("Order contains duplicate items");

            for (Long id : map.keySet()) {
                List<Integer> indices = map.get(id);
                if (indices.size() > 1) {
                    for (Integer index : indices) {
                        itemsForm.index(index).error("duplicate item");
                    }
                }

            }
        }

        return this;
    }
}
