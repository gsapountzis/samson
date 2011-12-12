package samson.example.jsp.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Repository {

    private static final Repository INSTANCE = new Repository();

    public static Repository get() {
        return INSTANCE;
    }

    private final AtomicLong seq = new AtomicLong(1);

    private Map<Long, Product> products = new HashMap<Long, Product>();
    private Map<Long, Order> orders = new HashMap<Long, Order>();

    private Repository() {
        Product product = new Product();
        product.code = "0596529260";
        product.name = "Restful Web Services";
        product.price = BigDecimal.valueOf(24.34);
        createProduct(product);

        product = new Product();
        product.code = "0596158041";
        product.name = "RESTful Java with JAX-RS";
        product.price = BigDecimal.valueOf(26.39);
        createProduct(product);
    }

    public Collection<Product> getProducts() {
        return products.values();
    }

    public Product findProduct(Long id) {
        return products.get(id);
    }

    public Long createProduct(Product product) {
        Long id = seq.getAndIncrement();
        product.id = id;
        products.put(id, product);
        return id;
    }

    public void updateProduct(Long id, Product product) {
        product.id = id;
        products.put(id, product);
    }

    public Order findOrder(Long id) {
        return orders.get(id);
    }

    public Long createOrder(Order order) {
        Long id = seq.getAndIncrement();
        order.id = id;
        orders.put(id, order);
        return id;
    }

}
