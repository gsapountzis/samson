package samson.example.jsp.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import samson.JForm;
import samson.example.jsp.domain.Order;
import samson.example.jsp.domain.Product;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class products {
        public static Viewable list(Collection<Product> products) {
            return new Viewable("/WEB-INF/views/products/list.jsp", products);
        }

        public static Viewable create(JForm<Product> productForm) {
            return new Viewable("/WEB-INF/views/products/create.jsp", productForm);
        }

        public static Viewable view(Long id, Product product) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("product", product);
            return new Viewable("/WEB-INF/views/products/view.jsp", model);
        }

        public static Viewable edit(Long id, JForm<Product> productForm) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("form", productForm);
            return new Viewable("/WEB-INF/views/products/edit.jsp", model);
        }
    }

    public static class orders {
        public static Viewable create(JForm<Order> orderForm) {
            return new Viewable("/WEB-INF/views/orders/create.jsp", orderForm);
        }

        public static Viewable view(Long id, Order order) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", id);
            model.put("order", order);
            return new Viewable("/WEB-INF/views/orders/view.jsp", model);
        }
    }

}
