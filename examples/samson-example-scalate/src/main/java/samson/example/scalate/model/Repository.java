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
        products.put(id, product);
        return id;
    }

    public void updateProduct(Long id, Product product) {
        product.id = id;
        products.put(id, product);
    }

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

    public Collection<Order> getOrders() {
        return orders.values();
    }

    public Order findOrder(Long id) {
        return orders.get(id);
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

    public void saveOrder(Long id, Order order) {
        if (order == null) {
            throw new IllegalArgumentException();
        }
        if (order.customer == null) {
            throw new IllegalArgumentException();
        }
        if (order.customer.id == null) {
            throw new IllegalArgumentException();
        }

        Customer customer = customers.get(order.customer.id);
        if (customer == null) {
            throw new RuntimeException("Foreign constraint violation");
        }
        order.customer = customer;

        order.id = id;
        orders.put(id, order);
    }

}
