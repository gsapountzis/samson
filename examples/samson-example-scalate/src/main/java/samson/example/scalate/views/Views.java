package samson.example.scalate.views;

import java.util.Collection;

import samson.JForm;
import samson.example.scalate.model.Order;
import samson.example.scalate.model.Product;
import samson.example.scalate.resources.OrdersResource;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class Products {

        public static Viewable list(Collection<Product> products) {
            Model model = new Model(null, null, null, products);
            return new Viewable("/WEB-INF/views/products/list", model);
        }

        public static Viewable create(JForm<Product> productForm) {
            Model model = new Model(null, null, productForm, null);
            return new Viewable("/WEB-INF/views/products/create", model);
        }

        public static Viewable view(Long id, Product product) {
            Model model = new Model(id, product, null, null);
            return new Viewable("/WEB-INF/views/products/view", model);
        }

        public static Viewable edit(Long id, JForm<Product> productForm) {
            Model model = new Model(id, null, productForm, null);
            return new Viewable("/WEB-INF/views/products/edit", model);
        }

        /** Tuple */
        public static class Model {
            public final Long id;
            public final Product product;
            public final JForm<Product> productForm;
            public final Collection<Product> products;

            Model(Long id, Product product, JForm<Product> productForm, Collection<Product> products) {
                this.id = id;
                this.product = product;
                this.productForm = productForm;
                this.products = products;
            }

        }

    }

    public static class Orders {

        public static Viewable list(Collection<Order> orders) {
            Model model = new Model(null, null, null, null, orders);
            return new Viewable("/WEB-INF/views/orders/list", model);
        }

        public static Viewable create(OrdersResource resource, JForm<Order> orderForm) {
            Model model = new Model(resource, null, null, orderForm, null);
            return new Viewable("/WEB-INF/views/orders/create", model);
        }

        public static Viewable view(Long id, Order order) {
            Model model = new Model(null, id, order, null, null);
            return new Viewable("/WEB-INF/views/orders/view", model);
        }

        public static Viewable edit(OrdersResource resource, Long id, JForm<Order> orderForm) {
            Model model = new Model(resource, id, null, orderForm, null);
            return new Viewable("/WEB-INF/views/orders/edit", model);
        }

        /** Tuple */
        public static class Model {
            public final OrdersResource resource;
            public final Long id;
            public final Order order;
            public final JForm<Order> orderForm;
            public final Collection<Order> orders;

            Model(OrdersResource resource, Long id, Order order, JForm<Order> orderForm, Collection<Order> orders) {
                this.resource = resource;
                this.id = id;
                this.order = order;
                this.orderForm = orderForm;
                this.orders = orders;
            }

        }

    }

}
