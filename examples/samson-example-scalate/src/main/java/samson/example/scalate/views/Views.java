package samson.example.scalate.views;

import java.util.Collection;

import samson.example.scalate.model.Order;
import samson.example.scalate.model.Product;
import samson.example.scalate.resources.OrdersResource;
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
        }

        public static class CreateModel {
            public final SamsonForm<Product> productForm;

            CreateModel(SamsonForm<Product> productForm) {
                this.productForm = productForm;
            }
        }

        public static class ViewModel {
            public final Long id;
            public final Product product;

            ViewModel(Long id, Product product) {
                this.id = id;
                this.product = product;
            }
        }

        public static class EditModel {
            public final Long id;
            public final SamsonForm<Product> productForm;

            EditModel(Long id, SamsonForm<Product> productForm) {
                this.id = id;
                this.productForm = productForm;
            }
        }

        /** Static factory method */
        public static Viewable list(Collection<Product> products) {
            return new Viewable("/WEB-INF/views/products/list", new ListModel(products));
        }

        public static Viewable create(SamsonForm<Product> productForm) {
            return new Viewable("/WEB-INF/views/products/create", new CreateModel(productForm));
        }

        public static Viewable view(Long id, Product product) {
            return new Viewable("/WEB-INF/views/products/view", new ViewModel(id, product));
        }

        public static Viewable edit(Long id, SamsonForm<Product> productForm) {
            return new Viewable("/WEB-INF/views/products/edit", new EditModel(id, productForm));
        }

    }

    public static class Orders {

        /** Tuple */
        public static class ListModel {
            public final Collection<Order> orders;

            ListModel(Collection<Order> orders) {
                this.orders = orders;
            }
        }

        public static class CreateModel {
            public final OrdersResource resource;
            public final SamsonForm<Order> orderForm;

            CreateModel(OrdersResource resource, SamsonForm<Order> orderForm) {
                this.resource = resource;
                this.orderForm = orderForm;
            }
        }

        public static class ViewModel {
            public final Long id;
            public final Order order;

            ViewModel(Long id, Order order) {
                this.id = id;
                this.order = order;
            }
        }

        public static class EditModel {
            public final OrdersResource resource;
            public final Long id;
            public final SamsonForm<Order> orderForm;

            EditModel(OrdersResource resource, Long id, SamsonForm<Order> orderForm) {
                this.resource = resource;
                this.id = id;
                this.orderForm = orderForm;
            }
        }

        /** Static factory method */
        public static Viewable list(Collection<Order> orders) {
            return new Viewable("/WEB-INF/views/orders/list", new ListModel(orders));
        }

        public static Viewable create(OrdersResource resource, SamsonForm<Order> orderForm) {
            return new Viewable("/WEB-INF/views/orders/create", new CreateModel(resource, orderForm));
        }

        public static Viewable view(Long id, Order order) {
            return new Viewable("/WEB-INF/views/orders/view", new ViewModel(id, order));
        }

        public static Viewable edit(OrdersResource resource, Long id, SamsonForm<Order> orderForm) {
            return new Viewable("/WEB-INF/views/orders/edit", new EditModel(resource, id, orderForm));
        }

    }

}
