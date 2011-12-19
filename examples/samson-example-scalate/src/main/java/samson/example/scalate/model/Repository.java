package samson.example.scalate.model;

import java.math.BigDecimal;
import java.util.Calendar;
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
    private Map<Long, Customer> customers = new HashMap<Long, Customer>();
    private Map<Long, Order> orders = new HashMap<Long, Order>();

    private Repository() {

        // -- Product

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

        product = new Product();
        product.code = "0596801688";
        product.name = "RESTful Web Services Cookbook";
        product.price = BigDecimal.valueOf(23.17);
        createProduct(product);

        // -- Customer

        Customer customer = new Customer();
        customer.name = "George";
        createCustomer(customer);

        customer = new Customer();
        customer.name = "John";
        createCustomer(customer);

        customer = new Customer();
        customer.name = "Nick";
        createCustomer(customer);

        customer = new Customer();
        customer.name = "Andrew";
        createCustomer(customer);

        // -- Order

        Calendar cal = Calendar.getInstance();
        cal.set(2011, 8 - 1, 23);

        Order order = new Order();
        order.customer = findCustomerByName("George");
        order.code = "001";
        order.orderDate = cal.getTime();
        order.status = OrderStatus.NEW;

        OrderItem item = new OrderItem();
        item.order = order;
        item.product = findProductByCode("0596529260");
        item.qty = 10;
        order.items.add(item);

        item = new OrderItem();
        item.order = order;
        item.product = findProductByCode("0596158041");
        item.qty = 2;
        order.items.add(item);

        item = new OrderItem();
        item.order = order;
        item.product = findProductByCode("0596801688");
        item.qty = 15;
        order.items.add(item);

        createOrder(order);

        order = new Order();
        order.customer = findCustomerByName("John");
        order.code = "002";
        order.status = OrderStatus.NEW;

        item = new OrderItem();
        item.order = order;
        item.product = findProductByCode("0596529260");
        item.qty = 1;
        order.items.add(item);

        createOrder(order);
    }

    // -- Product

    public Collection<Product> getProducts() {
        return products.values();
    }

    public Product findProduct(Long id) {
        return products.get(id);
    }

    public Product findProductByCode(String code) {
        for (Product p : products.values()) {
            if (p.code.equals(code)) {
                return p;
            }
        }
        return null;
    }

    public Long createProduct(Product product) {
        Long id = seq.getAndIncrement();
        product.id = id;
        saveProduct(id, product);
        return id;
    }

    public void updateProduct(Long id, Product product) {
        product.id = id;
        saveProduct(id, product);
    }

    private void saveProduct(Long id, Product product) {
        products.put(id, product);
    }

    // -- Customer

    public Collection<Customer> getCustomers() {
        return customers.values();
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers.values()) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Long createCustomer(Customer customer) {
        Long id = seq.getAndIncrement();
        customer.id = id;
        customers.put(id, customer);
        return id;
    }

    // -- Order

    public Collection<Order> getOrders() {
        return orders.values();
    }

    public Order findOrder(Long id) {
        Order order = orders.get(id);
        fetchOrderCustomer(order);
        for (OrderItem item : order.items) {
            fetchOrderItemProduct(item);
        }
        return order;
    }

    private Order fetchOrderCustomer(Order order) {
        order.customer = customers.get(order.customer.id);
        return order;
    }

    private OrderItem fetchOrderItemProduct(OrderItem item) {
        item.product = products.get(item.product.id);
        return item;
    }

    public Long createOrder(Order order) {
        Long id = seq.getAndIncrement();
        order.id = id;
        saveOrder(id, order);
        return id;
    }

    public void updateOrder(Long id, Order order) {
        order.id = id;
        saveOrder(id, order);
    }

    private void saveOrder(Long id, Order order) {
        checkNotNull(order);
        checkNotNull(order.customer);
        checkNotNull(order.customer.id);
        checkForeignKey(customers, order.customer.id);

        for (OrderItem item : order.items) {
            item.order = order;

            checkNotNull(item);
            checkNotNull(item.product);
            checkNotNull(item.product.id);
            checkForeignKey(products, item.product.id);

            checkNotNull(item.order);
            checkNotNull(item.order.id);
            checkState(item.order.id.equals(id));
        }

        orders.put(id, order);
    }

    // -- Constraints

    public static <T> T checkNotNull(T ref) {
        if (ref == null) {
            throw new NullPointerException();
        }
        return ref;
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkForeignKey(Map<?,?> table, Object key) {
        if (key == null) {
            return;
        }
        if (!table.containsKey(key)) {
            throw new IllegalStateException("Foreign constraint violation");
        }
    }

}
