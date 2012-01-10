package samson.example.jsp.views;

import java.util.Collection;

import samson.JForm;
import samson.example.jsp.model.Product;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class Products {

        public static Viewable list(Collection<Product> products) {
            Model model = new Model(null, null, null, products);
            return new Viewable("/products/list", model);
        }

        public static Viewable create(JForm<Product> productForm) {
            Model model = new Model(null, null, productForm, null);
            return new Viewable("/products/create", model);
        }

        public static Viewable view(Long id, Product product) {
            Model model = new Model(id, product, null, null);
            return new Viewable("/products/view", model);
        }

        public static Viewable edit(Long id, JForm<Product> productForm) {
            Model model = new Model(id, null, productForm, null);
            return new Viewable("/products/edit", model);
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

            public Long getId() {
                return id;
            }

            public Product getProduct() {
                return product;
            }

            public JForm<Product> getProductForm() {
                return productForm;
            }

            public Collection<Product> getProducts() {
                return products;
            }

        }

    }

}
