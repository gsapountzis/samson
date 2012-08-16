package samson.example.jsp.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import samson.example.jsp.model.Order;
import samson.example.jsp.model.Product;
import samson.form.SamsonForm;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class Products {

        /** Static factory method */
        public static Viewable list(Collection<Product> products) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("products", products);
            model.put("body", "/WEB-INF/views/products/list.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable create(SamsonForm<Product> productForm) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("productForm", productForm);
            model.put("body", "/WEB-INF/views/products/create.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable view(Long id, Product product) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("product", product);
            model.put("body", "/WEB-INF/views/products/view.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable edit(Long id, SamsonForm<Product> productForm) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("productForm", productForm);
            model.put("body", "/WEB-INF/views/products/edit.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
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
