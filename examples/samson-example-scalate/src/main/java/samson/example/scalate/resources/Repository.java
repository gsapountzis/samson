package samson.example.scalate.resources;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import samson.example.scalate.domain.Product;

public class Repository {

    private static final Repository INSTANCE = new Repository();

    public static Repository get() {
        return INSTANCE;
    }

    private final AtomicLong seq = new AtomicLong(1);

    private Map<Long, Product> products = new HashMap<Long, Product>();

    private Repository() {
        Product product;

        product = new Product();
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

}
