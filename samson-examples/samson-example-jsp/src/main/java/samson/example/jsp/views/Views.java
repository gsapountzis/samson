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

        /** Static factory method */
        public static Viewable list(Collection<Order> orders) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("orders", orders);
            model.put("body", "/WEB-INF/views/orders/list.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable create(SamsonForm<Order> orderForm,
                Map<String, String> customerOptions,
                Map<String, String> productOptions)
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("orderForm", orderForm);
            model.put("customerOptions", customerOptions);
            model.put("productOptions", productOptions);
            model.put("body", "/WEB-INF/views/orders/create.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable view(Long id, Order order) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("order", order);
            model.put("body", "/WEB-INF/views/orders/view.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

        public static Viewable edit(Long id, SamsonForm<Order> orderForm,
                Map<String, String> customerOptions,
                Map<String, String> productOptions)
        {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("orderForm", orderForm);
            model.put("customerOptions", customerOptions);
            model.put("productOptions", productOptions);
            model.put("body", "/WEB-INF/views/orders/edit.jsp");
            model.put("styles", "/WEB-INF/views/orders/styles.jsp");
            model.put("scripts", "/WEB-INF/views/orders/scripts.jsp");
            return new Viewable("/WEB-INF/layouts/default", model);
        }

    }

}
