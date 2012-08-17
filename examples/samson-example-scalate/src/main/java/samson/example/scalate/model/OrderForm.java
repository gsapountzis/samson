package samson.example.scalate.model;

import java.util.HashMap;
import java.util.Map;

import samson.form.FormNode;
import samson.form.SamsonForm;

public class OrderForm extends SamsonForm<Order> {

    public OrderForm(SamsonForm<Order> orderForm) {
        super(orderForm.getNode(), orderForm.get());
    }

    /**
     * Validate form for duplicate items.
     * <p>
     * Implemented as a subclass of <code>SamsonForm&lt;Order&gt;</code> in order to
     * demonstrate usage of the {@link SamsonForm} interface. Alternatively, it could
     * be implemented as a {@link javax.validation.Constraint}.
     */
    public OrderForm validate() {
        Order order = super.get();
        if (order == null) {
            return this;
        }

        FormNode itemsNode = super.getNode().path("items");

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        boolean duplicate = false;

        int i = 0;
        for (OrderItem item : order.items) {

            Long productId = (item != null) ? item.productId : null;
            if (productId != null) {
                if (map.containsKey(productId)) {
                    duplicate = true;
                    Integer first = map.get(productId);
                    if (first != null) {
                        map.put(productId, null);
                        itemsNode.path(first).path("product").error("first item");
                    }
                    itemsNode.path(i).path("product").error("duplicate item");
                }
                else {
                    map.put(productId, i);
                }
            }
            else {
                itemsNode.path(i).path("product").error("must select a product");
            }

            i += 1;
        }

        if (duplicate) {
            itemsNode.error("order contains duplicate items");
        }

        return this;
    }
}
