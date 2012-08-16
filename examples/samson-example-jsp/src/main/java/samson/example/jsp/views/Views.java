package samson.example.jsp.views;

import java.util.Collection;
import java.util.Map;

import samson.example.jsp.model.Order;
import samson.example.jsp.model.Product;
import samson.form.SamsonForm;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class Products {

        /** Tuple */
        public static class ListModel {
            public final Collection<Product> products;

            ListModel(Collection<Product> products) {
                this.products = products;
            }

            public Collection<Product> getProducts() {
                return products;
            }
        }

        public static class CreateModel {
            public final SamsonForm<Product> productForm;

            CreateModel(SamsonForm<Product> productForm) {
                this.productForm = productForm;
            }

            public SamsonForm<Product> getProductForm() {
                return productForm;
            }
        }

        public static class ViewModel {
            public final Long id;
            public final Product product;

            ViewModel(Long id, Product product) {
                this.id = id;
                this.product = product;
            }

            public Long getId() {
                return id;
            }

            public Product getProduct() {
                return product;
            }
        }

        public static class EditModel {
            public final Long id;
            public final SamsonForm<Product> productForm;

            EditModel(Long id, SamsonForm<Product> productForm) {
                this.id = id;
                this.productForm = productForm;
            }

            public Long getId() {
                return id;
            }

            public SamsonForm<Product> getProductForm() {
                return productForm;
            }
        }

        /** Static factory method */
        public static Viewable list(Collection<Product> products) {
            return new Viewable("/products/list", new ListModel(products));
        }

        public static Viewable create(SamsonForm<Product> productForm) {
            return new Viewable("/products/create", new CreateModel(productForm));
        }

        public static Viewable view(Long id, Product product) {
            return new Viewable("/products/view", new ViewModel(id, product));
        }

        public static Viewable edit(Long id, SamsonForm<Product> productForm) {
            return new Viewable("/products/edit", new EditModel(id, productForm));
        }

    }

    public static class Orders {

        /** Tuple */
        public static class ListModel {
            public final Collection<Order> orders;

            ListModel(Collection<Order> orders) {
                this.orders = orders;
            }

            public Collection<Order> getOrders() {
                return orders;
            }
        }

        public static class CreateModel {
            public final SamsonForm<Order> orderForm;
            public final Map<String, String> customerOptions;
            public final Map<String, String> productOptions;

            CreateModel(SamsonForm<Order> orderForm,
                    Map<String, String> customerOptions,
                    Map<String, String> productOptions)
            {
                this.orderForm = orderForm;
                this.customerOptions = customerOptions;
                this.productOptions = productOptions;
            }

            public SamsonForm<Order> getOrderForm() {
                return orderForm;
            }

            public Map<String, String> getCustomerOptions() {
                return customerOptions;
            }

            public Map<String, String> getProductOptions() {
                return productOptions;
            }
        }

        public static class ViewModel {
            public final Long id;
            public final Order order;

            ViewModel(Long id, Order order) {
                this.id = id;
                this.order = order;
            }

            public Long getId() {
                return id;
            }

            public Order getOrder() {
                return order;
            }
        }

        public static class EditModel {
            public final Long id;
            public final SamsonForm<Order> orderForm;
            public final Map<String, String> customerOptions;
            public final Map<String, String> productOptions;

            EditModel(Long id, SamsonForm<Order> orderForm,
                    Map<String, String> customerOptions,
                    Map<String, String> productOptions)
            {
                this.id = id;
                this.orderForm = orderForm;
                this.customerOptions = customerOptions;
                this.productOptions = productOptions;
            }

            public Long getId() {
                return id;
            }

            public SamsonForm<Order> getOrderForm() {
                return orderForm;
            }

            public Map<String, String> getCustomerOptions() {
                return customerOptions;
            }

            public Map<String, String> getProductOptions() {
                return productOptions;
            }
        }

        /** Static factory method */
        public static Viewable list(Collection<Order> orders) {
            return new Viewable("/orders/list", new ListModel(orders));
        }

        public static Viewable create(SamsonForm<Order> orderForm,
                Map<String, String> customerOptions,
                Map<String, String> productOptions)
        {
            return new Viewable("/orders/create", new CreateModel(orderForm, customerOptions, productOptions));
        }

        public static Viewable view(Long id, Order order) {
            return new Viewable("/orders/view", new ViewModel(id, order));
        }

        public static Viewable edit(Long id, SamsonForm<Order> orderForm,
                Map<String, String> customerOptions,
                Map<String, String> productOptions)
        {
            return new Viewable("/orders/edit", new EditModel(id, orderForm, customerOptions, productOptions));
        }

    }

}
